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
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for handling the music player.
 * audioPlayerManager is used for creating audio players and loading tracks and playlists.
 *
 * Important: Music player handles music replies, in order to provide the thumbnail to the reply as well as any other relevant information.
 */
public class MusicPlayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicPlayer.class);
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
     *
     * @param event
     * @param channel      t
     * @param voiceChannel
     * @param trackUrl
     * @param embedBuilder
     */
    public void loadAndPlay(SlashCommandInteractionEvent event, final TextChannel channel, VoiceChannel voiceChannel, final String trackUrl, EmbedBuilder embedBuilder) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                createReturnMessageAndPlay(event, track, channel, voiceChannel, musicManager, embedBuilder);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> audioTrackList = playlist.getTracks();
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }
                createReturnMessageAndPlayPlaylist(event, firstTrack, channel, voiceChannel, musicManager, embedBuilder, audioTrackList);
                audioTrackList.remove(firstTrack);
                for(AudioTrack track: audioTrackList){
                    musicManager.getScheduler().queue(track);
                }
            }

            @Override
            public void noMatches() {
                event.getInteraction().getHook().sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getInteraction().getHook().sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void createReturnMessageAndPlay(SlashCommandInteractionEvent event, AudioTrack track, TextChannel channel, VoiceChannel voiceChannel, GuildMusicManager musicManager, EmbedBuilder embedBuilder) {
        embedBuilder.addField("Added to queue: ", track.getInfo().title, false);
        embedBuilder.setColor(Color.ORANGE);
        InputStream inputStream = getThumbnailIfPossible(track.getInfo().identifier);
        embedBuilder.addField(new MessageEmbed.Field("duration: ", formatTime(track.getDuration()), true));
        if(inputStream != null) {
            embedBuilder.setImage("attachment://thumbnail.png");
            event.getInteraction().getHook().editOriginalEmbeds().setFiles(FileUpload.fromData(inputStream, "thumbnail.png")).setEmbeds(embedBuilder.build()).queue();
        } else {
            event.getInteraction().getHook().editOriginalEmbeds().setEmbeds(embedBuilder.build()).queue();
        }

        play(channel.getGuild(), voiceChannel, musicManager, track);
    }
    private void createReturnMessageAndPlayPlaylist(SlashCommandInteractionEvent event, AudioTrack track, TextChannel channel, VoiceChannel voiceChannel, GuildMusicManager musicManager, EmbedBuilder embedBuilder, List<AudioTrack> audioTrackList) {
        embedBuilder.setColor(Color.ORANGE);
        //TODO Optimize this as it thinks this is for Youtube
        InputStream inputStream = getThumbnailIfPossible(track.getInfo().identifier);
        embedBuilder.setImage("attachment://thumbnail.png");
        embedBuilder.addField("Adding the following tracks to the queue: ", "", false);
        int counter = 1;
        for(AudioTrack audioTrack:audioTrackList) {
            embedBuilder.addField(new MessageEmbed.Field(counter + ". Added to the queue!", audioTrack.getInfo().title + "(" + formatTime(track.getDuration()) +")", true));
            counter++;
        }

        if(inputStream != null) {
            event.getInteraction().getHook().editOriginalEmbeds().setFiles(FileUpload.fromData(inputStream, "thumbnail.png")).setEmbeds(embedBuilder.build()).queue();
        } else {
            event.getInteraction().getHook().editOriginalEmbeds().setEmbeds(embedBuilder.build()).queue();
        }

        play(channel.getGuild(), voiceChannel, musicManager, track);
    }

    private void play(Guild guild, VoiceChannel voiceChannel, GuildMusicManager musicManager, AudioTrack track) {
        if(!guild.getAudioManager().isConnected()) {
            connectToVoiceChannel(guild.getAudioManager(), voiceChannel);
        }
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
    }

    public void connectToVoiceChannel(AudioManager audioManager, VoiceChannel voiceChannel) {
        if (!audioManager.isConnected() && audioManager.isAutoReconnect()) {
            audioManager.openAudioConnection(voiceChannel);
        } else {
            audioManager.closeAudioConnection();
            try {
                //This flow applies to the case that the bot is already connected to another voice channel.
                //Wait for some seconds to register the leaving of one voice channel before entering another one.
                Thread.sleep(100);
                audioManager.openAudioConnection(voiceChannel);
            } catch (InterruptedException e) {

            }

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

    public String getTimeRemaining(Guild guild) {
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        StringBuilder ret = new StringBuilder()
                .append(formatTime(musicManager.getScheduler().getPlayer().getPlayingTrack().getPosition()))
                .append(" : ")
                .append(formatTime(musicManager.getScheduler().getPlayer().getPlayingTrack().getDuration()));
        return ret.toString();
    }

    public static String formatTime(long timeInMs) {
        long seconds = timeInMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        // Calculate remaining minutes and seconds after calculating hours
        minutes %= 60;
        seconds %= 60;

        // Format the time as a string with leading zeros where needed
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String disconnectFromVoiceChannel(AudioManager audioManager) {
        String voiceChanel = audioManager.getConnectedChannel().getName();
        audioManager.closeAudioConnection();
        return voiceChanel;
    }
    private InputStream getThumbnailIfPossible(String id) {
        try {
            String thumbnailUrl = "https://i.ytimg.com/vi/" +
                    id +
                    "/default.jpg";
            HttpURLConnection connection = getHttpURLConnection(thumbnailUrl);
            return connection.getInputStream();
        } catch (IOException e) {
            //error downloading suppress
            LOGGER.error("error downloading thumbnail: {}", id, e);
        }
        return null;
    }
    @NotNull
    private HttpURLConnection getHttpURLConnection(String attachmentUrl) throws IOException {
        URL url = new URL(attachmentUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the headers to match the ones sent by a browser to trick the CDN and avoid error 403
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Referer", "https://discord.com/channels/");
        connection.setRequestProperty("Cookie", "cookies_here");
        return connection;
    }
}
