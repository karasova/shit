package ru.mustakimov.vkbot.service.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class HumanMessageDTO(
    @JsonProperty("type")
    val type: String,

    @JsonProperty("from_id")
    val fromId: Long,

    @JsonProperty("peer_id")
    val peerId: Long,

    @JsonProperty("text")
    val text: String,
)
