package main

import main.view.DrawingView
import main.view.HistoryView
import main.view.TransactionView
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JScrollPane

class VisualisationFrame: JFrame("Crux Visualisation") {
    val transactionView = TransactionView()
    val drawingView = DrawingView()
    val historyView = HistoryView()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        extendedState = MAXIMIZED_BOTH
        isUndecorated = true

        layout = BorderLayout()

        add(transactionView, BorderLayout.EAST)
        add(drawingView, BorderLayout.CENTER)

        val historyScroll = JScrollPane()
        historyScroll.preferredSize = Dimension(200, -1)
        add(historyScroll, BorderLayout.WEST)
        historyScroll.setViewportView(historyView)
    }
}