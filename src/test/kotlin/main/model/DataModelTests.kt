package main.model

import clojure.lang.Keyword
import crux.api.*
import main.extension.kw
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.awt.Color
import java.time.Duration
import java.util.*
import java.util.function.Consumer

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataModelTests {
    private val real = DataModel(Crux.startNode())
    @Suppress("UNCHECKED_CAST")
    private val fake = DataModel(
        object: ICruxAPI {
            val transactionTimes = (0 .. 23).map { getTime(it) }
            var transactionIndex = 0

            val data = (0 .. 23).map { vtr ->
                getTime(vtr) to (0 .. 23).map { ttr ->
                    getTime(ttr) to (vtr * ttr)
                }.toMap()
            }.toMap()

            override fun close() { throw Exception() }
            override fun openTxLog(p0: Long?, p1: Boolean): ICursor<MutableMap<Keyword, *>> { throw Exception() }
            override fun db(): ICruxDatasource { throw Exception() }
            override fun db(p0: Date?): ICruxDatasource { throw Exception() }
            override fun db(p0: Date?, p1: Date?): ICruxDatasource { throw Exception() }
            override fun db(p0: MutableMap<Keyword, *>?): ICruxDatasource { throw Exception() }
            override fun openDB(): ICruxDatasource { throw Exception() }
            override fun openDB(p0: Date?): ICruxDatasource { throw Exception() }
            override fun openDB(p0: MutableMap<Keyword, *>?): ICruxDatasource { throw Exception() }
            override fun status(): MutableMap<Keyword, *> { throw Exception() }
            override fun hasTxCommitted(p0: MutableMap<Keyword, *>?): Boolean { throw Exception() }
            override fun awaitTxTime(p0: Date?, p1: Duration?): Date { throw Exception() }
            override fun awaitTx(p0: MutableMap<Keyword, *>?, p1: Duration?): MutableMap<Keyword, *> { throw Exception() }
            override fun listen(p0: MutableMap<Keyword, *>?, p1: Consumer<MutableMap<Keyword, *>>?): AutoCloseable { throw Exception() }
            override fun latestCompletedTx(): MutableMap<Keyword, *> { throw Exception() }
            override fun latestSubmittedTx(): MutableMap<Keyword, *> { throw Exception() }
            override fun attributeStats(): MutableMap<Keyword, Long> { throw Exception() }
            override fun activeQueries(): MutableList<IQueryState> { throw Exception() }
            override fun recentQueries(): MutableList<IQueryState> { throw Exception() }
            override fun slowestQueries(): MutableList<IQueryState> { throw Exception() }

            //Don't do anything meaningful
            override fun sync(p0: Duration?): Date { return Date() }

            //Just return a distinct tx time
            override fun submitTx(p0: MutableList<MutableList<*>>?): MutableMap<Keyword, *> {
                val ret = mapOf("crux.tx/tx-time".kw to transactionTimes[transactionIndex])
                transactionIndex += 1
                return ret as MutableMap<Keyword, *>
            }

            //Return an ICruxDatasource which delegates to our map
            override fun openDB(validTime: Date?, transactionTime: Date?): ICruxDatasource {
                if (validTime == null) throw Exception()
                if (transactionTime == null) throw Exception()

                return object: ICruxDatasource {
                    override fun close() { throw Exception() }
                    override fun entityTx(p0: Any?): MutableMap<Keyword, *> { throw Exception() }
                    override fun query(p0: Any?, vararg p1: Any?): MutableCollection<MutableList<*>> { throw Exception() }
                    override fun openQuery(p0: Any?, vararg p1: Any?): ICursor<MutableList<*>> { throw Exception() }
                    override fun entityHistory(p0: Any?, p1: HistoryOptions?): MutableList<MutableMap<Keyword, *>> { throw Exception() }
                    override fun openEntityHistory(p0: Any?, p1: HistoryOptions?): ICursor<MutableMap<Keyword, *>> { throw Exception() }
                    override fun validTime(): Date { throw Exception() }
                    override fun transactionTime(): Date { throw Exception() }
                    override fun dbBasis(): MutableMap<Keyword, *> { throw Exception() }
                    override fun withTx(p0: MutableList<MutableList<*>>?): ICruxDatasource { throw Exception() }

                    override fun entity(id: Any?): MutableMap<Keyword, Any> {
                        return mapOf(
                            "colour".kw to data[validTime]!![transactionTime]!!
                        ) as MutableMap<Keyword, Any>
                    }
                }
            }
        }
    ).apply {
        submit(TransactionRequestData(TransactionType.PUT, null, null, red))
        submit(TransactionRequestData(TransactionType.PUT, getTime(3), null, red))
        submit(TransactionRequestData(TransactionType.PUT, getTime(8), getTime(9), red))
        submit(TransactionRequestData(TransactionType.DELETE, null, null, red))
        submit(TransactionRequestData(TransactionType.DELETE, getTime(4), getTime(10), red))
        submit(TransactionRequestData(TransactionType.DELETE, getTime(7), getTime(15), red))
    }

    private fun getTime(hour: Int, minute: Int = 0, second: Int = 0) = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, second)
        set(Calendar.MILLISECOND, 0)
    }.time

    private val red = Color.RED.rgb
    private val validTime = getTime(12, 8, 19)
    private val endValidTime = getTime(13, 31, 9)

    private val expectedTransactionHours = (0 .. 5)
    private val expectedValidHours = listOf(0, 3, 4, 7, 8, 9, 10, 15)

    @Nested
    inner class ExpectedTransactions {
        @Nested
        inner class Put {
            @Test
            fun `No times specified`() {
                val request = TransactionRequestData(TransactionType.PUT, null, null, red)
                val result = real.submit(request)

                assertEquals(TransactionType.PUT, result.type)
                assertNotNull(result.transactionTime)
                assertEquals(result.transactionTime, result.validTime)
                assertNull(result.endValidTime)
                assertEquals(red, result.colour)
            }

            @Test
            fun `Valid time specified`() {
                val request = TransactionRequestData(TransactionType.PUT, validTime, null, red)
                val result = real.submit(request)

                assertEquals(TransactionType.PUT, result.type)
                assertNotNull(result.transactionTime)
                assertEquals(validTime, result.validTime)
                assertNull(result.endValidTime)
                assertEquals(red, result.colour)
            }

            @Test
            fun `Both times specified`() {
                val request = TransactionRequestData(TransactionType.PUT, validTime, endValidTime, red)
                val result = real.submit(request)

                assertEquals(TransactionType.PUT, result.type)
                assertNotNull(result.transactionTime)
                assertEquals(validTime, result.validTime)
                assertEquals(endValidTime, result.endValidTime)
                assertEquals(red, result.colour)
            }
        }

        @Nested
        inner class Delete {
            @Test
            fun `No times specified`() {
                val request = TransactionRequestData(TransactionType.DELETE, null, null, red)
                val result = real.submit(request)

                assertEquals(TransactionType.DELETE, result.type)
                assertNotNull(result.transactionTime)
                assertEquals(result.transactionTime, result.validTime)
                assertNull(result.endValidTime)
                assertNull(result.colour)
            }

            @Test
            fun `Valid time specified`() {
                val request = TransactionRequestData(TransactionType.DELETE, validTime, null, red)
                val result = real.submit(request)

                assertEquals(TransactionType.DELETE, result.type)
                assertNotNull(result.transactionTime)
                assertEquals(validTime, result.validTime)
                assertNull(result.endValidTime)
                assertNull(result.colour)
            }

            @Test
            fun `Both times specified`() {
                val request = TransactionRequestData(TransactionType.DELETE, validTime, endValidTime, red)
                val result = real.submit(request)

                assertEquals(TransactionType.DELETE, result.type)
                assertNotNull(result.transactionTime)
                assertEquals(validTime, result.validTime)
                assertEquals(endValidTime, result.endValidTime)
                assertNull(result.colour)
            }
        }

        @Nested
        inner class Evict {
            @Test
            fun `Evict works as expected`() {
                val request = TransactionRequestData(TransactionType.EVICT, validTime, endValidTime, red)
                val result = real.submit(request)

                assertEquals(TransactionType.EVICT, result.type)
                assertNotNull(result.transactionTime)
                assertNull(result.validTime)
                assertNull(result.endValidTime)
                assertNull(result.colour)
            }
        }
    }

    @Test
    fun `Data is retrieved as expected`() {
        val data = fake.getData()

        assertEquals(
            expectedTransactionHours.map { getTime(it) },
            data.transactionTimes
        )

        assertEquals(
            expectedValidHours.map { getTime(it) },
            data.validTimes
        )

        assertEquals(
            expectedTransactionHours.map { th ->
                expectedValidHours.map { vh ->
                    vh * th
                }
            },
            data.colours
        )
    }
}