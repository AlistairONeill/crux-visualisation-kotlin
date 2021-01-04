package main.view

import main.model.DataModel
import main.model.TransactionData
import main.model.TransactionType
import main.util.TodayDateFormat
import java.awt.*
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class HistoryView: JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    fun add(data: TransactionData) {
        val panel = HistoryPanel(data)
        add(panel)
        invalidate()
        revalidate()
    }

    fun clear() {
        removeAll()
    }

    class HistoryPanel(data: TransactionData): JPanel() {
        init {
            layout = BorderLayout()
            preferredSize = Dimension(-1, 150)
            maximumSize = Dimension(Int.MAX_VALUE, 150)

            if (data.colour != null) {
                val colourPanel = JPanel()
                colourPanel.preferredSize = Dimension(20, -1)
                colourPanel.background = Color(data.colour)
                add(colourPanel, BorderLayout.EAST)
            }

            val mainPanel = JPanel()
            add(mainPanel, BorderLayout.CENTER)
            mainPanel.layout = GridBagLayout()

            fun add(text: String, alignment: Int, y: Int) {
                mainPanel.add(
                    JLabel(text, alignment),
                    GridBagConstraints().apply {
                        fill = GridBagConstraints.HORIZONTAL
                        gridy = y
                    }
                )
            }

            add(
                when (data.type) {
                    TransactionType.PUT -> "Put"
                    TransactionType.DELETE -> "Delete"
                    TransactionType.EVICT -> "Evict"
                },
                SwingConstants.CENTER,
                0
            )

            add("Transaction Time", SwingConstants.LEFT, 1)
            add(TodayDateFormat.shared.format(data.transactionTime), SwingConstants.RIGHT, 2)

            var y = 3
            fun addTimeField(label: String, date: Date?) {
                if (date == null) return
                add(label, SwingConstants.LEFT, y)
                add(TodayDateFormat.shared.format(date), SwingConstants.RIGHT, y+1)
                y += 2
            }

            addTimeField("ValidTime", data.validTime)
            addTimeField("EndValidTime", data.endValidTime)
        }
    }
}