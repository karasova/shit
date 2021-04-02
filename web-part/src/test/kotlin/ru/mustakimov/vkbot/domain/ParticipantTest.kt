package ru.mustakimov.vkbot.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.mustakimov.vkbot.web.rest.equalsVerifier

class ParticipantTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Participant::class)
        val participant1 = Participant()
        participant1.id = 1L
        val participant2 = Participant()
        participant2.id = participant1.id
        assertThat(participant1).isEqualTo(participant2)
        participant2.id = 2L
        assertThat(participant1).isNotEqualTo(participant2)
        participant1.id = null
        assertThat(participant1).isNotEqualTo(participant2)
    }
}
