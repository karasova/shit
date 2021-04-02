package ru.mustakimov.vkbot.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.mustakimov.vkbot.web.rest.equalsVerifier

class MailingTaskTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(MailingTask::class)
        val mailingTask1 = MailingTask()
        mailingTask1.id = 1L
        val mailingTask2 = MailingTask()
        mailingTask2.id = mailingTask1.id
        assertThat(mailingTask1).isEqualTo(mailingTask2)
        mailingTask2.id = 2L
        assertThat(mailingTask1).isNotEqualTo(mailingTask2)
        mailingTask1.id = null
        assertThat(mailingTask1).isNotEqualTo(mailingTask2)
    }
}
