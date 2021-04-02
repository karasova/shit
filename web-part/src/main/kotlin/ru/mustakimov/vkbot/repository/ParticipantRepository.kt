package ru.mustakimov.vkbot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.mustakimov.vkbot.domain.Participant
import java.util.*

/**
 * Spring Data  repository for the [Participant] entity.
 */
@Suppress("unused")
@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {
    fun findByVkId(id: Long): Optional<Participant>
}
