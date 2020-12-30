package main.view

import main.model.DataModel
import java.awt.BorderLayout
import java.awt.Graphics
import javax.swing.JPanel
import java.awt.Color
import java.awt.Dimension

class DrawingView: JPanel() {
    companion object {
        fun format(input: Long): String {
            fun Long.fmt(): String = toString().padEnd(2, '0')
            var time = input
            val seconds = time % 60
            time /= 60
            val minutes = time % 60
            val hours = time / 60
            return "${hours}:${minutes.fmt()}:${seconds.fmt()}"
        }
    }
    private var data: DataModel.DrawingData? = null

    private val central = Central(this)
    private val east = East(this)
    private val north = North(this)

    init {
        layout = BorderLayout()

        east.preferredSize = Dimension(100, -1)
        north.preferredSize = Dimension(-1, 100)

        add(central, BorderLayout.CENTER)
        add(east, BorderLayout.EAST)
        add(north, BorderLayout.NORTH)
    }

    fun refresh(data: DataModel.DrawingData) {
        this.data = data
        central.repaint()
        east.repaint()
        north.repaint()
    }

    class Central(private val parent: DrawingView): JPanel() {
        override fun paint(g: Graphics?) {
            super.paintComponent(g)

            if (g == null) {
                return
            }

            val data = parent.data ?: return

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

    class East(private val parent: DrawingView): JPanel() {
        override fun paint(g: Graphics?) {
            super.paint(g)

            if (g == null) {
                return
            }

            val data = parent.data ?: return

            val height = height.toDouble()

            val yPos = data.validTimes.map { it to (it * height / 24 / 60 / 60).toInt() }
            yPos.forEach {
                g.drawString(format(it.first), 0, it.second)
            }
        }
    }

    class North(private val parent: DrawingView): JPanel() {
        override fun paint(g: Graphics?) {
            super.paint(g)

            if (g == null) {
                return
            }

            val data = parent.data ?: return

            val width = parent.central.width.toDouble()

            val firstTt = data.transactionTimes.first()
            val lastTt = data.transactionTimes.last()
            val dTt = (lastTt - firstTt) * 1.2

            val xPos = data.transactionTimes.map { it to ((it - firstTt) * width / dTt).toInt() }
            xPos.forEach {
                g.drawString(format(it.first), it.second, height)
            }
        }
    }
}