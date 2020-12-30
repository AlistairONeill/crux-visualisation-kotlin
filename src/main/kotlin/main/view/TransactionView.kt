package main.view

import main.presenter.VisualisationPresenter
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

class TransactionView: JPanel(), ActionListener {
    private val colourChooser = JColorChooser()
    private val putButton = JButton("Put")
    private val deleteButton = JButton("Delete")
    private val resetButton = JButton("Reset")
    private val validTimeField = JTextField()
    private val endValidTimeField = JTextField()

    lateinit var presenter: VisualisationPresenter

    private val colour get() = colourChooser.color.rgb
    private val validTime get() = validTimeField.text
    private val endValidTime get() = endValidTimeField.text

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(colourChooser)
        add(validTimeField)
        add(endValidTimeField)
        add(putButton)
        add(deleteButton)
        add(resetButton)

        putButton.actionCommand = "PUT"
        putButton.addActionListener(this)

        deleteButton.actionCommand = "DELETE"
        deleteButton.addActionListener(this)

        resetButton.actionCommand = "RESET"
        resetButton.addActionListener(this)
    }

    override fun actionPerformed(ae: ActionEvent?) {
        when (ae!!.actionCommand) {
            "PUT" -> put()
            "DELETE" -> delete()
            "RESET" -> reset()
        }
    }

    fun put() {
        presenter.put(colour, validTime, endValidTime)
    }

    fun delete() {
        presenter.delete(validTime, endValidTime)
    }

    fun reset() {
        presenter.reset()
    }
}