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
import ru.mustakimov.vkbot.domain.Participant
import ru.mustakimov.vkbot.repository.ParticipantRepository
import ru.mustakimov.vkbot.repository.TeamRepository
import ru.mustakimov.vkbot.web.rest.errors.ExceptionTranslator
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [ParticipantResource] REST controller.
 *
 * @see ParticipantResource
 */
@SpringBootTest(classes = [BotApp::class])
@AutoConfigureMockMvc
@WithMockUser
class ParticipantResourceIT {

    @Autowired
    private lateinit var participantRepository: ParticipantRepository

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

    private lateinit var restParticipantMockMvc: MockMvc

    private lateinit var participant: Participant

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val participantResource = ParticipantResource(participantRepository, teamRepository)
        this.restParticipantMockMvc = MockMvcBuilders.standaloneSetup(participantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        participant = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createParticipant() {
        val databaseSizeBeforeCreate = participantRepository.findAll().size

        // Create the Participant
        restParticipantMockMvc.perform(
            post("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(participant))
        ).andExpect(status().isCreated)

        // Validate the Participant in the database
        val participantList = participantRepository.findAll()
        assertThat(participantList).hasSize(databaseSizeBeforeCreate + 1)
        val testParticipant = participantList[participantList.size - 1]
        assertThat(testParticipant.vkId).isEqualTo(DEFAULT_VK_ID)
        assertThat(testParticipant.fullName).isEqualTo(DEFAULT_FULL_NAME)
        assertThat(testParticipant.age).isEqualTo(DEFAULT_AGE)
        assertThat(testParticipant.employer).isEqualTo(DEFAULT_EMPLOYER)
        assertThat(testParticipant.phoneNumber).isEqualTo(DEFAULT_PHONE_NUMBER)
    }

    @Test
    @Transactional
    fun createParticipantWithExistingId() {
        val databaseSizeBeforeCreate = participantRepository.findAll().size

        // Create the Participant with an existing ID
        participant.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restParticipantMockMvc.perform(
            post("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(participant))
        ).andExpect(status().isBadRequest)

        // Validate the Participant in the database
        val participantList = participantRepository.findAll()
        assertThat(participantList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllParticipants() {
        // Initialize the database
        participantRepository.saveAndFlush(participant)

        // Get all the participantList
        restParticipantMockMvc.perform(get("/api/participants?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(participant.id?.toInt())))
            .andExpect(jsonPath("$.[*].vkId").value(hasItem(DEFAULT_VK_ID?.toInt())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].employer").value(hasItem(DEFAULT_EMPLOYER)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getParticipant() {
        // Initialize the database
        participantRepository.saveAndFlush(participant)

        val id = participant.id
        assertNotNull(id)

        // Get the participant
        restParticipantMockMvc.perform(get("/api/participants/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(participant.id?.toInt()))
            .andExpect(jsonPath("$.vkId").value(DEFAULT_VK_ID?.toInt()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE))
            .andExpect(jsonPath("$.employer").value(DEFAULT_EMPLOYER))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingParticipant() {
        // Get the participant
        restParticipantMockMvc.perform(get("/api/participants/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateParticipant() {
        // Initialize the database
        participantRepository.saveAndFlush(participant)

        val databaseSizeBeforeUpdate = participantRepository.findAll().size

        // Update the participant
        val id = participant.id
        assertNotNull(id)
        val updatedParticipant = participantRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedParticipant are not directly saved in db
        em.detach(updatedParticipant)
        updatedParticipant.vkId = UPDATED_VK_ID
        updatedParticipant.fullName = UPDATED_FULL_NAME
        updatedParticipant.age = UPDATED_AGE
        updatedParticipant.employer = UPDATED_EMPLOYER
        updatedParticipant.phoneNumber = UPDATED_PHONE_NUMBER

        restParticipantMockMvc.perform(
            put("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedParticipant))
        ).andExpect(status().isOk)

        // Validate the Participant in the database
        val participantList = participantRepository.findAll()
        assertThat(participantList).hasSize(databaseSizeBeforeUpdate)
        val testParticipant = participantList[participantList.size - 1]
        assertThat(testParticipant.vkId).isEqualTo(UPDATED_VK_ID)
        assertThat(testParticipant.fullName).isEqualTo(UPDATED_FULL_NAME)
        assertThat(testParticipant.age).isEqualTo(UPDATED_AGE)
        assertThat(testParticipant.employer).isEqualTo(UPDATED_EMPLOYER)
        assertThat(testParticipant.phoneNumber).isEqualTo(UPDATED_PHONE_NUMBER)
    }

    @Test
    @Transactional
    fun updateNonExistingParticipant() {
        val databaseSizeBeforeUpdate = participantRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParticipantMockMvc.perform(
            put("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(participant))
        ).andExpect(status().isBadRequest)

        // Validate the Participant in the database
        val participantList = participantRepository.findAll()
        assertThat(participantList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteParticipant() {
        // Initialize the database
        participantRepository.saveAndFlush(participant)

        val databaseSizeBeforeDelete = participantRepository.findAll().size

        // Delete the participant
        restParticipantMockMvc.perform(
            delete("/api/participants/{id}", participant.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val participantList = participantRepository.findAll()
        assertThat(participantList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_VK_ID: Long = 1L
        private const val UPDATED_VK_ID: Long = 2L

        private const val DEFAULT_FULL_NAME = "AAAAAAAAAA"
        private const val UPDATED_FULL_NAME = "BBBBBBBBBB"

        private const val DEFAULT_AGE: Int = 1
        private const val UPDATED_AGE: Int = 2

        private const val DEFAULT_EMPLOYER = "AAAAAAAAAA"
        private const val UPDATED_EMPLOYER = "BBBBBBBBBB"

        private const val DEFAULT_PHONE_NUMBER = "AAAAAAAAAA"
        private const val UPDATED_PHONE_NUMBER = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Participant {
            val participant = Participant(
                vkId = DEFAULT_VK_ID,
                fullName = DEFAULT_FULL_NAME,
                age = DEFAULT_AGE,
                employer = DEFAULT_EMPLOYER,
                phoneNumber = DEFAULT_PHONE_NUMBER
            )

            return participant
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Participant {
            val participant = Participant(
                vkId = UPDATED_VK_ID,
                fullName = UPDATED_FULL_NAME,
                age = UPDATED_AGE,
                employer = UPDATED_EMPLOYER,
                phoneNumber = UPDATED_PHONE_NUMBER
            )

            return participant
        }
    }
}
