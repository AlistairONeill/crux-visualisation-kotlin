package main

import main.view.DrawingView
import main.view.TransactionView
import java.awt.BorderLayout
import javax.swing.JFrame

class VisualisationFrame: JFrame("Crux Visualisation") {
    val transactionView = TransactionView()
    val drawingView = DrawingView()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        extendedState = MAXIMIZED_BOTH
        isUndecorated = true

        layout = BorderLayout()

        add(transactionView, BorderLayout.EAST)
        add(drawingView, BorderLayout.CENTER)
    }
}