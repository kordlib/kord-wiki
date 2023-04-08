package dev.kord.samples

import dev.kord.common.entity.PresenceStatus
import dev.kord.gateway.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val gateway = DefaultGateway()

    gateway.events.filterIsInstance<MessageCreate>().onEach {
        val words = it.message.content.split(' ')
        when (words.firstOrNull()) {
            "!close" -> gateway.stop()
            "!detach" -> gateway.detach()
            "!status" -> when (words.getOrNull(1)) {
                "playing" -> gateway.editPresence {
                    status = PresenceStatus.Online
                    afk = false
                    playing("Kord")
                }
            }
        }
    }.launchIn(gateway)

    gateway.start(token) {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
