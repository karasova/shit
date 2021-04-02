package ru.mustakimov.vkbot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource

/**
 * Properties specific to Bot.
 *
 * Properties are configured in the `application.yml` file.
 * See [io.github.jhipster.config.JHipsterProperties] for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@PropertySource("classpath:META-INF/additional-spring-configuration-metadata.json")
class ApplicationProperties {
    var messaging: Messaging = Messaging()

    data class Messaging(
        var bot: QueueOptions = QueueOptions(),
        var human: QueueOptions = QueueOptions()
    )

    data class QueueOptions(
        var mainQueue: String = "",
        var retryQueue: String = "",
        var retryLifetime: Long = 300_000
    )
}
