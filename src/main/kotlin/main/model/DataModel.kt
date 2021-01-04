package main.model

import clojure.lang.Keyword
import crux.api.Crux
import crux.kotlin.transactions.submitTx
import main.extension.kw
import main.util.TodayDateFormat
import java.time.Duration
import java.util.*

class DataModel {
    companion object {
        private val id: Keyword = "id".kw
        private val cid: Keyword = "colour".kw
        private val tt: Keyword = "crux.tx/tx-time".kw
    }

    private var cruxNode = Crux.startNode()

    private val validTimes = HashSet<Date>()
    private val transactionTimes = HashSet<Date>()


    fun submit(transactionRequest: TransactionRequestData): TransactionData {
        return when (transactionRequest.type) {
            TransactionType.PUT -> put(transactionRequest.colour, transactionRequest.validTime, transactionRequest.endValidTime)
            TransactionType.DELETE -> delete(transactionRequest.validTime, transactionRequest.endValidTime)
            TransactionType.EVICT -> evict()
        }
    }

    private fun put(colour: Int, vt: Date?, evt: Date?): TransactionData {
        val ret = cruxNode.submitTx {
            put(id) {
                add(cid to colour)
                validTime = vt
                endValidTime = evt
            }
        }

        val tt = ret[tt] as Date
        validTimes.add(vt ?: tt)
        evt?.also { validTimes.add(it) }
        transactionTimes.add(tt)

        return TransactionData(
            TransactionType.PUT,
            tt,
            vt ?: tt,
            evt,
            colour
        )
    }

    fun delete(vt: Date?, evt: Date?): TransactionData {
        val ret = cruxNode.submitTx {
            delete(id) {
                validTime = vt
                endValidTime = evt
            }
        }

        val tt = ret[tt] as Date
        validTimes.add(vt ?: tt)
        evt?.also { validTimes.add(it) }
        transactionTimes.add(tt)

        return TransactionData(
            TransactionType.DELETE,
            tt,
            vt ?: tt,
            evt,
            null
        )
    }

    fun evict(): TransactionData {
        val ret = cruxNode.submitTx {
            evict(id)
        }

        val tt = ret[tt] as Date

        return TransactionData(
            TransactionType.EVICT,
            tt,
            null,
            null,
            null
        )
    }

    fun getData(): DrawingData {
        cruxNode.sync(Duration.ofSeconds(10))

        val validTimes = validTimes.toList().sorted()
        val transactionTimes = transactionTimes.toList().sorted()

        val colours = transactionTimes.map { tt ->
            validTimes.map { vt ->
                val entity = cruxNode.openDB(vt, tt).entity(id)
                if (entity == null) {
                    null
                }
                else {
                    entity["colour".kw] as Int
                }
            }
        }

        return DrawingData(validTimes, transactionTimes, colours)
    }

    fun reset() {
        try {
            cruxNode.close()
        }
        catch (e: Exception) {
            //meh
        }
        cruxNode = Crux.startNode()
    }
}