package main.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class TodayDateFormat(
    private val startOfDay: Long) {
    companion object {
        val shared = getInstance()
        fun getInstance() = TodayDateFormat(
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }

    private val output = SimpleDateFormat("HH:mm:ss")

    fun parse(input: String): Date? {
        val seconds = seconds(input) ?: return null
        return Date.from(Instant.ofEpochMilli(startOfDay + seconds * 1000))
    }

    fun seconds(input: Date): Long {
        val time = input.time - startOfDay
        return time / 1000
    }

    private fun seconds(input: String): Long? {
        fun parseException() {
            throw Exception("Could not parse input time: $input")
        }

        if (input == "") return null

        val split = input.split(":").map { it.toInt() }
        if (split.size > 3) {
            parseException()
        }

        fun getPart(index: Int, multiplier: Int, max: Int): Int {
            if (index >= split.size) {
                return 0
            }

            val value = split[index]
            if (value < 0 || value > max) {
                parseException()
            }

            return value * multiplier
        }

        var time = 0L
        time += getPart(0, 3600, 23)
        time += getPart(1, 60, 59)
        time += getPart(2, 1, 59)
        return time
    }

    fun format(input: Date): String = output.format(input)
}