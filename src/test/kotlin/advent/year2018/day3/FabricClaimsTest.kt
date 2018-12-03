package advent.year2018.day3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FabricClaimsTest {

    @Test
    fun `overlappingSquareInches -- reference input -- reference output`() {
        val claims = FabricClaims.parse("""
            #1 @ 1,3: 4x4
            #2 @ 3,1: 4x4
            #3 @ 5,5: 2x2
        """.trimIndent())

        val overlapping = claims.overlappingSquareInches

        assertThat(overlapping).isEqualTo(4)
    }

    @Test
    fun `nonOverlappingClaims -- reference input -- reference output`() {
        val claims = FabricClaims.parse("""
            #1 @ 1,3: 4x4
            #2 @ 3,1: 4x4
            #3 @ 5,5: 2x2
        """.trimIndent())

        val nonOverlapping = claims.nonOverlappingClaims

        assertThat(nonOverlapping).containsExactly(3)
    }

}