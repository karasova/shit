package ru.mustakimov.vkbot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.mustakimov.vkbot.domain.Team
import ru.mustakimov.vkbot.domain.Track
import ru.mustakimov.vkbot.domain.enumeration.TeamStatus

/**
 * Spring Data  repository for the [Team] entity.
 */
@Repository
interface TeamRepository : JpaRepository<Team, Long> {
    fun findAllByStatus(status: TeamStatus): List<Team>

    fun findAllByStatusAndCase(status: TeamStatus, case: Track): List<Team>
}
