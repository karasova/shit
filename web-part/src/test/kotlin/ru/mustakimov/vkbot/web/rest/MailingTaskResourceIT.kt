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
import ru.mustakimov.vkbot.domain.MailingTask
import ru.mustakimov.vkbot.domain.enumeration.TeamStatus
import ru.mustakimov.vkbot.repository.MailingTaskRepository
import ru.mustakimov.vkbot.web.rest.errors.ExceptionTranslator
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [MailingTaskResource] REST controller.
 *
 * @see MailingTaskResource
 */
@SpringBootTest(classes = [BotApp::class])
@AutoConfigureMockMvc
@WithMockUser
class MailingTaskResourceIT {

    @Autowired
    private lateinit var mailingTaskRepository: MailingTaskRepository

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

    private lateinit var restMailingTaskMockMvc: MockMvc

    private lateinit var mailingTask: MailingTask

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val mailingTaskResource = MailingTaskResource(mailingTaskRepository)
        this.restMailingTaskMockMvc = MockMvcBuilders.standaloneSetup(mailingTaskResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        mailingTask = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createMailingTask() {
        val databaseSizeBeforeCreate = mailingTaskRepository.findAll().size

        // Create the MailingTask
        restMailingTaskMockMvc.perform(
            post("/api/mailing-tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(mailingTask))
        ).andExpect(status().isCreated)

        // Validate the MailingTask in the database
        val mailingTaskList = mailingTaskRepository.findAll()
        assertThat(mailingTaskList).hasSize(databaseSizeBeforeCreate + 1)
        val testMailingTask = mailingTaskList[mailingTaskList.size - 1]
        assertThat(testMailingTask.plannedTime).isEqualTo(DEFAULT_PLANNED_TIME)
        assertThat(testMailingTask.filterStatus).isEqualTo(DEFAULT_FILTER_STATUS)
        assertThat(testMailingTask.message).isEqualTo(DEFAULT_MESSAGE)
    }

    @Test
    @Transactional
    fun createMailingTaskWithExistingId() {
        val databaseSizeBeforeCreate = mailingTaskRepository.findAll().size

        // Create the MailingTask with an existing ID
        mailingTask.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restMailingTaskMockMvc.perform(
            post("/api/mailing-tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(mailingTask))
        ).andExpect(status().isBadRequest)

        // Validate the MailingTask in the database
        val mailingTaskList = mailingTaskRepository.findAll()
        assertThat(mailingTaskList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkPlannedTimeIsRequired() {
        val databaseSizeBeforeTest = mailingTaskRepository.findAll().size
        // set the field null
        mailingTask.plannedTime = null

        // Create the MailingTask, which fails.

        restMailingTaskMockMvc.perform(
            post("/api/mailing-tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(mailingTask))
        ).andExpect(status().isBadRequest)

        val mailingTaskList = mailingTaskRepository.findAll()
        assertThat(mailingTaskList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    fun checkMessageIsRequired() {
        val databaseSizeBeforeTest = mailingTaskRepository.findAll().size
        // set the field null
        mailingTask.message = null

        // Create the MailingTask, which fails.

        restMailingTaskMockMvc.perform(
            post("/api/mailing-tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(mailingTask))
        ).andExpect(status().isBadRequest)

        val mailingTaskList = mailingTaskRepository.findAll()
        assertThat(mailingTaskList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllMailingTasks() {
        // Initialize the database
        mailingTaskRepository.saveAndFlush(mailingTask)

        // Get all the mailingTaskList
        restMailingTaskMockMvc.perform(get("/api/mailing-tasks?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mailingTask.id?.toInt())))
            .andExpect(jsonPath("$.[*].plannedTime").value(hasItem(DEFAULT_PLANNED_TIME.toString())))
            .andExpect(jsonPath("$.[*].filterStatus").value(hasItem(DEFAULT_FILTER_STATUS.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getMailingTask() {
        // Initialize the database
        mailingTaskRepository.saveAndFlush(mailingTask)

        val id = mailingTask.id
        assertNotNull(id)

        // Get the mailingTask
        restMailingTaskMockMvc.perform(get("/api/mailing-tasks/{id}", id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mailingTask.id?.toInt()))
            .andExpect(jsonPath("$.plannedTime").value(DEFAULT_PLANNED_TIME.toString()))
            .andExpect(jsonPath("$.filterStatus").value(DEFAULT_FILTER_STATUS.toString()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingMailingTask() {
        // Get the mailingTask
        restMailingTaskMockMvc.perform(get("/api/mailing-tasks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun updateMailingTask() {
        // Initialize the database
        mailingTaskRepository.saveAndFlush(mailingTask)

        val databaseSizeBeforeUpdate = mailingTaskRepository.findAll().size

        // Update the mailingTask
        val id = mailingTask.id
        assertNotNull(id)
        val updatedMailingTask = mailingTaskRepository.findById(id).get()
        // Disconnect from session so that the updates on updatedMailingTask are not directly saved in db
        em.detach(updatedMailingTask)
        updatedMailingTask.plannedTime = UPDATED_PLANNED_TIME
        updatedMailingTask.filterStatus = UPDATED_FILTER_STATUS
        updatedMailingTask.message = UPDATED_MESSAGE

        restMailingTaskMockMvc.perform(
            put("/api/mailing-tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedMailingTask))
        ).andExpect(status().isOk)

        // Validate the MailingTask in the database
        val mailingTaskList = mailingTaskRepository.findAll()
        assertThat(mailingTaskList).hasSize(databaseSizeBeforeUpdate)
        val testMailingTask = mailingTaskList[mailingTaskList.size - 1]
        assertThat(testMailingTask.plannedTime).isEqualTo(UPDATED_PLANNED_TIME)
        assertThat(testMailingTask.filterStatus).isEqualTo(UPDATED_FILTER_STATUS)
        assertThat(testMailingTask.message).isEqualTo(UPDATED_MESSAGE)
    }

    @Test
    @Transactional
    fun updateNonExistingMailingTask() {
        val databaseSizeBeforeUpdate = mailingTaskRepository.findAll().size

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMailingTaskMockMvc.perform(
            put("/api/mailing-tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(mailingTask))
        ).andExpect(status().isBadRequest)

        // Validate the MailingTask in the database
        val mailingTaskList = mailingTaskRepository.findAll()
        assertThat(mailingTaskList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteMailingTask() {
        // Initialize the database
        mailingTaskRepository.saveAndFlush(mailingTask)

        val databaseSizeBeforeDelete = mailingTaskRepository.findAll().size

        // Delete the mailingTask
        restMailingTaskMockMvc.perform(
            delete("/api/mailing-tasks/{id}", mailingTask.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val mailingTaskList = mailingTaskRepository.findAll()
        assertThat(mailingTaskList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private val DEFAULT_PLANNED_TIME: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_PLANNED_TIME: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_FILTER_STATUS: TeamStatus = TeamStatus.ADDED
        private val UPDATED_FILTER_STATUS: TeamStatus = TeamStatus.CASE_SELECTION

        private const val DEFAULT_MESSAGE = "AAAAAAAAAA"
        private const val UPDATED_MESSAGE = "BBBBBBBBBB"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): MailingTask {
            val mailingTask = MailingTask(
                plannedTime = DEFAULT_PLANNED_TIME,
                filterStatus = DEFAULT_FILTER_STATUS,
                message = DEFAULT_MESSAGE
            )

            return mailingTask
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): MailingTask {
            val mailingTask = MailingTask(
                plannedTime = UPDATED_PLANNED_TIME,
                filterStatus = UPDATED_FILTER_STATUS,
                message = UPDATED_MESSAGE
            )

            return mailingTask
        }
    }
}
