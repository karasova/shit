package ru.mustakimov.vkbot.web.rest

import io.github.jhipster.web.util.HeaderUtil
import io.github.jhipster.web.util.ResponseUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.mustakimov.vkbot.domain.Team
import ru.mustakimov.vkbot.repository.Converter
import ru.mustakimov.vkbot.repository.TeamRepository
import ru.mustakimov.vkbot.web.rest.errors.BadRequestAlertException
import java.net.URI
import java.net.URISyntaxException
import javax.validation.Valid

private const val ENTITY_NAME = "team"

/**
 * REST controller for managing [ru.mustakimov.vkbot.domain.Team].
 */
@RestController
@RequestMapping("/api")
@Transactional
class TeamResource(
    private val teamRepository: TeamRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /teams` : Create a new team.
     *
     * @param team the team to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new team, or with status `400 (Bad Request)` if the team has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/teams")
    fun createTeam(@Valid @RequestBody team: Team): ResponseEntity<Team> {
        log.debug("REST request to save Team : $team")
        if (team.id != null) {
            throw BadRequestAlertException(
                "A new team cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = teamRepository.save(team)
        return ResponseEntity.created(URI("/api/teams/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    @PostMapping("/teamsupload")
    fun importTeams(@Valid @RequestParam(value = "file", required = true) file: MultipartFile) {
        log.debug("REST request to import teams")
        val converter = Converter()

        val teams = converter.convert(file.inputStream)

        teamRepository.saveAll(teams)
    }

    /**
     * `PUT  /teams` : Updates an existing team.
     *
     * @param team the team to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated team,
     * or with status `400 (Bad Request)` if the team is not valid,
     * or with status `500 (Internal Server Error)` if the team couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/teams")
    fun updateTeam(@Valid @RequestBody team: Team): ResponseEntity<Team> {
        log.debug("REST request to update Team : $team")
        if (team.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        val result = teamRepository.save(team)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    team.id.toString()
                )
            )
            .body(result)
    }

    /**
     * `GET  /teams` : get all the teams.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of teams in body.
     */
    @GetMapping("/teams")
    fun getAllTeams(): MutableList<Team> {
        log.debug("REST request to get all Teams")
        return teamRepository.findAll()
    }

    /**
     * `GET  /teams/:id` : get the "id" team.
     *
     * @param id the id of the team to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the team, or with status `404 (Not Found)`.
     */
    @GetMapping("/teams/{id}")
    fun getTeam(@PathVariable id: Long): ResponseEntity<Team> {
        log.debug("REST request to get Team : $id")
        val team = teamRepository.findById(id)
        return ResponseUtil.wrapOrNotFound(team)
    }

    /**
     *  `DELETE  /teams/:id` : delete the "id" team.
     *
     * @param id the id of the team to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/teams/{id}")
    fun deleteTeam(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Team : $id")

        teamRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
