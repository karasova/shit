package ru.mustakimov.vkbot.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.mustakimov.vkbot.web.rest.equalsVerifier

class TrackTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Track::class)
        val track1 = Track()
        track1.id = 1L
        val track2 = Track()
        track2.id = track1.id
        assertThat(track1).isEqualTo(track2)
        track2.id = 2L
        assertThat(track1).isNotEqualTo(track2)
        track1.id = null
        assertThat(track1).isNotEqualTo(track2)
    }
}
