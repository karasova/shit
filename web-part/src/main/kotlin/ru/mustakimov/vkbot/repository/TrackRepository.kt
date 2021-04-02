package ru.mustakimov.vkbot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.mustakimov.vkbot.domain.Track
import java.util.*

/**
 * Spring Data  repository for the [Track] entity.
 */
@Suppress("unused")
@Repository
interface TrackRepository : JpaRepository<Track, Long> {
    @Query("SELECT t FROM Track t WHERE t.title LIKE :title")
    fun findOneByTitle(@Param("title") title: String): Optional<Track>
}
