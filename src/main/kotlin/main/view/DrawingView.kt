package main.view

import main.model.DataModel
import java.awt.Graphics
import javax.swing.JPanel
import java.awt.Color

class DrawingView: JPanel() {
    private var data: DataModel.DrawingData? = null

    fun refresh(data: DataModel.DrawingData) {
        this.data = data
        repaint()
    }

    override fun paint(g: Graphics?) {
        super.paintComponent(g)

        if (g == null) {
            return
        }

        val data = data ?: return

        val width = width.toDouble()
        val height = height.toDouble()

        val firstTt = data.transactionTimes.first()
        val lastTt = data.transactionTimes.last()
        val dTt = (lastTt - firstTt) * 1.2

        val yPos = data.validTimes.map { it * height / 24 / 60 / 60 }.map { it.toInt() }
        val xPos = data.transactionTimes.map { (it - firstTt) * width / dTt }.map { it.toInt() }

        val x = xPos.last()
        val y = yPos.last()
        for (i in 0 until xPos.size - 1) {
            for (j in 0 until yPos.size - 1) {
                val x1 = xPos[i]
                val x2 = xPos[i+1]
                val y1 = yPos[j]
                val y2 = yPos[j+1]
                val colour = data.colours[i][j] ?: continue
                g.color = Color(colour)
                g.fillRect(x1, y1, x2 - x1, y2 - y1)
            }

            val x1 = xPos[i]
            val x2 = xPos[i+1]
            val colour = data.colours[i].last() ?: continue
            g.color = Color(colour)
            g.fillRect(x1, y, x2 - x1, (height - y).toInt())
        }

        for (j in 0 until yPos.size - 1) {
            val y1 = yPos[j]
            val y2 = yPos[j+1]
            val colour = data.colours.last()[j] ?: continue
            g.color = Color(colour)
            g.fillRect(x, y1, (width - x).toInt(), y2 - y1)
        }

        val colour = data.colours.last().last() ?: return
        g.color = Color(colour)
        g.fillRect(x, y, (width - x).toInt(), (height - y).toInt())
    }
}