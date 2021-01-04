package main

import main.presenter.VisualisationPresenter

class Main {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val frame = VisualisationFrame()
            VisualisationPresenter(frame)
        }
    }
}