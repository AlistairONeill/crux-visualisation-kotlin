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

    fun put(hex: Int) {
        val ret = cruxNode.submitTx {
            put(id) {
                add(cid to hex)
            }
        }

        validTimes.add(ret[tt] as Date)
        transactionTimes.add(ret[tt] as Date)
    }

    fun put(hex: Int, vt: Date) {
        val ret = cruxNode.submitTx {
            put(id) {
                add(cid to hex)
                validTime = vt
            }
        }

        validTimes.add(vt)
        transactionTimes.add(ret[tt] as Date)
    }

    fun put(hex: Int, vt: Date, evt: Date) {
        val ret = cruxNode.submitTx {
            put(id) {
                add(cid to hex)
                validTime = vt
                endValidTime = evt
            }
        }

        validTimes.add(vt)
        validTimes.add(evt)
        transactionTimes.add(ret[tt] as Date)
    }

    fun delete() {
        val ret = cruxNode.submitTx {
            delete(id)
        }

        validTimes.add(ret[tt] as Date)
        transactionTimes.add(ret[tt] as Date)
    }

    fun delete(vt: Date) {
        val ret = cruxNode.submitTx {
            delete(id) {
                validTime = vt
            }
        }

        validTimes.add(vt)
        transactionTimes.add(ret[tt] as Date)
    }

    fun delete(vt: Date, evt: Date) {
        val ret = cruxNode.submitTx {
            delete(id) {
                validTime = vt
                endValidTime = evt
            }
        }

        validTimes.add(vt)
        validTimes.add(evt)
        transactionTimes.add(ret[tt] as Date)
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