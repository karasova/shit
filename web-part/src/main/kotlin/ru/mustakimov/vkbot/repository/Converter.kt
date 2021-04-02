package ru.mustakimov.vkbot.repository

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import ru.mustakimov.vkbot.domain.Participant
import ru.mustakimov.vkbot.domain.Team
import ru.mustakimov.vkbot.domain.enumeration.TeamStatus
import java.io.InputStream

class Converter {

    fun convert(csv: InputStream): List<Team> {
        val rows: List<Map<String, String>> = csvReader().readAllWithHeader(csv)

        val filteredRows = rows.map { row -> row.map { (k, v) -> k to v.trim() }.toMap() }

        val teams = mutableListOf<Team>()

        for (row in filteredRows) {
            val ages = row[AGE]?.split(",")?.map { it.trim() }
            val employers = row[EMPLOYER]?.split(",")

            val participant1 = Participant().apply {
                age = ages?.get(0)?.toInt()
                employer = employers?.get(0)?.trim()
                fullName = row[REGISTRATOR]
                phoneNumber = row[PHONE_NUMBER]
                vkId = row[VK_ID]?.toLong()
            }

            val participants = mutableListOf(participant1)

            for (i in 1..3) {
                val rowName = when (i) {
                    1 -> PARTICIPANT2
                    2 -> PARTICIPANT3
                    3 -> PARTICIPANT4
                    else -> TODO()
                }

                val participantName = row[rowName]

                if (participantName != null && participantName.length > 2) {
                    participants.add(
                        Participant().apply {
                            fullName = participantName
                            age = ages?.getOrElse(i) { ages.last() }?.toInt()
                            employer = employers?.getOrElse(i) { employers.last() }?.trim()
                        }
                    )
                }
            }

            val teamStatus = when (row[STATUS]) {
                "Поступил" -> TeamStatus.ADDED
                "Отменен" -> TeamStatus.CANCELED
                "Замена участников" -> TeamStatus.PARTICIPANTS_NEEDED
                else -> TeamStatus.ADDED
            }

            val team = Team().apply {
                id = row[ID]?.toLong()
                title = row[TITLE]
                comment = row[COMMENT]
                status = teamStatus
                registrator = participant1
            }

            participants.forEach { team.addParticipant(it) }

            teams.add(team)
        }

        return teams
    }

    companion object {
        const val ID = "Номер заявки"
        const val TITLE = "Название команды"
        const val STATUS = "Статус"
        const val COMMENT = "Комментарий менеджера"
        const val REGISTRATOR = "ФИО регистратора"
        const val PARTICIPANT2 = "ФИО второго участника"
        const val PARTICIPANT3 = "ФИО третьего участника"
        const val PARTICIPANT4 = "ФИО четвертого участника"
        const val VK_ID = "Id пользователя"
        const val AGE = "Возраст всех участников (через запятую)"
        const val EMPLOYER = "Место(а) учебы/работы"
        const val PHONE_NUMBER = "Номер телефона регистратора"
        const val CITY = "Город(а) всех участников команды"
    }
}
