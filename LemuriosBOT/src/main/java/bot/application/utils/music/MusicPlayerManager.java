package bot.application.utils.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for mapping Music player instances to discord servers.
 * Each server is mapped by its guild id in String format.
 */

@Service
public class MusicPlayerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicPlayerManager.class);
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
        LOGGER.info("Voice channel {}", voiceChannel.getName());
        embedBuilder.setTitle(":musical_note: Lemurios BOT - Music Player :musical_note:");
        LOGGER.info("loadAndPlay() - ENTER - Attempting to play for Guild Id {}", voiceChannel.getGuild().getId());
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
        LOGGER.info("loadAndPlay() - LEAVE, caller: {}, guildId: {}, voice channel: {}, song: {} ", caller, guildId, voiceChannelName, song);
    }

    public void pause(Guild guild) {
        LOGGER.info("pause() - ENTER - Attempting to pause for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        player.pause(guild);
        LOGGER.info("pause() - LEAVE");
    }

    public void skipTrack(SlashCommandInteractionEvent event, TextChannel textChannel) {
        LOGGER.info("skipTrack() - ENTER - Attempting to skipTrack for Guild Id {}", event.getGuild().getId());
        MusicPlayer player = instances.get(event.getGuild().getId());
        player.skipTrack(textChannel);
        LOGGER.info("skipTrack() - LEAVE");

    }

    public void stop(Guild guild){
        LOGGER.info("stop() - ENTER - Attempting to stop MusicPlayer for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        player.stop(guild);
        //TODO set timer for 5 minute idle. on 5 minute completion disconnect bot
        LOGGER.info("stop() - LEAVE");
    }

    public void connectToVoiceChannel(SlashCommandInteractionEvent event, VoiceChannel voiceChannel) {
        LOGGER.info("stop() - ENTER - Attempting to connect MusicPlayer to voice channel: {} for Guild Id {}",voiceChannel.getName(), event.getChannel().getId());
        MusicPlayer player = instances.get(event.getGuild().getId());
        player.connectToVoiceChannel(event.getGuild().getAudioManager(), voiceChannel);
        LOGGER.info("stop() - LEAVE");
    }

    public void resume(Guild guild) {
        LOGGER.info("resume() - ENTER - Attempting to resume MusicPlayer for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        player.resume(guild);
        LOGGER.info("resume() - LEAVE");
    }

    public List<String> getSongPlaying(Guild guild) {
        LOGGER.info("getSongPlaying() - ENTER - Attempting to get the song playing for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        List<String> ret = player.getSongQueue(guild);
        LOGGER.info("getSongPlaying() - LEAVE, queue size: {}", ret.size());
        return ret;
    }

    public String getTimeRemaining(Guild guild) {
        LOGGER.info("getTimeRemaining() - ENTER for Guild Id {}", guild.getId());
        MusicPlayer player = instances.get(guild.getId());
        String timeRemaining = player.getTimeRemaining(guild);
        LOGGER.info("getTimeRemaining() - {} - LEAVE", timeRemaining);
        return timeRemaining;
    }

    public String disconnectFromVoiceChannel(SlashCommandInteractionEvent event) {
        LOGGER.info("disconnectFromVoiceChannel() - ENTER - Attempting to disconnect MusicPlayer to voice channel for Guild Id {}", event.getChannel().getId());
        MusicPlayer player = instances.get(event.getGuild().getId());
        player.stop(event.getGuild());
        String disconnectedChannelName = player.disconnectFromVoiceChannel(event.getGuild().getAudioManager());
        removeInstance(event.getGuild().getId());
        LOGGER.info("disconnectFromVoiceChannel() - Left from {} - LEAVE", disconnectedChannelName);
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
        LOGGER.info("Voice channel {}", voiceChannel.getName());
        if (instances.containsKey(textChannel.getGuild().getId())){
            stop(textChannel.getGuild());
        }
        loadAndPlay(event, song, embedBuilder);
    }
}
