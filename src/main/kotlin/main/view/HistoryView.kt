package main.view

import main.model.DataModel
import java.awt.*
import java.text.SimpleDateFormat
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class HistoryView: JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    fun add(data: DataModel.TransactionData) {
        val panel = HistoryPanel(data)
        add(panel)
        invalidate()
        revalidate()
    }

    fun clear() {
        removeAll()
    }

    class HistoryPanel(data: DataModel.TransactionData): JPanel() {
        init {
            val dateFormat = SimpleDateFormat("HH:mm:ss")
            layout = BorderLayout()
            preferredSize = Dimension(-1, 150)
            maximumSize = Dimension(Int.MAX_VALUE, 150)

            if (data.hex != null) {
                val colourPanel = JPanel()
                colourPanel.preferredSize = Dimension(20, -1)
                colourPanel.background = Color(data.hex)
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
                    DataModel.TransactionData.Type.PUT -> "Put"
                    DataModel.TransactionData.Type.DELETE -> "Delete"
                },
                SwingConstants.CENTER,
                0
            )
            add("Transaction Time", SwingConstants.LEFT, 1)
            add(dateFormat.format(data.transactionTime), SwingConstants.RIGHT, 2)

            add("Valid Time", SwingConstants.LEFT, 3)
            add(dateFormat.format(data.validTime), SwingConstants.RIGHT, 4)

            if (data.endValidTime != null) {
                add("End Valid Time", SwingConstants.LEFT, 5)
                add(dateFormat.format(data.endValidTime), SwingConstants.RIGHT, 6)
            }
        }
    }
}