package ru.mustakimov.vkbot.web.rest

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import ru.mustakimov.vkbot.BotApp
import ru.mustakimov.vkbot.domain.Team
import ru.mustakimov.vkbot.domain.enumeration.TeamStatus
import ru.mustakimov.vkbot.repository.TeamRepository
import ru.mustakimov.vkbot.web.rest.errors.ExceptionTranslator
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [TeamResource] REST controller.
 *
 * @see TeamResource
 */
@SpringBootTest(classes = [BotApp::class])
@AutoConfigureMockMvc
@WithMockUser
class TeamResourceIT {

    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    private lateinit var restTeamMockMvc: MockMvc

    private lateinit var team: Team

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val teamResource = TeamResource(teamRepository)
        this.restTeamMockMvc = MockMvcBuilders.standaloneSetup(teamResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        team = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createTeam() {
        val databaseSizeBeforeCreate = teamRepository.findAll().size

        // Create the Team
        restTeamMockMvc.perform(
            post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(team))
        ).andExpect(status().isCreated)

        // Validate the Team in the database
        val teamList = teamRepository.findAll()
        assertThat(teamList).hasSize(databaseSizeBeforeCreate + 1)
        val testTeam = teamList[teamList.size - 1]
        assertThat(testTeam.title).isEqualTo(DEFAULT_TITLE)
        assertThat(testTeam.status).isEqualTo(DEFAULT_STATUS)
        assertThat(testTeam.comment).isEqualTo(DEFAULT_COMMENT)
    }

    @Test
    @Transactional
    fun createTeamWithExistingId() {
        val databaseSizeBeforeCreate = teamRepository.findAll().size

        // Create the Team with an existing ID
        team.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamMockMvc.perform(
            post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(team))
        ).andExpect(status().isBadRequest)

        // Validate the Team in the database
        val teamList = teamRepository.findAll()
        assertThat(teamList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = teamRepository.findAll().size
        // set the field null
        team.title = null

        // Create the Team, which fails.

        restTeamMockMvc.perform(
            post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(team))
        ).andExpect(status().isBadRequest)

        val teamList = teamRepository.findAll()
        assertThat(teamList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkStatusIsRequired() {
        val databaseSizeBeforeTest = teamRepository.findAll().size
        // set the field null
        team.status = null

        // Create the Team, which fails.

        restTeamMockMvc.perform(
            post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(team))
        ).andExpect(status().isBadRequest)

        val teamList = teamRepository.findAll()
        assertThat(teamList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllTeams() {
        // Initialize the database
        teamRepository.saveAndFlush(team)

        // Get all the teamList
        restTeamMockMvc.perform(get("/api/teams?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(team.id?.toInt())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getTeam() {
        // Initialize the database
        teamRepository.saveAndFlush(team)

        val id = team.id
        assertNotNull(id)

        // Get the team
        restTeamMockMvc.perform(get("/api/teams/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(team.id?.toInt()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingTeam() {
        // Get the team
        restTeamMockMvc.perform(get("/api/teams/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }

    @Test
    @Transactional
    fun updateTeam() {
        // Initialize the database
        teamRepository.saveAndFlush(team)

        val databaseSizeBeforeUpdate = teamRepository.findAll().size

        // Update the team
        val id = team.id
        assertNotNull(id)
        val updatedTeam = teamRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTeam are not directly saved in db
        em.detach(updatedTeam)
        updatedTeam.title = UPDATED_TITLE
        updatedTeam.status = UPDATED_STATUS
        updatedTeam.comment = UPDATED_COMMENT

        restTeamMockMvc.perform(
            put("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedTeam))
        ).andExpect(status().isOk)

        // Validate the Team in the database
        val teamList = teamRepository.findAll()
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate)
        val testTeam = teamList[teamList.size - 1]
        assertThat(testTeam.title).isEqualTo(UPDATED_TITLE)
        assertThat(testTeam.status).isEqualTo(UPDATED_STATUS)
        assertThat(testTeam.comment).isEqualTo(UPDATED_COMMENT)
    }

    @Test
    @Transactional
    fun updateNonExistingTeam() {
        val databaseSizeBeforeUpdate = teamRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamMockMvc.perform(
            put("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(team))
        ).andExpect(status().isBadRequest)

        // Validate the Team in the database
        val teamList = teamRepository.findAll()
        assertThat(teamList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteTeam() {
        // Initialize the database
        teamRepository.saveAndFlush(team)

        val databaseSizeBeforeDelete = teamRepository.findAll().size

        // Delete the team
        restTeamMockMvc.perform(
            delete("/api/teams/{id}", team.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val teamList = teamRepository.findAll()
        assertThat(teamList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        private val DEFAULT_STATUS: TeamStatus = TeamStatus.ADDED
        private val UPDATED_STATUS: TeamStatus = TeamStatus.CASE_SELECTION

        private const val DEFAULT_COMMENT = "AAAAAAAAAA"
        private const val UPDATED_COMMENT = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Team {
            val team = Team(
                title = DEFAULT_TITLE,
                status = DEFAULT_STATUS,
                comment = DEFAULT_COMMENT
            )

            return team
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Team {
            val team = Team(
                title = UPDATED_TITLE,
                status = UPDATED_STATUS,
                comment = UPDATED_COMMENT
            )

            return team
        }
    }
}
