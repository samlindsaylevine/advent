package advent.year2019.day13

import advent.utils.Point
import advent.year2019.day5.IntcodeComputer
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.io.File
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture
import javax.swing.*

class IntcodeArcadeCabinet(val program: List<Long>) {
    fun startingBlocks(): Set<Point> {
        val blocks = mutableSetOf<Point>()
        val computer = IntcodeComputer()
        computer.execute(program,
                { throw IllegalStateException("No input available") },
                consumeChunked(3) {
                    if (it[2] == 2L) blocks.add(Point(it[0].toInt(), it[1].toInt()))
                })
        return blocks
    }

    fun play() = play(HumanArcadePlayer())

    fun automate() = play(BotArcadePlayer())

    private fun play(player: ArcadePlayer) {
        val display = IntcodeArcadeDisplay()

        val computer = IntcodeComputer()

        val newProgram = program.toMutableList()
        newProgram[0] = 2

        sleep(10000)

        computer.execute(newProgram,
                { player.nextInput(display).input.toLong() },
                consumeChunked(3) {
                    if (it[0] == -1L && it[1] == 0L) {
                        display.setScore(it[2])
                    } else {
                        display.draw(it[0].toInt(), it[1].toInt(), Tile.ofCode(it[2]))
                    }
                })
    }
}

private interface ArcadePlayer {
    fun nextInput(display: IntcodeArcadeDisplay): JoystickState
}

private class HumanArcadePlayer : ArcadePlayer {
    override fun nextInput(display: IntcodeArcadeDisplay) = display.pollKeyboard()
}

private class BotArcadePlayer : ArcadePlayer {
    override fun nextInput(display: IntcodeArcadeDisplay): JoystickState {
        sleep(20)
        return when {
            display.ballX() < display.paddleX() -> JoystickState.LEFT
            display.ballX() > display.paddleX() -> JoystickState.RIGHT
            else -> JoystickState.NEUTRAL
        }
    }
}

/**
 * Hope everyone is ready for some not at all obsolete and clunky Java UI with Swing!!
 *
 * In retrospect this was very silly and I should have just maintained a game state... but I thought it would be easier
 * to play the game and win, so I figured I would have the enjoyment of doing that!
 *
 * However, I am crappy at breakout :) So I ended up writing a bot to do the input. That left all this UI based code
 * very pointless... but it's fun to watch!
 */
private class IntcodeArcadeDisplay {
    companion object {
        const val PIXELS_PER_SPACE = 20
    }

    private val frame = JFrame()
    private val scoreDisplay = JTextArea()
    private val tiles = mutableMapOf<Point, Tile>()
    private val panel = PixelPanel(tiles)
    @Volatile
    private var awaitingKeyStroke: CompletableFuture<JoystickState>? = null

    fun ballX() = tiles.filter { it.value == Tile.BALL }.map { it.key.x }.min()
            ?: throw IllegalStateException("No ball")

    fun paddleX() = tiles.filter { it.value == Tile.PADDLE }.map { it.key.x }.min()
            ?: throw IllegalStateException("No paddle")

    init {
        panel.background = Color.BLACK
        panel.layout = null

        scoreDisplay.isEditable = false
        frame.contentPane.add(scoreDisplay, BorderLayout.NORTH)
        frame.contentPane.add(panel, BorderLayout.CENTER)
        frame.pack()
        frame.isVisible = true

        panel.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LEFT")
        panel.actionMap.put("LEFT", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                joystick(JoystickState.LEFT)
            }
        })
        panel.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RIGHT")
        panel.actionMap.put("RIGHT", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                joystick(JoystickState.RIGHT)
            }
        })
        panel.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DOWN")
        panel.actionMap.put("DOWN", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                joystick(JoystickState.NEUTRAL)
            }
        })

        panel.requestFocus()
    }

    fun setScore(score: Long) {
        scoreDisplay.text = "Score: $score"
        frame.pack()
    }

    fun draw(x: Int, y: Int, tile: Tile) {
        val newWidth = Math.max(panel.width, (x + 1) * PIXELS_PER_SPACE)
        val newHeight = Math.max(panel.height, (x + 1) * PIXELS_PER_SPACE)
        if (newWidth > panel.width || newHeight > panel.height) {
            panel.setSize(newWidth, newHeight)
            panel.preferredSize = panel.size
            frame.pack()
        }

        tiles[Point(x, y)] = tile

        panel.repaint()
    }

    private fun joystick(state: JoystickState) {
        awaitingKeyStroke?.complete(state)
    }

    fun pollKeyboard(): JoystickState {
        val eventFuture = CompletableFuture<JoystickState>()
        awaitingKeyStroke = eventFuture
        return eventFuture.get()
    }
}

private class PixelPanel(val tiles: Map<Point, Tile>) : JPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        tiles.forEach { (point, tile) ->
            g.color = tile.color
            g.fillRect(point.x * IntcodeArcadeDisplay.PIXELS_PER_SPACE,
                    point.y * IntcodeArcadeDisplay.PIXELS_PER_SPACE,
                    IntcodeArcadeDisplay.PIXELS_PER_SPACE,
                    IntcodeArcadeDisplay.PIXELS_PER_SPACE)
        }
    }
}

private enum class JoystickState(val input: Int) {
    LEFT(-1),
    NEUTRAL(0),
    RIGHT(1);

    companion object {
        fun of(keyCode: Int): JoystickState? = when (keyCode) {
            KeyEvent.VK_LEFT -> LEFT
            KeyEvent.VK_DOWN -> NEUTRAL
            KeyEvent.VK_RIGHT -> RIGHT
            else -> null
        }
    }
}

private enum class Tile(val color: Color, val code: Long) {
    EMPTY(Color.BLACK, 0),
    WALL(Color.DARK_GRAY, 1),
    BLOCK(Color.LIGHT_GRAY, 2),
    PADDLE(Color.GREEN, 3),
    BALL(Color.RED, 4);

    companion object {
        fun ofCode(code: Long) = values().first { it.code == code }
    }
}

fun <T> consumeChunked(size: Int, listConsumer: (List<T>) -> Unit): (T) -> Unit {
    val partialList = mutableListOf<T>()

    return {
        partialList.add(it)
        if (partialList.size == size) {
            listConsumer(partialList)
            partialList.clear()
        }
    }
}

fun parseIntcodeFromFile(filePath: String) = File(filePath)
        .readText()
        .trim()
        .split(",")
        .map { it.toLong() }

fun main() {
    val program = parseIntcodeFromFile("src/main/kotlin/advent/year2019/day13/input.txt")

    val cabinet = IntcodeArcadeCabinet(program)
    val blocks = cabinet.startingBlocks()
    println(blocks.size)

    cabinet.automate()

}
