package advent.year2024.day14

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.Panel
import java.awt.event.ActionEvent
import java.lang.Thread.sleep
import javax.swing.*
import kotlin.concurrent.thread

/**
 * Hope everyone is ready for some more totally-not-obsolete Java UI with Swing!!
 */
class RestroomRobotsDisplay(
    initialRobots: RestroomRobots,
    var time: Int = 0,
    val pausePlayingAt: Int? = null
) {
    @Volatile
    private var playing: Boolean = false

    private val timeDisplay = JTextArea().apply {
        isEditable = false
    }
    private val panel = RobotPixelPanel(initialRobots)

    private val previousTimeAction = object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) = updateTime(time - 1)
    }
    private val nextTimeAction = object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) = updateTime(time + 1)
    }

    fun play() {
        playing = true
        thread(start = true) {
            while (playing) {
                updateTime(time + 1)
                // If we requested a time to pause playing, and hit that time, pause.
                if (time == pausePlayingAt) playing = false
                sleep(2)
            }
        }
    }

    private fun pause() {
        playing = false
    }

    private val controlPanel = Panel().apply {
        val playButton = JButton().apply {
            text = "Play"
            addActionListener {
                if (playing) {
                    pause()
                    text = "Play"
                } else {
                    play()
                    text = "Pause"
                }
            }
        }
        val previousButton = JButton(previousTimeAction).apply {
            text = "<<"
        }
        val nextButton = JButton(nextTimeAction).apply {
            text = ">>"
        }
        add(playButton, BorderLayout.WEST)
        add(previousButton, BorderLayout.WEST)
        add(nextButton, BorderLayout.WEST)
    }
    private val frame = JFrame().apply {
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        contentPane.add(timeDisplay, BorderLayout.NORTH)
        contentPane.add(panel, BorderLayout.CENTER)
        contentPane.add(controlPanel, BorderLayout.SOUTH)
        pack()
        isVisible = true
    }

    init {
        updateTime(time)
    }

    fun updateTime(time: Int) {
        this.time = time
        panel.time = time
        timeDisplay.text = time.toString()
        panel.repaint()
        frame.pack()
    }


}

class RobotPixelPanel(private val initialRobots: RestroomRobots, var time: Int = 0) : JPanel() {
    companion object {
        private const val PIXELS_PER_SPACE = 4
    }

    init {
        background = Color.WHITE
        layout = null
        setSize(initialRobots.room.width * PIXELS_PER_SPACE, initialRobots.room.height * PIXELS_PER_SPACE)
        preferredSize = size
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.color = Color.BLACK
        val currentRobots = initialRobots.next(time)
        currentRobots.robots.forEach { robot ->
            val point = robot.position
            g.fillRect(
                point.x * PIXELS_PER_SPACE,
                point.y * PIXELS_PER_SPACE,
                PIXELS_PER_SPACE,
                PIXELS_PER_SPACE
            )
        }
    }
}