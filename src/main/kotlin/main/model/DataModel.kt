package main.model

import crux.api.CruxDocument
import crux.api.ICruxAPI
import crux.api.TransactionInstant
import crux.api.tx.submitTx
import java.time.Duration
import java.util.*

class DataModel(
    private val cruxNode: ICruxAPI
) {
    companion object {
        private val id = "id"
        private val cid = "colour"
    }

    private val validTimes = HashSet<Date>()
    private val transactionTimes = HashSet<Date>()

    fun submit(transactionRequest: TransactionRequestData): TransactionData {
        return when (transactionRequest.type) {
            TransactionType.PUT -> put(transactionRequest.colour, transactionRequest.validTime, transactionRequest.endValidTime)
            TransactionType.DELETE -> delete(transactionRequest.validTime, transactionRequest.endValidTime)
            TransactionType.EVICT -> evict()
        }
    }

    private fun put(colour: Int, validTime: Date?, endValidTime: Date?): TransactionData {
        val document = CruxDocument.build(id) {
            it.put(cid, colour)
        }


        val transactionTime = cruxNode.submitTx {
            when {
                validTime == null -> put(document)
                endValidTime == null -> put(document from validTime)
                else -> put(document from validTime until endValidTime)
            }
        }.let(TransactionInstant::getTime)

        validTimes.add(validTime ?: transactionTime)
        endValidTime?.also(validTimes::add)
        transactionTimes.add(transactionTime)

        return TransactionData(
            TransactionType.PUT,
            transactionTime,
            validTime ?: transactionTime,
            endValidTime,
            colour
        )
    }

    private fun delete(validTime: Date?, endValidTime: Date?): TransactionData {
        val transactionTime = cruxNode.submitTx {
            when {
                validTime == null -> delete(cid)
                endValidTime == null -> delete(cid from validTime)
                else -> delete(cid from validTime until endValidTime)
            }
        }.let(TransactionInstant::getTime)

        validTimes.add(validTime ?: transactionTime)
        endValidTime?.also { validTimes.add(it) }
        transactionTimes.add(transactionTime)

        return TransactionData(
            TransactionType.DELETE,
            transactionTime,
            validTime ?: transactionTime,
            endValidTime,
            null
        )
    }

    private fun evict(): TransactionData =
        cruxNode.submitTx {
            evict(id)
        }.let {
            TransactionData(
                TransactionType.EVICT,
                it.time,
                null,
                null,
                null
            )
        }

    fun getData(): DrawingData {
        cruxNode.sync(Duration.ofSeconds(10))

        val validTimes = validTimes.toList().sorted()
        val transactionTimes = transactionTimes.toList().sorted()

        val colours = transactionTimes.map { transactionTime ->
            validTimes.map { validTime ->
                cruxNode.openDB(validTime, transactionTime).entity(id)?.get("colour") as Int?
            }
        }

        return DrawingData(validTimes, transactionTimes, colours)
    }
}