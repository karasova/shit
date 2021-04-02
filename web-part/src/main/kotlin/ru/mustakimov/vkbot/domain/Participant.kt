package ru.mustakimov.vkbot.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable
import javax.persistence.*

/**
 * A Participant.
 */
@Entity
@Table(name = "participant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
data class Participant(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null,
    @Column(name = "vk_id")
    var vkId: Long? = null,

    @Column(name = "full_name")
    var fullName: String? = null,

    @Column(name = "age")
    var age: Int? = null,

    @Column(name = "employer")
    var employer: String? = null,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @ManyToOne
    @JsonIgnoreProperties(value = ["participants", "registratorId"], allowSetters = true)
    var team: Team? = null

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Participant) return false

        return id != null && other.id != null && id == other.id
    }

    override fun hashCode() = 31

    override fun toString() = "Participant{" +
        "id=$id" +
        ", vkId=$vkId" +
        ", fullName='$fullName'" +
        ", age=$age" +
        ", employer='$employer'" +
        ", phoneNumber='$phoneNumber'" +
        "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
