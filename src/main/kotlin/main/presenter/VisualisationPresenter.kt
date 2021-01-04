package main.presenter

import main.VisualisationFrame
import main.model.DataModel
import main.model.TransactionData
import main.model.TransactionRequestData
import java.time.Instant
import java.util.*
import javax.swing.JOptionPane

class VisualisationPresenter(private val visualisationFrame: VisualisationFrame) {
    private val dataModel = DataModel()

    init {
        visualisationFrame.transactionView.presenter = this
        visualisationFrame.isVisible = true
    }

    fun submit(request: TransactionRequestData) {
        val transactionData = dataModel.submit(request)
        refresh(transactionData)
    }

    private fun refresh(transactionData: TransactionData) {
        visualisationFrame.historyView.add(transactionData)
        val data = dataModel.getData()
        visualisationFrame.drawingView.refresh(data)
    }

    fun reset() {
        visualisationFrame.historyView.clear()
        dataModel.reset()
        val data = dataModel.getData()
        visualisationFrame.drawingView.refresh(data)
    }

    private fun error(message: String) {
        JOptionPane.showMessageDialog(visualisationFrame, message)
    }
}