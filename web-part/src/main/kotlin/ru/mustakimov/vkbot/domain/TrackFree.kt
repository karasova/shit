package ru.mustakimov.vkbot.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.Immutable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "track_free")
@Immutable
class TrackFree {
    @Id
    @Column(name = "track_id")
    @JsonIgnore
    var trackId: Long? = null

    @Column(name = "teams_count")
    @JsonProperty("count")
    var teamsCount: Long? = null

    @Column(name = "track_remaining")
    @JsonProperty("remaining")
    var trackRemaining: Long? = null

    @Column(name = "track_max")
    @JsonProperty("max")
    var trackMax: Long? = null
}
