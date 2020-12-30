package main.model

import clojure.lang.Keyword
import crux.api.Crux
import crux.kotlin.projection.ICruxDataClass
import crux.kotlin.projection.annotation.CruxKey
import crux.kotlin.transactions.submitTx
import main.extension.kw
import java.awt.Color
import java.time.Duration
import java.util.*

class DataModel {
    companion object {
        private val id: Keyword = "id".kw
        private val cid: Keyword = "colour".kw
        private val tt: Keyword = "crux.tx/tx-time".kw
    }

    data class Colour(
        @property:CruxKey("colour") val colour: Int
    ): ICruxDataClass {
        override val cruxId = id
    }

    private var cruxNode = Crux.startNode()

    private val validTimes = HashSet<Date>()
    private val transactionTimes = HashSet<Date>()

    data class TransactionData(val type: Type, val hex: Int?, val transactionTime: Date, val validTime: Date, val endValidTime: Date?) {
        enum class Type {
            PUT, DELETE
        }
    }

    fun put(hex: Int): TransactionData {
        val ret = cruxNode.submitTx {
            put(id) {
                add(cid to hex)
            }
        }

        val tt = ret[tt] as Date
        validTimes.add(tt)
        transactionTimes.add(tt)
        return TransactionData(TransactionData.Type.PUT, hex, tt, tt, null)
    }

    fun put(hex: Int, vt: Date): TransactionData {
        val ret = cruxNode.submitTx {
            put(id) {
                add(cid to hex)
                validTime = vt
            }
        }

        val tt = ret[tt] as Date
        validTimes.add(vt)
        transactionTimes.add(tt)
        return TransactionData(TransactionData.Type.PUT, hex, tt, vt, null)
    }

    fun put(hex: Int, vt: Date, evt: Date): TransactionData {
        val ret = cruxNode.submitTx {
            put(id) {
                add(cid to hex)
                validTime = vt
                endValidTime = evt
            }
        }

        val tt = ret[tt] as Date
        validTimes.add(vt)
        validTimes.add(evt)
        transactionTimes.add(tt)
        return TransactionData(TransactionData.Type.PUT, hex, tt, vt, evt)
    }

    fun delete(): TransactionData {
        val ret = cruxNode.submitTx {
            delete(id)
        }

        val tt = ret[tt] as Date
        validTimes.add(tt)
        transactionTimes.add(tt)
        return TransactionData(TransactionData.Type.DELETE, null, tt, tt, null)
    }

    fun delete(vt: Date): TransactionData {
        val ret = cruxNode.submitTx {
            delete(id) {
                validTime = vt
            }
        }
        val tt = ret[tt] as Date
        validTimes.add(vt)
        transactionTimes.add(tt)
        return TransactionData(TransactionData.Type.DELETE, null, tt, vt, null)
    }

    fun delete(vt: Date, evt: Date): TransactionData {
        val ret = cruxNode.submitTx {
            delete(id) {
                validTime = vt
                endValidTime = evt
            }
        }

        val tt = ret[tt] as Date
        validTimes.add(vt)
        validTimes.add(evt)
        transactionTimes.add(tt)
        return TransactionData(TransactionData.Type.DELETE, null, tt, vt, evt)
    }

    data class DrawingData(val validTimes: List<Long>, val transactionTimes: List<Long>, val colours: List<List<Int?>>)

    fun getData(startOfDay: Long): DrawingData {
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

        return DrawingData(validTimes.map { it.time / 1000 - startOfDay}, transactionTimes.map { it.time/ 1000 - startOfDay }, colours)
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