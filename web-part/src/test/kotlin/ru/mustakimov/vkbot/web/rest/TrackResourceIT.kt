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
import ru.mustakimov.vkbot.domain.Track
import ru.mustakimov.vkbot.repository.TrackRepository
import ru.mustakimov.vkbot.web.rest.errors.ExceptionTranslator
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [TrackResource] REST controller.
 *
 * @see TrackResource
 */
@SpringBootTest(classes = [BotApp::class])
@AutoConfigureMockMvc
@WithMockUser
class TrackResourceIT {

    @Autowired
    private lateinit var trackRepository: TrackRepository

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

    private lateinit var restTrackMockMvc: MockMvc

    private lateinit var track: Track

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val trackResource = TrackResource(trackRepository)
        this.restTrackMockMvc = MockMvcBuilders.standaloneSetup(trackResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        track = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createTrack() {
        val databaseSizeBeforeCreate = trackRepository.findAll().size

        // Create the Track
        restTrackMockMvc.perform(
            post("/api/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(track))
        ).andExpect(status().isCreated)

        // Validate the Track in the database
        val trackList = trackRepository.findAll()
        assertThat(trackList).hasSize(databaseSizeBeforeCreate + 1)
        val testTrack = trackList[trackList.size - 1]
        assertThat(testTrack.title).isEqualTo(DEFAULT_TITLE)
    }

    @Test
    @Transactional
    fun createTrackWithExistingId() {
        val databaseSizeBeforeCreate = trackRepository.findAll().size

        // Create the Track with an existing ID
        track.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrackMockMvc.perform(
            post("/api/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(track))
        ).andExpect(status().isBadRequest)

        // Validate the Track in the database
        val trackList = trackRepository.findAll()
        assertThat(trackList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkTitleIsRequired() {
        val databaseSizeBeforeTest = trackRepository.findAll().size
        // set the field null
        track.title = null

        // Create the Track, which fails.

        restTrackMockMvc.perform(
            post("/api/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(track))
        ).andExpect(status().isBadRequest)

        val trackList = trackRepository.findAll()
        assertThat(trackList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllTracks() {
        // Initialize the database
        trackRepository.saveAndFlush(track)

        // Get all the trackList
        restTrackMockMvc.perform(get("/api/tracks?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(track.id?.toInt())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getTrack() {
        // Initialize the database
        trackRepository.saveAndFlush(track)

        val id = track.id
        assertNotNull(id)

        // Get the track
        restTrackMockMvc.perform(get("/api/tracks/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(track.id?.toInt()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingTrack() {
        // Get the track
        restTrackMockMvc.perform(get("/api/tracks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateTrack() {
        // Initialize the database
        trackRepository.saveAndFlush(track)

        val databaseSizeBeforeUpdate = trackRepository.findAll().size

        // Update the track
        val id = track.id
        assertNotNull(id)
        val updatedTrack = trackRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedTrack are not directly saved in db
        em.detach(updatedTrack)
        updatedTrack.title = UPDATED_TITLE

        restTrackMockMvc.perform(
            put("/api/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedTrack))
        ).andExpect(status().isOk)

        // Validate the Track in the database
        val trackList = trackRepository.findAll()
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate)
        val testTrack = trackList[trackList.size - 1]
        assertThat(testTrack.title).isEqualTo(UPDATED_TITLE)
    }

    @Test
    @Transactional
    fun updateNonExistingTrack() {
        val databaseSizeBeforeUpdate = trackRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrackMockMvc.perform(
            put("/api/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(track))
        ).andExpect(status().isBadRequest)

        // Validate the Track in the database
        val trackList = trackRepository.findAll()
        assertThat(trackList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteTrack() {
        // Initialize the database
        trackRepository.saveAndFlush(track)

        val databaseSizeBeforeDelete = trackRepository.findAll().size

        // Delete the track
        restTrackMockMvc.perform(
            delete("/api/tracks/{id}", track.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val trackList = trackRepository.findAll()
        assertThat(trackList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_TITLE = "AAAAAAAAAA"
        private const val UPDATED_TITLE = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Track {
            val track = Track(
                title = DEFAULT_TITLE
            )

            return track
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Track {
            val track = Track(
                title = UPDATED_TITLE
            )

            return track
        }
    }
}
