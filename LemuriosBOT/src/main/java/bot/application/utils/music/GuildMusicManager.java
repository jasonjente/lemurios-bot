package bot.application.utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {
    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    private final TrackScheduler scheduler;

    /**
     * Creates a player and a track scheduler.
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildMusicManager(final AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }
}