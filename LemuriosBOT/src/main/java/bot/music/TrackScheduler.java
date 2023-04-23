package bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

public class TrackScheduler extends AudioEventAdapter {

    private AudioPlayer audioPlayer;
    public TrackScheduler(AudioPlayer player) {
        this.audioPlayer = player;
    }
}
