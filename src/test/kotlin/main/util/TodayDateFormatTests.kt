package main.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.text.SimpleDateFormat
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodayDateFormatTests {
    private val now = Calendar.getInstance().time
    private val nowString = SimpleDateFormat("dd MMMM yyyy ").format(now)
    private val timeFormat = SimpleDateFormat("dd MMMM yyyy HH:mm:ss")

    private val testFormat = TodayDateFormat.getInstance()

    private fun getDateAfterEpoch(hours: Int, minutes: Int, seconds: Int): Date {
        val date = Date()
        date.time = 1000L * ((hours - 1) * 3600 + minutes * 60 + seconds)
        return date
    }

    private data class TestDatum(val date: Date, val fullString: String, val partialStrings: List<String>) {
        companion object {
            val full = SimpleDateFormat("HH:mm:ss")
            val minutes = SimpleDateFormat("HH:mm")
            val hours = SimpleDateFormat("HH")

            fun factory(hour: Int, minute: Int, second: Int): TestDatum {
                val date = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, second)
                    set(Calendar.MILLISECOND, 0)
                }.time

                val fullString = full.format(date)
                val partialStrings = ArrayList<String>()
                if (second == 0) {
                    partialStrings.add(minutes.format(date))
                    if (minute == 0) {
                        partialStrings.add(hours.format(date))
                    }
                }

                return TestDatum(date, fullString, partialStrings)
            }
        }
    }

    private val testData = listOf(
        TestDatum.factory(3,12,3),
        TestDatum.factory(12,0,29),
        TestDatum.factory(18, 29, 59),
        TestDatum.factory(21, 43, 0),
        TestDatum.factory(9, 3, 0),
        TestDatum.factory(18, 0, 0),
        TestDatum.factory(23, 0, 0)
    )

    @Nested
    inner class WorkingAsIntended {
        @Test
        fun `Correctly parses full time strings`() {
            for (testDatum in testData) {
                val parsed = testFormat.parse(testDatum.fullString)
                assertEquals(testDatum.date, parsed)
            }
        }

        @Test
        fun `Correctly formats full time strings`() {
            for (testDatum in testData) {
                val formatted = testFormat.format(testDatum.date)
                assertEquals(testDatum.fullString, formatted)
            }
        }

        @Test
        fun `Correctly parses partial time strings`() {
            for (testDatum in testData) {
                for (partial in testDatum.partialStrings) {
                    val parsed = testFormat.parse(partial)
                    assertEquals(testDatum.date, parsed)
                }
            }
        }
    }
}