package main.presenter

import main.VisualisationFrame
import main.model.DataModel
import java.time.Instant
import java.util.*
import javax.swing.JOptionPane

class VisualisationPresenter(val visualisationFrame: VisualisationFrame) {
    data class Time(val hours: Int, val minutes: Int, val seconds: Int) {
        companion object {
            fun factory(raw: String): Time? {
                try {
                    val split = raw.split(":")

                    if (split.size > 3) {
                        return null
                    }

                    val hours = split[0].toInt()

                    val minutes = if (split.size > 1) {
                        split[1].toInt()
                    }
                    else {
                        0
                    }

                    val seconds = if (split.size > 2) {
                        split[2].toInt()
                    }
                    else {
                        0
                    }

                    return Time(hours, minutes, seconds)
                }
                catch (e: Exception) {
                    return null
                }
            }

            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis / 1000
        }

        val isValid get() = hours in (0..23) && minutes in (0..59) && seconds in (0..59)
        val date get() = Date.from(Instant.ofEpochSecond(startOfDay + seconds + 60 * minutes + 3600 * hours))
    }

    val dataModel = DataModel()

    init {
        visualisationFrame.transactionView.presenter = this

        visualisationFrame.isVisible = true
    }

    fun put(hex: Int, vtRaw: String, evtRaw: String) {
        if (evtRaw.isEmpty()) {
            put(hex, vtRaw)
            return
        }

        if (vtRaw.isEmpty()) {
            error("Can't have End Valid Time without Valid Time")
            return
        }

        val vt = Time.factory(vtRaw)
        val evt = Time.factory(evtRaw)

        if (vt == null) {
            error("Can't parse Valid Time")
            return
        }

        if (evt == null) {
            error("Can't parse End Valid Time")
            return
        }

        if (!vt.isValid) {
            error("Invalid Valid Vime")
            return
        }

        if (!evt.isValid) {
            error("Invalid End Valid Time")
        }

        dataModel.put(hex, vt.date, evt.date)
        refresh()
    }

    fun put(hex: Int, vtRaw: String) {
        if (vtRaw.isEmpty()) {
            put(hex)
            return
        }

        val vt = Time.factory(vtRaw)

        if (vt == null) {
            error("Can't parse Valid Time")
            return
        }


        if (!vt.isValid) {
            error("Invalid Valid Vime")
            return
        }

        dataModel.put(hex, vt.date)
        refresh()
    }

    fun put(hex: Int) {
        dataModel.put(hex)
        refresh()
    }

    fun delete(vtRaw: String, evtRaw: String) {
        if (evtRaw.isEmpty()) {
            delete(vtRaw)
            return
        }

        if (vtRaw.isEmpty()) {
            error("Can't have End Valid Time without Valid Time")
            return
        }

        val vt = Time.factory(vtRaw)
        val evt = Time.factory(evtRaw)

        if (vt == null) {
            error("Can't parse Valid Time")
            return
        }

        if (evt == null) {
            error("Can't parse End Valid Time")
            return
        }

        if (!vt.isValid) {
            error("Invalid Valid Vime")
            return
        }

        if (!evt.isValid) {
            error("Invalid End Valid Time")
        }

        dataModel.delete(vt.date, evt.date)
        refresh()
    }

    fun delete(vtRaw: String) {
        if (vtRaw.isEmpty()) {
            delete()
            return
        }

        val vt = Time.factory(vtRaw)

        if (vt == null) {
            error("Can't parse Valid Time")
            return
        }


        if (!vt.isValid) {
            error("Invalid Valid Vime")
            return
        }

        dataModel.delete(vt.date)
        refresh()
    }

    fun refresh() {
        val data = dataModel.getData(Time.startOfDay)
        visualisationFrame.drawingView.refresh(data)
    }

    fun delete() {
        dataModel.delete()
        refresh()
    }

    fun reset() {
        dataModel.reset()
        refresh()
    }

    fun error(message: String) {
        JOptionPane.showMessageDialog(visualisationFrame, message)
    }
}