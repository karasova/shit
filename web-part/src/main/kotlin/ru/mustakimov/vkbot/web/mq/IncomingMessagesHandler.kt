package ru.mustakimov.vkbot.web.mq

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import ru.mustakimov.vkbot.service.MessagingService
import ru.mustakimov.vkbot.service.dto.HumanMessageDTO

@Service
class IncomingMessagesHandler(
    private val messagingService: MessagingService
) {
    @RabbitListener(queues = ["\${application.messaging.human.mainQueue}"])
    fun processMessage(message: HumanMessageDTO) {
        messagingService.handleUserMessage(message.text, message.fromId)
    }
}
