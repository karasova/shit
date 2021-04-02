package ru.mustakimov.vkbot.service.dto

data class MailingTaskDTO(
    val seed: Long,
    val vkIds: List<Long>,
    val message: Message
)

data class Message(
    val text: String,
    val keyboard: Keyboard? = null
)

data class Keyboard(
    val type: KeyboardType,
    val oneTime: Boolean,
    val items: List<KeyboardButton>
)

enum class KeyboardType {
    INLINE,
    STANDARD
}

sealed class KeyboardButton(
    val type: String
) {
    class TextKeyboardButton(
        val label: String,
        val payload: String,
        val color: KeyboardColor
    ) : KeyboardButton("text")

    class CallbackKeyboardButton(
        val label: String,
        val payload: String,
        val color: KeyboardColor
    ) : KeyboardButton("callback")

    class OpenLinkButton(
        val label: String,
        val payload: String,
        val link: String
    ) : KeyboardButton("open_link")
}

enum class KeyboardColor {
    PRIMARY,
    SECONDARY,
    NEGATIVE,
    POSITIVE
}
