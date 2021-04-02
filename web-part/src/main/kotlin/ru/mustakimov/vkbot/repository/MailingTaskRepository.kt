package ru.mustakimov.vkbot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.mustakimov.vkbot.domain.MailingTask
import ru.mustakimov.vkbot.domain.enumeration.MailingStatus
import java.time.Instant

/**
 * Spring Data  repository for the [MailingTask] entity.
 */
@Suppress("unused")
@Repository
interface MailingTaskRepository : JpaRepository<MailingTask, Long> {
    fun findAllByPlannedTimeBeforeAndMailingStatus(time: Instant, mailingStatus: MailingStatus): List<MailingTask>

    @JvmDefault
    fun findAllByPlannedTimeBeforeAndMailingStatusAdded(time: Instant): List<MailingTask> {
        return findAllByPlannedTimeBeforeAndMailingStatus(time, MailingStatus.ADDED)
    }
}
