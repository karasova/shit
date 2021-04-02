package ru.mustakimov.vkbot.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import ru.mustakimov.vkbot.domain.enumeration.MailingStatus
import ru.mustakimov.vkbot.domain.enumeration.MailingType
import ru.mustakimov.vkbot.domain.enumeration.TeamStatus
import java.io.Serializable
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A MailingTask.
 */
@Entity
@Table(name = "mailing_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class MailingTask(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "planned_time", nullable = false)
    var plannedTime: Instant? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "filter_status")
    var filterStatus: TeamStatus? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JsonProperty("status")
    var mailingStatus: MailingStatus? = MailingStatus.ADDED,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @JsonProperty("type")
    var mailingType: MailingType? = MailingType.STANDARD,

    @get: NotNull
    @get: Size(min = 1)
    @Column(name = "message", nullable = false)
    var message: String? = null,

    @ManyToOne @JsonIgnoreProperties(value = ["mailingTasks"], allowSetters = true)
    var filterCase: Track? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MailingTask) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "MailingTask{" +
        "id=$id" +
        ", plannedTime='$plannedTime'" +
        ", filterStatus='$filterStatus'" +
        ", mailingStatus='$mailingStatus'" +
        ", mailingType='$mailingType'" +
        ", message='$message'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
