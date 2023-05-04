package bot.music;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private String currentlyPlaying;

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();

        player.setVolume(69);
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
        if((queue.size()==1)){
            currentlyPlaying = track.getInfo().title;
        }


    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public boolean nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        boolean playing = player.startTrack(queue.poll(), false);
        if(playing){
            currentlyPlaying = player.getPlayingTrack().getInfo().title;
        }
        return playing;

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player){
        player.setPaused(true);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        player.setPaused(false);
    }


    public void stop() {
        player.stopTrack();
        queue.clear();
    }

    public List<String> getAllSongsOfList(){
        List<String> ret = new ArrayList<>();

        currentlyPlaying = this.player.getPlayingTrack().getInfo().title;
        ret.add(currentlyPlaying);

        for(AudioTrack audioTrack:queue){
            ret.add(audioTrack.getInfo().title);
        }

        return ret;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public String getCurrentlyPlaying() {
        return currentlyPlaying;
    }
}