package ru.mustakimov.vkbot.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.mustakimov.vkbot.web.rest.equalsVerifier

class TeamTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Team::class)
        val team1 = Team()
        team1.id = 1L
        val team2 = Team()
        team2.id = team1.id
        assertThat(team1).isEqualTo(team2)
        team2.id = 2L
        assertThat(team1).isNotEqualTo(team2)
        team1.id = null
        assertThat(team1).isNotEqualTo(team2)
    }
}
