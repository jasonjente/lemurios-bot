package bot.music;

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
 * Class for mapping Music player instances to servers.
 */

@Service
public class MusicPlayerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicPlayerManager.class);
    private static final Map<String, MusicPlayer> instances = new HashMap<>();

    public void addInstance(String guildId){
        instances.put(guildId, new MusicPlayer());
    }

    public void removeInstance(String guildId){
        instances.remove(guildId);
    }
    public void loadAndPlay(TextChannel textChannel, VoiceChannel voiceChannel, String song) {
        LOGGER.info("loadAndPlay() - ENTER - Attempting to pause for Guild Id {}", voiceChannel.getGuild().getId());
        if(!instances.containsKey(voiceChannel.getGuild().getId())){
            addInstance(voiceChannel.getGuild().getId());
        }
        MusicPlayer player = instances.get(voiceChannel.getGuild().getId());
        player.loadAndPlay(textChannel, voiceChannel, song);
        LOGGER.info("loadAndPlay() - LEAVE");
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
        removeInstance(guild.getId());
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
        LOGGER.info("getSongPlaying() - LEAVE");
        return ret;
    }

    public String getTimeRemaining(Guild guild) {
        LOGGER.info("getTimeRemaining() - ENTER");
        MusicPlayer player = instances.get(guild.getId());
        String timeRemaining = player.getTimeRemaining(guild);
        LOGGER.info("getTimeRemaining() - LEAVE");
        return timeRemaining;
    }

    public void disconnectFromVoiceChannel(SlashCommandInteractionEvent event, VoiceChannel voiceChannel) {
        LOGGER.info("disconnectFromVoiceChannel() - ENTER - Attempting to connect MusicPlayer to voice channel: {} for Guild Id {}",voiceChannel.getName(), event.getChannel().getId());
        MusicPlayer player = instances.get(event.getGuild().getId());
        player.disconnectFromVoiceChannel(event.getGuild().getAudioManager());
        LOGGER.info("disconnectFromVoiceChannel() - LEAVE");
    }

    public void joinVoiceChannel(SlashCommandInteractionEvent event, VoiceChannel voiceChannel) {
        if(!instances.containsKey(voiceChannel.getGuild().getId())){
            addInstance(voiceChannel.getGuild().getId());
        }
        MusicPlayer player = instances.get(voiceChannel.getGuild().getId());
        player.connectToVoiceChannel(event.getGuild().getAudioManager(), voiceChannel);
    }
}
