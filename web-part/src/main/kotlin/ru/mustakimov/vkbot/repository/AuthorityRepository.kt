package ru.mustakimov.vkbot.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.mustakimov.vkbot.domain.Authority

/**
 * Spring Data JPA repository for the [Authority] entity.
 */

interface AuthorityRepository : JpaRepository<Authority, String>
