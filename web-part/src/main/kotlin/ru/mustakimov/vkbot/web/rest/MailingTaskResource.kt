package ru.mustakimov.vkbot.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.mustakimov.vkbot.domain.MailingTask
import ru.mustakimov.vkbot.domain.enumeration.MailingType
import ru.mustakimov.vkbot.domain.enumeration.TeamStatus
import ru.mustakimov.vkbot.repository.MailingTaskRepository
import ru.mustakimov.vkbot.web.rest.errors.BadRequestAlertException
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid

private const val ENTITY_NAME = "mailingTask"

/**
 * REST controller for managing [ru.mustakimov.vkbot.domain.MailingTask].
 */
@RestController
@RequestMapping("/api")
@Transactional
class MailingTaskResource(
    private val mailingTaskRepository: MailingTaskRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /mailing-tasks` : Create a new mailingTask.
     *
     * @param mailingTask the mailingTask to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new mailingTask, or with status `400 (Bad Request)` if the mailingTask has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mailing-tasks")
    fun createMailingTask(@Valid @RequestBody mailingTask: MailingTask): ResponseEntity<MailingTask> {
        log.debug("REST request to save MailingTask : $mailingTask")
        if (mailingTask.id != null) {
            throw BadRequestAlertException(
                "A new mailingTask cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        if (mailingTask.mailingType == MailingType.SELECT_CASE && mailingTask.filterStatus != TeamStatus.REGISTERED) {
            throw BadRequestAlertException(
                "Mailing task with case selection should be filtered with REGISTERED teams",
                ENTITY_NAME,
                "validation"
            )
        }
        val result = mailingTaskRepository.save(mailingTask)
        return ResponseEntity.created(URI("/api/mailing-tasks/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /mailing-tasks` : Updates an existing mailingTask.
     *
     * @param mailingTask the mailingTask to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated mailingTask,
     * or with status `400 (Bad Request)` if the mailingTask is not valid,
     * or with status `500 (Internal Server Error)` if the mailingTask couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mailing-tasks")
    fun updateMailingTask(@Valid @RequestBody mailingTask: MailingTask): ResponseEntity<MailingTask> {
        log.debug("REST request to update MailingTask : $mailingTask")
        if (mailingTask.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (mailingTask.mailingType == MailingType.SELECT_CASE && mailingTask.filterStatus != TeamStatus.REGISTERED) {
            throw BadRequestAlertException(
                "Mailing task with case selection should be filtered with REGISTERED teams",
                ENTITY_NAME,
                "validation"
            )
        }
        val result = mailingTaskRepository.save(mailingTask)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    mailingTask.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /mailing-tasks` : get all the mailingTasks.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of mailingTasks in body.
     */
    @GetMapping("/mailing-tasks")
    fun getAllMailingTasks(): MutableList<MailingTask> {
        log.debug("REST request to get all MailingTasks")
        return mailingTaskRepository.findAll()
    }

    /**
     * `GET  /mailing-tasks/:id` : get the "id" mailingTask.
     *
     * @param id the id of the mailingTask to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the mailingTask, or with status `404 (Not Found)`.
     */
    @GetMapping("/mailing-tasks/{id}")
    fun getMailingTask(@PathVariable id: Long): ResponseEntity<MailingTask> {
        log.debug("REST request to get MailingTask : $id")
        val mailingTask = mailingTaskRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(mailingTask)
    }

    /**
     *  `DELETE  /mailing-tasks/:id` : delete the "id" mailingTask.
     *
     * @param id the id of the mailingTask to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/mailing-tasks/{id}")
    fun deleteMailingTask(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete MailingTask : $id")

        mailingTaskRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
