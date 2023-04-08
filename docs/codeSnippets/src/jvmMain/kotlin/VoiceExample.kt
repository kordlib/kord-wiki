import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.connect
import dev.kord.core.behavior.reply
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.voice.AudioFrame
import dev.kord.voice.VoiceConnection
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(KordVoice::class)
suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")
    val kord = Kord(token)

    val lavaplayerManager = DefaultAudioPlayerManager()
    // to use YouTube, we tell LavaPlayer to use remote sources, like YouTube.
    AudioSourceManagers.registerRemoteSources(lavaplayerManager)

    // here we keep track of active voice connections
    val connections: MutableMap<Snowflake, VoiceConnection> = mutableMapOf()

    kord.on<MessageCreateEvent> {
        if (message.content.startsWith("!play")) {
            val channel = member?.getVoiceState()?.getChannelOrNull() ?: return@on

            // lets close the old connection if there is one
            if (connections.contains(guildId)) {
                connections.remove(guildId)!!.shutdown()
            }

            // our lavaplayer audio player which will provide frames of audio
            val player = lavaplayerManager.createPlayer()

            // lavaplayer uses ytsearch: as an identifier to search for YouTube
            val query = "ytsearch: ${message.content.removePrefix("!play")}"

            val track = lavaplayerManager.playTrack(query, player)

            // here we actually connect to the voice channel
            val connection = channel.connect {
                // the audio provider should provide frames of audio
                audioProvider { AudioFrame.fromData(player.provide()?.data) }
            }

            connections[guildId!!] = connection

            message.reply {
                content = "playing track: ${track.info.title}"
            }
        } else if (message.content == "!stop") {
            if (guildId == null) return@on

            connections.remove(guildId)?.shutdown()
        }
    }

    kord.login()
}

// lavaplayer isn't super kotlin-friendly, so we'll make it nicer to work with
suspend fun DefaultAudioPlayerManager.playTrack(query: String, player: AudioPlayer): AudioTrack {
    val track = suspendCoroutine<AudioTrack> {
        this.loadItem(query, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                it.resume(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                it.resume(playlist.tracks.first())
            }

            override fun noMatches() {
                TODO()
            }

            override fun loadFailed(exception: FriendlyException?) {
                TODO()
            }
        })
    }

    player.playTrack(track)

    return track
}
