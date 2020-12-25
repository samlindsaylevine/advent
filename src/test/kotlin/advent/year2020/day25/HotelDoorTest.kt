package advent.year2020.day25

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HotelDoorTest {

  @Test
  fun `doorLoopSize -- reference inputs -- 11`() {
    val door = HotelDoor(doorPublicKey = 17807724, cardPublicKey = 5764801)

    assertThat(door.doorLoopSize).isEqualTo(11)
  }

  @Test
  fun `encryption key -- reference inputs -- 14897079`() {
    val door = HotelDoor(doorPublicKey = 17807724, cardPublicKey = 5764801)

    assertThat(door.encryptionKey).isEqualTo(14897079)
  }
}