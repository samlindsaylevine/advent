package advent.year2024.day15

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RobotOperatedWarehouseTest {

    @Test
    fun `gps -- reference example -- 104`() {
        val input = """
            #######
            #...O..
            #.....@
            
            
        """.trimIndent()
        val warehouse = RobotOperatedWarehouse.of(input)

        val sum = warehouse.gpsSum()

        assertThat(sum).isEqualTo(104)
    }

    @Test
    fun `gps -- wide example -- 105`() {
        val input = """
            ##########
            ##...[]...
            ##........
            
            
        """.trimIndent()
        val warehouse = RobotOperatedWarehouse.of(input)

        val sum = warehouse.gpsSum()

        assertThat(sum).isEqualTo(105)
    }

    @Test
    fun `after moving -- smaller example -- reference value`() {
        val input = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########

            <^^>>>vv<v>>v<<
        """.trimIndent()
        var warehouse = RobotOperatedWarehouse.of(input)

        warehouse = warehouse.attempt(RobotOperatedWarehouse.Move.of('<'))

        var expected = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
        """.trimIndent()
        assertThat(warehouse.toString()).isEqualTo(expected)

        warehouse = warehouse.attempt(RobotOperatedWarehouse.Move.of('^'))

        expected = """
            ########
            #.@O.O.#
            ##..O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
        """.trimIndent()
        assertThat(warehouse.toString()).isEqualTo(expected)

        warehouse = warehouse.attempt(RobotOperatedWarehouse.Move.of('^'))

        assertThat(warehouse.toString()).isEqualTo(expected)

        warehouse = warehouse.attempt(RobotOperatedWarehouse.Move.of('>'))

        expected = """
            ########
            #..@OO.#
            ##..O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
        """.trimIndent()
        assertThat(warehouse.toString()).isEqualTo(expected)

        warehouse = warehouse.attempt(RobotOperatedWarehouse.Move.of('>'))

        expected = """
            ########
            #...@OO#
            ##..O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########
        """.trimIndent()
        assertThat(warehouse.toString()).isEqualTo(expected)
    }

    @Test
    fun `gps after moving -- smaller example -- 2028`() {
        val input = """
            ########
            #..O.O.#
            ##@.O..#
            #...O..#
            #.#.O..#
            #...O..#
            #......#
            ########

            <^^>>>vv<v>>v<<
        """.trimIndent()
        val warehouse = RobotOperatedWarehouse.of(input)

        val sum = warehouse.afterMoves().gpsSum()

        assertThat(sum).isEqualTo(2028)
    }

    @Test
    fun `gps after moving -- larger example -- 10092`() {
        val input = """
            ##########
            #..O..O.O#
            #......O.#
            #.OO..O.O#
            #..O@..O.#
            #O#..O...#
            #O..O..O.#
            #.OO.O.OO#
            #....O...#
            ##########

            <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
            vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
            ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
            <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
            ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
            ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
            >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
            <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
            ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
            v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
        """.trimIndent()
        val warehouse = RobotOperatedWarehouse.of(input)

        val sum = warehouse.afterMoves().gpsSum()

        assertThat(sum).isEqualTo(10092)
    }

    @Test
    fun `wider warehouse -- larger example -- reference value`() {
        val input = """
            ##########
            #..O..O.O#
            #......O.#
            #.OO..O.O#
            #..O@..O.#
            #O#..O...#
            #O..O..O.#
            #.OO.O.OO#
            #....O...#
            ##########
        """.trimIndent()

        val wider = input.widerWarehouse()

        val expected = """
            ####################
            ##....[]....[]..[]##
            ##............[]..##
            ##..[][]....[]..[]##
            ##....[]@.....[]..##
            ##[]##....[]......##
            ##[]....[]....[]..##
            ##..[][]..[]..[][]##
            ##........[]......##
            ####################
        """.trimIndent()
        assertThat(wider).isEqualTo(expected)
    }

    @Test
    fun `after each step -- reference widened larger example -- is in provided state`() {
        val input = """
            #######
            #...#.#
            #.....#
            #..OO@#
            #..O..#
            #.....#
            #######

            <vv<<^^<<^^
        """.trimIndent()
        var warehouse = RobotOperatedWarehouse.of(input.widerWarehouse())

        var expected = """
            ##############
            ##......##..##
            ##..........##
            ##....[][]@.##
            ##....[]....##
            ##..........##
            ##############
        """.trimIndent()
        assertThat(warehouse.toString()).isEqualTo(expected)

        warehouse = warehouse.attempt(RobotOperatedWarehouse.Move.of('<'))

        expected = """
            ##############
            ##......##..##
            ##..........##
            ##...[][]@..##
            ##....[]....##
            ##..........##
            ##############
        """.trimIndent()
        assertThat(warehouse.toString()).isEqualTo(expected)
    }
    
    @Test
    fun `after moving -- widened larger example -- 9021`() {
        val input = """
            ##########
            #..O..O.O#
            #......O.#
            #.OO..O.O#
            #..O@..O.#
            #O#..O...#
            #O..O..O.#
            #.OO.O.OO#
            #....O...#
            ##########

            <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
            vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
            ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
            <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
            ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
            ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
            >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
            <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
            ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
            v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
        """.trimIndent()
        val warehouse = RobotOperatedWarehouse.of(input.widerWarehouse())

        val after = warehouse.afterMoves()

        val expected = """
            ####################
            ##[].......[].[][]##
            ##[]...........[].##
            ##[]........[][][]##
            ##[]......[]....[]##
            ##..##......[]....##
            ##..[]............##
            ##..@......[].[][]##
            ##......[][]..[]..##
            ####################
        """.trimIndent()
        assertThat(after.toString()).isEqualTo(expected)
        assertThat(after.gpsSum()).isEqualTo(9021)
    }
}