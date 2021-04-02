package ru.mustakimov.vkbot.service

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import ru.mustakimov.vkbot.domain.MailingTask
import ru.mustakimov.vkbot.domain.Participant
import ru.mustakimov.vkbot.domain.Team
import ru.mustakimov.vkbot.domain.Track
import ru.mustakimov.vkbot.domain.enumeration.MailingStatus
import ru.mustakimov.vkbot.domain.enumeration.MailingType
import ru.mustakimov.vkbot.domain.enumeration.TeamStatus
import ru.mustakimov.vkbot.repository.MailingTaskRepository
import ru.mustakimov.vkbot.repository.ParticipantRepository
import ru.mustakimov.vkbot.repository.TeamRepository
import ru.mustakimov.vkbot.repository.TrackRepository
import ru.mustakimov.vkbot.service.dto.*
import java.time.Instant
import javax.transaction.Transactional
import kotlin.random.Random

@Service
class MessagingService(
    private val mailingTaskRepository: MailingTaskRepository,
    private val teamRepository: TeamRepository,
    private val trackRepository: TrackRepository,
    private val participantRepository: ParticipantRepository,
    private val template: AmqpTemplate
) {
    @Value("\${application.messaging.bot.mainQueue}")
    lateinit var botExchange: String

    private val log = LoggerFactory.getLogger(javaClass)

    @Async
    fun sendAllPendingMessages(moment: Instant = Instant.now()) {
        val plannedMailings = mailingTaskRepository.findAllByPlannedTimeBeforeAndMailingStatusAdded(moment)
        for (task in plannedMailings) {
            sendMessagesInMailingTask(task)
        }
    }

    @Transactional
    fun sendMessagesInMailingTask(task: MailingTask) {
        log.info("Send messages based on task: $task")

        val status = task.filterStatus
        val case = task.filterCase
        val teams = when {
            status != null && case != null ->
                teamRepository.findAllByStatusAndCase(status, case)
            status != null -> teamRepository.findAllByStatus(status)
            else -> error("Mailing list cannot have status=null")
        }
        val id = task.id ?: error("Task should be connected to DB")
        val message = task.message ?: error("Task should have message")

        when (task.mailingType ?: error("Task should have type")) {
            MailingType.STANDARD -> {
                val vkIds = teams.flatMap(Team::participants).mapNotNull(Participant::vkId)
                vkIds.forEach {
                    sendMessage(id, message, listOf(it))
                }
            }
            MailingType.SELECT_CASE -> {
                val tracks = trackRepository.findAll()
                teams.forEach {
                    sendCaseSelectionButtonsToTeam(id, message, it, tracks)
                    it.status = TeamStatus.CASE_SELECTION
                    teamRepository.save(it)
                }
            }
        }

        task.mailingStatus = MailingStatus.SENT
        mailingTaskRepository.save(task)

        log.info("Messages sent based on task ${task.id}")
    }

    fun handleUserMessage(userMessage: String, userId: Long) {
        val user = participantRepository.findByVkId(userId)
        if (user.isEmpty) return
        val team = user.get().team ?: return
        if (team.status == TeamStatus.CASE_SELECTED) {
            val track = trackRepository.findOneByTitle(userMessage)
            if (track.isPresent) {
                sendMessage(Random.nextLong(), "Ой, ты уже выбрал кейс — с тебя хватит \uD83D\uDE31", listOf(userId))
                return
            }
        }
        if (team.status != TeamStatus.CASE_SELECTION) return

        val track = trackRepository.findOneByTitle(userMessage)
        if (track.isEmpty) {
            sendMessage(Random.nextLong(), "Мы не нашли такой кейс… Повтори, пожалуйста", listOf(userId))
            return
        }

        val selectedTrack = track.get()
        if (selectedTrack.derived!!.trackRemaining!! <= 0) {
            sendMessage(Random.nextLong(), "Ой, а этот кейс уже переполнен… Попробуй другие", listOf(userId))
            return
        }

        team.case = track.get()
        team.status = TeamStatus.CASE_SELECTED
        sendMessage(Random.nextLong(), "Классный кейс! Мы запомнили ;)", listOf(userId))
        teamRepository.save(team)
    }

    private fun sendCaseSelectionButtonsToTeam(seed: Long, messageText: String, team: Team, tracks: List<Track>) {
        val keyboard = Keyboard(
            type = KeyboardType.INLINE,
            oneTime = false,
            items = tracks.map {
                KeyboardButton.TextKeyboardButton(
                    label = it.title!!,
                    payload = "",
                    color = KeyboardColor.POSITIVE
                )
            }
        )
        val mailingTaskDTO = MailingTaskDTO(
            seed = seed,
            vkIds = team.participants.mapNotNull { it.vkId },
            message = Message(
                text = messageText,
                keyboard = keyboard
            )
        )
        sendMessage(mailingTaskDTO)
    }

    private fun sendMessage(seed: Long, message: String, users: Iterable<Long>) {
        sendMessage(
            MailingTaskDTO(
                seed,
                users.toList(),
                Message(message)
            )
        )
    }

    private fun sendMessage(message: MailingTaskDTO) {
        template.convertAndSend(botExchange, "", message)
    }
}
