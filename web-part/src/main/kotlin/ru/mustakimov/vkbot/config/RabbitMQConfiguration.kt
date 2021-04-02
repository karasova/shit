package ru.mustakimov.vkbot.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfiguration {
    @Value("\${application.messaging.bot.mainQueue}")
    lateinit var botQueue: String

    @Value("\${application.messaging.bot.retryQueue}")
    lateinit var botRetryQueue: String

    @Value("\${application.messaging.human.mainQueue}")
    lateinit var humanQueue: String

    @Value("\${application.messaging.human.retryQueue}")
    lateinit var humanRetryQueue: String

    val messageStatusQueue = "message_status"
    val messageStatusRetryQueue = "message_status_retry"

    @Bean
    @Qualifier("bot")
    fun botExchange(): Exchange = ExchangeBuilder
        .fanoutExchange(botQueue)
        .durable(true)
        .build()

    @Bean
    @Qualifier("bot_retry")
    fun botRetryExchange(): Exchange = ExchangeBuilder
        .directExchange(botRetryQueue)
        .durable(true)
        .build()

    @Bean
    @Qualifier("bot")
    fun botQueue(): Queue = QueueBuilder
        .durable(botQueue)
        .deadLetterExchange(botRetryQueue)
        .build()

    @Bean
    @Qualifier("bot_retry")
    fun botRetryQueue(): Queue = QueueBuilder
        .durable(botRetryQueue)
        .deadLetterExchange(botQueue)
        .ttl(300_000)
        .build()

    @Bean
    fun botBinding(
        @Qualifier("bot") queue: Queue,
        @Qualifier("bot") exchange: Exchange
    ): Binding = BindingBuilder.bind(queue).to(exchange).with("").noargs()

    @Bean
    fun botRetryBinding(
        @Qualifier("bot_retry") queue: Queue,
        @Qualifier("bot_retry") exchange: Exchange
    ): Binding = BindingBuilder.bind(queue).to(exchange).with("").noargs()

    // -------- HUMAN ----------

    @Bean
    @Qualifier("human")
    fun humanExchange(): Exchange = ExchangeBuilder
        .fanoutExchange(humanQueue)
        .durable(true)
        .build()

    @Bean
    @Qualifier("human_retry")
    fun humanRetryExchange(): Exchange = ExchangeBuilder
        .directExchange(humanRetryQueue)
        .durable(true)
        .build()

    @Bean
    @Qualifier("human")
    fun humanQueue(): Queue = QueueBuilder
        .durable(humanQueue)
        .deadLetterExchange(humanRetryQueue)
        .build()

    @Bean
    @Qualifier("human_retry")
    fun humanRetryQueue(): Queue = QueueBuilder
        .durable(humanRetryQueue)
        .deadLetterExchange(humanQueue)
        .ttl(300_000)
        .build()

    @Bean
    fun humanBinding(
        @Qualifier("human") queue: Queue,
        @Qualifier("human") exchange: Exchange
    ): Binding = BindingBuilder.bind(queue).to(exchange).with("").noargs()

    @Bean
    fun humanRetryBinding(
        @Qualifier("human_retry") queue: Queue,
        @Qualifier("human_retry") exchange: Exchange
    ): Binding = BindingBuilder.bind(queue).to(exchange).with("").noargs()

    // ---------- Message status ------------

    @Bean
    @Qualifier("message_status")
    fun messageStatusExchange(): Exchange = ExchangeBuilder
        .fanoutExchange(messageStatusQueue)
        .durable(true)
        .build()

    @Bean
    @Qualifier("message_status_retry")
    fun messageStatusRetryExchange(): Exchange = ExchangeBuilder
        .directExchange(messageStatusRetryQueue)
        .durable(true)
        .build()

    @Bean
    @Qualifier("message_status")
    fun messageStatusQueue(): Queue = QueueBuilder
        .durable(messageStatusQueue)
        .deadLetterExchange(messageStatusRetryQueue)
        .build()

    @Bean
    @Qualifier("message_status_retry")
    fun messageStatusRetryQueue(): Queue = QueueBuilder
        .durable(messageStatusRetryQueue)
        .deadLetterExchange(messageStatusQueue)
        .ttl(300_000)
        .build()

    @Bean
    fun messageStatusBinding(
        @Qualifier("message_status") queue: Queue,
        @Qualifier("message_status") exchange: Exchange
    ): Binding = BindingBuilder.bind(queue).to(exchange).with("").noargs()

    @Bean
    fun messageStatusRetryBinding(
        @Qualifier("message_status_retry") queue: Queue,
        @Qualifier("message_status_retry") exchange: Exchange
    ): Binding = BindingBuilder.bind(queue).to(exchange).with("").noargs()

    @Bean
    @Qualifier("bot")
    fun rabbitTemplate(): AmqpTemplate = RabbitTemplate(CachingConnectionFactory())

    @Bean
    fun messageConverter(): MessageConverter = Jackson2JsonMessageConverter()
}
