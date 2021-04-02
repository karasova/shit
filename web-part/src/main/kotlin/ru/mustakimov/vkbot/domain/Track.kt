package ru.mustakimov.vkbot.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Track.
 */
@Entity
@Table(name = "track")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Track(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @get: NotNull
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @OneToMany(mappedBy = "case")
    @set:JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var teams: MutableSet<Team> = mutableSetOf(),

    @OneToMany(mappedBy = "filterCase")
    @set:JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    var mailingTasks: MutableSet<MailingTask> = mutableSetOf(),

    @OneToOne
    @PrimaryKeyJoinColumn(name = "track_id")
    var derived: TrackFree? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addTeam(team: Team): Track {
        this.teams.add(team)
        team.case = this
        return this
    }

    fun removeTeam(team: Team): Track {
        this.teams.remove(team)
        team.case = null
        return this
    }

    fun addMailingTask(mailingTask: MailingTask): Track {
        this.mailingTasks.add(mailingTask)
        mailingTask.filterCase = this
        return this
    }

    fun removeMailingTask(mailingTask: MailingTask): Track {
        this.mailingTasks.remove(mailingTask)
        mailingTask.filterCase = null
        return this
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Track) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Track{" +
        "id=$id" +
        ", title='$title'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
