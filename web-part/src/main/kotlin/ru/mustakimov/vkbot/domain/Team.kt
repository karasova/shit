package ru.mustakimov.vkbot.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import ru.mustakimov.vkbot.domain.enumeration.TeamStatus
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Team.
 */
@Entity
@Table(name = "team")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Team(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @get: NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: TeamStatus? = null,

    @Column(name = "comment")
    var comment: String? = null,

    @OneToOne @JoinColumn(unique = true)
    @JsonIgnoreProperties(value = ["team"])
    var registrator: Participant? = null,

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.DETACH], orphanRemoval = true)
    @JsonIgnoreProperties(value = ["team"])
    @JsonProperty("registrator", access = JsonProperty.Access.READ_ONLY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var participants: MutableSet<Participant> = mutableSetOf(),

    @ManyToOne @JsonIgnoreProperties(value = ["teams"], allowSetters = true)
    var case: Track? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addParticipant(participant: Participant): Team {
        this.participants.add(participant)
        participant.team = this
        return this
    }

    fun removeParticipant(participant: Participant): Team {
        this.participants.remove(participant)
        participant.team = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Team) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Team{" +
        "id=$id" +
        ", title='$title'" +
        ", status='$status'" +
        ", comment='$comment'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
