package main.view

import main.model.TransactionRequestData
import main.model.TransactionType
import main.presenter.VisualisationPresenter
import main.util.TodayDateFormat
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

class TransactionView: JPanel(), ActionListener {
    companion object {
        private const val ACTION_COMMAND_PUT = "PUT"
        private const val ACTION_COMMAND_DELETE = "DELETE"
        private const val ACTION_COMMAND_EVICT = "EVICT"
        private const val ACTION_COMMAND_RESET = "RESET"
    }

    private val colourChooser = JColorChooser()
    private val putButton = JButton("Put")
    private val deleteButton = JButton("Delete")
    private val resetButton = JButton("Reset")
    private val evictButton = JButton("Evict")
    private val labelValidTime = JLabel("Valid Time")
    private val labelEndValidTime = JLabel("End Valid Time")
    private val validTimeField = JTextField()
    private val endValidTimeField = JTextField()

    lateinit var presenter: VisualisationPresenter

    private val colour get() = colourChooser.color.rgb
    private val validTime get() = validTimeField.text
    private val endValidTime get() = endValidTimeField.text

    init {
        layout = GridBagLayout()

        fun add(component: JComponent, x: Int, y: Int, width: Int) {
            add(component, GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                gridx = x
                gridy = y
                gridwidth = width
            })
        }

        add(colourChooser, 0, 0, 2)
        add(labelValidTime, 0, 1, 1)
        add(validTimeField, 1, 1, 1)
        add(labelEndValidTime, 0, 2, 1)
        add(endValidTimeField, 1, 2, 1)
        add(putButton, 0, 3, 2)
        add(deleteButton, 0, 4, 2)
        add(evictButton, 0, 5, 2)
        add(resetButton, 0, 6, 2)

        putButton.actionCommand = ACTION_COMMAND_PUT
        putButton.addActionListener(this)

        deleteButton.actionCommand = ACTION_COMMAND_DELETE
        deleteButton.addActionListener(this)

        evictButton.actionCommand = ACTION_COMMAND_EVICT
        evictButton.addActionListener(this)

        resetButton.actionCommand = ACTION_COMMAND_RESET
        resetButton.addActionListener(this)
    }

    override fun actionPerformed(ae: ActionEvent?) {
        val actionCommand = ae?.actionCommand ?: return
        if (actionCommand == ACTION_COMMAND_RESET) {
            reset()
        }
        else {
            val type = when (actionCommand) {
                ACTION_COMMAND_PUT -> TransactionType.PUT
                ACTION_COMMAND_EVICT -> TransactionType.EVICT
                ACTION_COMMAND_DELETE -> TransactionType.DELETE
                else -> throw Exception()
            }

            val colour = colour
            val validTime = TodayDateFormat.shared.parse(validTime)
            val endValidTime = TodayDateFormat.shared.parse(endValidTime)

            val transactionRequest = TransactionRequestData(type, validTime, endValidTime, colour)
            presenter.submit(transactionRequest)
        }
    }

    private fun reset() {
        presenter.reset()
    }
}