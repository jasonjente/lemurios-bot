package bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling the music player.
 * audioPlayerManager is used for creating audio players and loading tracks and playlists.
 */
public class MusicPlayer {
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public MusicPlayer() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    /**
     * Loads the song on the playlist and starts playing if the queue is empty
     * @param channel t
     * @param voiceChannel
     * @param trackUrl
     */
    public void loadAndPlay(final TextChannel channel, VoiceChannel voiceChannel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                createReturnMessageAndPlay(track, channel, voiceChannel, musicManager);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }
                createReturnMessageAndPlay(firstTrack, channel, voiceChannel, musicManager);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void createReturnMessageAndPlay(AudioTrack track, TextChannel channel, VoiceChannel voiceChannel, GuildMusicManager musicManager) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Lemurios Music BOT");
        embedBuilder.addField("Added to queue: ", track.getInfo().title, false);
        embedBuilder.setColor(Color.ORANGE);

        channel.sendMessageEmbeds(embedBuilder.build()).queue();

        play(channel.getGuild(), voiceChannel, musicManager, track);
    }

    private void play(Guild guild, VoiceChannel voiceChannel, GuildMusicManager musicManager, AudioTrack track) {
        connectToVoiceChannel(guild.getAudioManager(), voiceChannel);

        musicManager.getScheduler().queue(track);
    }

    public void pause(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        musicManager.getScheduler().onPlayerPause(musicManager.player);
    }

    public void resume(Guild guild){
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        musicManager.getScheduler().onPlayerResume(musicManager.player);
    }

    public void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.getScheduler().nextTrack();

        channel.sendMessage("Skipped to next track.").queue();
    }

    public void connectToVoiceChannel(AudioManager audioManager, VoiceChannel voiceChannel) {
        if (!audioManager.isConnected() && audioManager.isAutoReconnect()) {
            audioManager.openAudioConnection(voiceChannel);
        }
    }

    public void stop(Guild guild) {
        if(guild.getAudioManager().isConnected()){
            GuildMusicManager musicManager = getGuildAudioPlayer(guild);
            musicManager.getScheduler().stop();
        }
    }

    public List<String> getSongQueue(Guild guild) {
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        return musicManager.getScheduler().getAllSongsOfList();
    }
}
