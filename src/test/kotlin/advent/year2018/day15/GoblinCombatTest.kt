package advent.year2018.day15

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GoblinCombatTest {

    private val largeReferenceInput = """
        #########
        #G..G..G#
        #.......#
        #.......#
        #G..E..G#
        #.......#
        #.......#
        #G..G..G#
        #########
    """.trimIndent()

    @Test
    fun `parse -- reference input -- succeeds and has same toString`() {
        val combat = GoblinCombat.parse(largeReferenceInput)

        assertThat(combat.toString()).isEqualTo(largeReferenceInput)
    }

    @Test
    fun `advance -- reference input -- gives reference movement`() {
        val combat = GoblinCombat.parse(largeReferenceInput)

        combat.advance()

        val expected = """
            #########
            #.G...G.#
            #...G...#
            #...E..G#
            #.G.....#
            #.......#
            #G..G..G#
            #.......#
            #########
        """.trimIndent()
        assertThat(combat.toString()).isEqualTo(expected)
    }

    @Test
    fun `advance -- reference input advanced twice-- gives reference movement`() {
        val combat = GoblinCombat.parse(largeReferenceInput)

        repeat(2) { combat.advance() }

        val expected = """
            #########
            #..G.G..#
            #...G...#
            #.G.E.G.#
            #.......#
            #G..G..G#
            #.......#
            #.......#
            #########
        """.trimIndent()
        assertThat(combat.toString()).isEqualTo(expected)
    }

    @Test
    fun `advance -- reference input advanced thrice-- gives reference movement`() {
        val combat = GoblinCombat.parse(largeReferenceInput)

        repeat(3) { combat.advance() }

        val expected = """
            #########
            #.......#
            #..GGG..#
            #..GEG..#
            #G..G...#
            #......G#
            #.......#
            #.......#
            #########
        """.trimIndent()
        assertThat(combat.toString()).isEqualTo(expected)
    }


    @Test
    fun `reference battle input -- advanced once -- has expected status`() {
        val input = """
            #######
            #.G...#
            #...EG#
            #.#.#G#
            #..G#E#
            #.....#
            #######
        """.trimIndent()
        val combat = GoblinCombat.parse(input)

        repeat(1) { combat.advance() }

        assertThat(combat.toString()).isEqualTo("""
            #######
            #..G..#
            #...EG#
            #.#G#G#
            #...#E#
            #.....#
            #######
        """.trimIndent())
        assertThat(combat.elfHitPoints())
                .containsExactly(197, 197)
        assertThat(combat.goblinHitPoints())
                .containsExactlyInAnyOrder(197, 197, 200, 200)
    }

    @Test
    fun `reference battle input -- advanced twice -- has expected status`() {
        val input = """
            #######
            #.G...#
            #...EG#
            #.#.#G#
            #..G#E#
            #.....#
            #######
        """.trimIndent()
        val combat = GoblinCombat.parse(input)

        repeat(2) { combat.advance() }

        assertThat(combat.toString()).isEqualTo("""
            #######
            #...G.#
            #..GEG#
            #.#.#G#
            #...#E#
            #.....#
            #######
        """.trimIndent())
        assertThat(combat.elfHitPoints())
                .containsExactlyInAnyOrder(188, 194)
        assertThat(combat.goblinHitPoints())
                .containsExactlyInAnyOrder(194, 194, 200, 200)
    }

    @Test
    fun `reference battle input -- advanced 28 times -- has expected status`() {
        val input = """
            #######
            #.G...#
            #...EG#
            #.#.#G#
            #..G#E#
            #.....#
            #######
        """.trimIndent()
        val combat = GoblinCombat.parse(input)

        repeat(28) { combat.advance() }

        assertThat(combat.toString()).isEqualTo("""
            #######
            #G....#
            #.G...#
            #.#.#G#
            #...#E#
            #....G#
            #######
        """.trimIndent())
        assertThat(combat.elfHitPoints())
                .containsExactly(113)
        assertThat(combat.goblinHitPoints())
                .containsExactlyInAnyOrder(116, 131, 200, 200)
    }

    @Test
    fun `battle -- reference input -- reference outcome`() {
        val input = """
            #######
            #.G...#
            #...EG#
            #.#.#G#
            #..G#E#
            #.....#
            #######
        """.trimIndent()
        val combat = GoblinCombat.parse(input)

        val outcome = combat.battle()

        assertThat(outcome).isEqualTo(GoblinCombat.Outcome(turnsElapsed = 47,
                hpRemaining = 590))
        assertThat(outcome.value).isEqualTo(27730)
    }

    @Test
    fun `battle -- reference input two -- reference outcome`() {
        val input = """
            #######
            #G..#E#
            #E#E.E#
            #G.##.#
            #...#E#
            #...E.#
            #######
        """.trimIndent()
        val combat = GoblinCombat.parse(input)

        val outcome = combat.battle()

        assertThat(outcome).isEqualTo(GoblinCombat.Outcome(turnsElapsed = 37,
                hpRemaining = 982))
    }

    @Test
    fun `battle -- reference input three -- reference outcome`() {
        val input = """
            #######
            #E..EG#
            #.#G.E#
            #E.##E#
            #G..#.#
            #..E#.#
            #######
        """.trimIndent()
        val combat = GoblinCombat.parse(input)

        val outcome = combat.battle()

        assertThat(outcome).isEqualTo(GoblinCombat.Outcome(turnsElapsed = 46,
                hpRemaining = 859))
    }

    @Test
    fun `battle - final reference input - final reference outcome`() {
        val input = """
            #########
            #G......#
            #.E.#...#
            #..##..G#
            #...##..#
            #...#...#
            #.G...G.#
            #.....G.#
            #########
        """.trimIndent()
        val combat = GoblinCombat.parse(input)

        val outcome = combat.battle()

        assertThat(outcome).isEqualTo(GoblinCombat.Outcome(turnsElapsed = 20,
                hpRemaining = 937))
    }


    private inline fun <reified T : Combatant> GoblinCombat.combatantHitPoints() =
            this.combatants.filterIsInstance<T>()
                    .filter { it.isAlive() }
                    .map { it.hitPoints }

    private fun GoblinCombat.elfHitPoints() = this.combatantHitPoints<Elf>()
    private fun GoblinCombat.goblinHitPoints() = this.combatantHitPoints<Goblin>()
}