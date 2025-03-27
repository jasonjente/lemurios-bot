package bot.application.music.player;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {

    @Getter
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private String currentlyPlaying;
    private static final Set<String> trackRetransmissionSet = new HashSet<>();

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(final AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();

        player.setVolume(69);
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     * Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing
     * If something is playing, it returns false and does nothing. In that case the player was already playing
     * so this track goes to the queue instead.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(final AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
        if ((queue.size() == 1)) {
            currentlyPlaying = track.getInfo().title;
        }


    }

    /**
     * Start the next track, stopping the current one if it is playing.
     * Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
     * giving null to startTrack, which is a valid argument and will simply stop the player.
     */
    public boolean nextTrack() {
        boolean playing = player.startTrack(queue.poll(), false);
        if (playing) {
            currentlyPlaying = player.getPlayingTrack().getInfo().title;
        }
        return playing;

    }

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onPlayerPause(final AudioPlayer player) {
        player.setPaused(true);
    }

    @Override
    public void onPlayerResume(final AudioPlayer player) {
        player.setPaused(false);
    }

    @Override
    public void onTrackException(final AudioPlayer player, final AudioTrack track, final FriendlyException exception) {
        // There was an error playing the track, so we retry once otherwise we suppress.
        if (!trackRetransmissionSet.contains(track.getInfo().identifier)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //do nothing
            }
            trackRetransmissionSet.add(track.getInfo().identifier);
            //must not be unique track so clone will work instead of passing the same object
            queue(track.makeClone());
        } else {
            trackRetransmissionSet.remove(track.getInfo().identifier);
        }
    }

    public void stop() {
        player.stopTrack();
        queue.clear();
    }

    public List<String> getAllSongsOfList() {
        var ret = new ArrayList<String>();

        currentlyPlaying = this.player.getPlayingTrack().getInfo().title;
        ret.add(currentlyPlaying);

        for (AudioTrack audioTrack : queue) {
            ret.add(audioTrack.getInfo().title);
        }

        return ret;
    }

}