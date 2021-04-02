package ru.mustakimov.vkbot.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.mustakimov.vkbot.domain.Participant
import ru.mustakimov.vkbot.repository.ParticipantRepository
import ru.mustakimov.vkbot.repository.TeamRepository
import ru.mustakimov.vkbot.web.rest.errors.BadRequestAlertException
import java.net.URI
import java.net.URISyntaxException

private const val ENTITY_NAME = "participant"

/**
 * REST controller for managing [ru.mustakimov.vkbot.domain.Participant].
 */
@RestController
@RequestMapping("/api")
@Transactional
class ParticipantResource(
    private val participantRepository: ParticipantRepository,
    private val teamRepository: TeamRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /participants` : Create a new participant.
     *
     * @param participant the participant to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new participant, or with status `400 (Bad Request)` if the participant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/participants")
    fun createParticipant(@RequestBody participant: Participant): ResponseEntity<Participant> {
        log.debug("REST request to save Participant : $participant")
        if (participant.id != null) {
            throw BadRequestAlertException(
                "A new participant cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = participantRepository.save(participant)
        return ResponseEntity.created(URI("/api/participants/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /participants` : Updates an existing participant.
     *
     * @param participant the participant to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated participant,
     * or with status `400 (Bad Request)` if the participant is not valid,
     * or with status `500 (Internal Server Error)` if the participant couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/participants")
    fun updateParticipant(@RequestBody participant: Participant): ResponseEntity<Participant> {
        log.debug("REST request to update Participant : $participant")
        if (participant.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = participantRepository.save(participant)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    participant.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /participants` : get all the participants.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of participants in body.
     */
    @GetMapping("/participants")
    fun getAllParticipants(): MutableList<Participant> {
        log.debug("REST request to get all Participants")
        return participantRepository.findAll()
    }

    /**
     * `GET  /participants/:id` : get the "id" participant.
     *
     * @param id the id of the participant to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the participant, or with status `404 (Not Found)`.
     */
    @GetMapping("/participants/{id}")
    fun getParticipant(@PathVariable id: Long): ResponseEntity<Participant> {
        log.debug("REST request to get Participant : $id")
        val participant = participantRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(participant)
    }

    /**
     *  `DELETE  /participants/:id` : delete the "id" participant.
     *
     * @param id the id of the participant to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/participants/{id}")
    fun deleteParticipant(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Participant : $id")

        val participant = participantRepository.getOne(id)
        val team = participant.team
        team?.removeParticipant(participant)
        participantRepository.deleteById(id)
        log.info("Participant $id exists: ${participantRepository.existsById(id)}")
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
