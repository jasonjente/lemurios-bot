package bot.application.music.player;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for mapping Music player instances to discord servers.
 * Each server is mapped by its guild id in String format.
 */

@Service
@Slf4j
public class MusicPlayerManager {
    
    private static final Map<String, MusicPlayer> instances = new HashMap<>();

    /**
     * Adds a new mapping of Music player with key the guild ID.
     * @param guildId
     */
    public void addInstance(final String guildId){
        instances.put(guildId, new MusicPlayer());
    }

    public void removeInstance(final String guildId){
        instances.remove(guildId);
    }
    public void loadAndPlay(SlashCommandInteractionEvent event, String song, EmbedBuilder embedBuilder) {
        var textChannel = event.getChannel().asTextChannel();
        var voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
        log.info("Voice channel {}", voiceChannel.getName());
        embedBuilder.setTitle(":musical_note: Lemurios BOT - Music Player :musical_note:");
        log.info("loadAndPlay() - ENTER - Attempting to play for Guild Id {}", voiceChannel.getGuild().getId());
        if (!instances.containsKey(voiceChannel.getGuild().getId())){
            addInstance(voiceChannel.getGuild().getId());
        }
        MusicPlayer player = instances.get(voiceChannel.getGuild().getId());
        player.loadAndPlay(event, textChannel, voiceChannel, song, embedBuilder);
        //In case we reuse the player in paused state we should unpause to have the music start
        player.resume(textChannel.getGuild());
        String caller = event.getUser().getName();
        String guildId = event.getGuild().getId();
        String voiceChannelName = voiceChannel.getName();
        log.info("loadAndPlay() - LEAVE, caller: {}, guildId: {}, voice channel: {}, song: {} ", caller, guildId, voiceChannelName, song);
    }

    public void pause(Guild guild) {
        log.info("pause() - ENTER - Attempting to pause for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        player.pause(guild);
        log.info("pause() - LEAVE");
    }

    public void skipTrack(SlashCommandInteractionEvent event, TextChannel textChannel) {
        log.info("skipTrack() - ENTER - Attempting to skipTrack for Guild Id {}", event.getGuild().getId());
        MusicPlayer player = instances.get(event.getGuild().getId());
        player.skipTrack(textChannel);
        log.info("skipTrack() - LEAVE");

    }

    public void stop(Guild guild){
        log.info("stop() - ENTER - Attempting to stop MusicPlayer for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        player.stop(guild);
        log.info("stop() - LEAVE");
    }

    public void connectToVoiceChannel(SlashCommandInteractionEvent event, VoiceChannel voiceChannel) {
        log.info("stop() - ENTER - Attempting to connect MusicPlayer to voice channel: {} for Guild Id {}",voiceChannel.getName(), event.getChannel().getId());
        MusicPlayer player = instances.get(event.getGuild().getId());
        player.connectToVoiceChannel(event.getGuild().getAudioManager(), voiceChannel);
        log.info("stop() - LEAVE");
    }

    public void resume(Guild guild) {
        log.info("resume() - ENTER - Attempting to resume MusicPlayer for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        player.resume(guild);
        log.info("resume() - LEAVE");
    }

    public List<String> getSongPlaying(Guild guild) {
        log.info("getSongPlaying() - ENTER - Attempting to get the song playing for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        List<String> ret = player.getSongQueue(guild);
        log.info("getSongPlaying() - LEAVE, queue size: {}", ret.size());
        return ret;
    }

    public String getTimeRemaining(Guild guild) {
        log.info("getTimeRemaining() - ENTER for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        String timeRemaining = player.getTimeRemaining(guild);
        log.info("getTimeRemaining() - {} - LEAVE", timeRemaining);
        return timeRemaining;
    }

    public String disconnectFromVoiceChannel(SlashCommandInteractionEvent event) {
        log.info("disconnectFromVoiceChannel() - ENTER - Attempting to disconnect MusicPlayer to voice channel for Guild Id {}", event.getChannel().getId());
        MusicPlayer player = instances.get(event.getGuild().getId());
        player.stop(event.getGuild());
        String disconnectedChannelName = player.disconnectFromVoiceChannel(event.getGuild().getAudioManager());
        removeInstance(event.getGuild().getId());
        log.info("disconnectFromVoiceChannel() - Left from {} - LEAVE", disconnectedChannelName);
        return disconnectedChannelName;
    }

    public void joinVoiceChannel(final SlashCommandInteractionEvent event, final VoiceChannel voiceChannel) {
        if (!instances.containsKey(voiceChannel.getGuild().getId())){
            addInstance(voiceChannel.getGuild().getId());
        }
        MusicPlayer player = instances.get(voiceChannel.getGuild().getId());
        player.connectToVoiceChannel(event.getGuild().getAudioManager(), voiceChannel);
    }

    public void stopAndLoadAndPlay(final SlashCommandInteractionEvent event, final String song,
                                   final EmbedBuilder embedBuilder) {
        var textChannel = event.getChannel().asTextChannel();
        var voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
        log.info("Voice channel {}", voiceChannel.getName());
        if (instances.containsKey(textChannel.getGuild().getId())){
            stop(textChannel.getGuild());
        }
        loadAndPlay(event, song, embedBuilder);
    }
}
