package ru.mustakimov.vkbot.web.cron

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.mustakimov.vkbot.service.MessagingService

@Service
class MailingCronService(
    private val messagingService: MessagingService
) {

    @Scheduled(cron = CRON_EVERY_MINUTE)
    fun sendMessagesToUsers() {
        messagingService.sendAllPendingMessages()
    }

    companion object {
        private const val CRON_EVERY_MINUTE = "*/10 * * * * *"
    }
}
