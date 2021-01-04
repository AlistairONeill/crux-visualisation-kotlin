package main.presenter

import crux.api.Crux
import main.VisualisationFrame
import main.model.DataModel
import main.model.TransactionData
import main.model.TransactionRequestData

class VisualisationPresenter(private val visualisationFrame: VisualisationFrame) {
    private var dataModel = DataModel(Crux.startNode())

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
        dataModel = DataModel(Crux.startNode())
        visualisationFrame.drawingView.refresh(null)
    }
}