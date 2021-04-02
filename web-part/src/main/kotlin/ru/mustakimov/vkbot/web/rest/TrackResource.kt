package ru.mustakimov.vkbot.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import ru.mustakimov.vkbot.domain.Track
import ru.mustakimov.vkbot.repository.TrackRepository
import ru.mustakimov.vkbot.web.rest.errors.BadRequestAlertException
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid

private const val ENTITY_NAME = "track"

/**
 * REST controller for managing [ru.mustakimov.vkbot.domain.Track].
 */
@RestController
@RequestMapping("/api")
@Transactional
class TrackResource(
    private val trackRepository: TrackRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /tracks` : Create a new track.
     *
     * @param track the track to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new track, or with status `400 (Bad Request)` if the track has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tracks")
    fun createTrack(@Valid @RequestBody track: Track): ResponseEntity<Track> {
        log.debug("REST request to save Track : $track")
        if (track.id != null) {
            throw BadRequestAlertException(
                "A new track cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = trackRepository.save(track)
        return ResponseEntity.created(URI("/api/tracks/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * `PUT  /tracks` : Updates an existing track.
     *
     * @param track the track to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated track,
     * or with status `400 (Bad Request)` if the track is not valid,
     * or with status `500 (Internal Server Error)` if the track couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tracks")
    fun updateTrack(@Valid @RequestBody track: Track): ResponseEntity<Track> {
        log.debug("REST request to update Track : $track")
        if (track.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = trackRepository.save(track)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    track.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /tracks` : get all the tracks.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of tracks in body.
     */
    @GetMapping("/tracks")
    fun getAllTracks(): MutableList<Track> {
        log.debug("REST request to get all Tracks")
        return trackRepository.findAll()
    }

    /**
     * `GET  /tracks/:id` : get the "id" track.
     *
     * @param id the id of the track to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the track, or with status `404 (Not Found)`.
     */
    @GetMapping("/tracks/{id}")
    fun getTrack(@PathVariable id: Long): ResponseEntity<Track> {
        log.debug("REST request to get Track : $id")
        val track = trackRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(track)
    }

    /**
     *  `DELETE  /tracks/:id` : delete the "id" track.
     *
     * @param id the id of the track to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/tracks/{id}")
    fun deleteTrack(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Track : $id")

        trackRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
