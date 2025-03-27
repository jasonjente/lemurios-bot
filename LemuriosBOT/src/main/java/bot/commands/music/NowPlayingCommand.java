package bot.commands.music;

import bot.commands.Command;
import bot.application.music.player.MusicPlayerManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static bot.application.constants.Commands.NOW_PLAYING;


@Slf4j
public class NowPlayingCommand extends Command {
    

    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        log.info("{} has requested the Now Playing command - ENTER.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            List<String> songInQueue = musicPlayerManager.getSongPlaying(Objects.requireNonNull(event.getGuild()));
            if (!songInQueue.isEmpty()) {
                embedBuilder.setTitle("LEMURIOS BOT - Now Playing: " + songInQueue.get(0)).setColor(Color.ORANGE);
                String currentlyPlayingValue = musicPlayerManager.getTimeRemaining(event.getGuild());
                embedBuilder.addField("Time: ", currentlyPlayingValue,false);
                songInQueue.remove(0);
            }
            for (String song : songInQueue) {
                embedBuilder.addField("In Queue:", song, false);
            }
            log.info("{} has requested the Now Playing command. - LEAVE", event.getUser().getName());
        }catch (NullPointerException e){
            embedBuilder.addField("No songs are playing!", "The queue is empty, use the /play command while in a voice channel to summon the bot and start playing music.", true).setColor(Color.RED);
        }
        
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String getCommandDescription() {
        return "Prints the songs in the queue.";
    }

    @Override
    public String getCommandName() {
        return NOW_PLAYING.getCommandName();
    }

    @Autowired
    public void setMusicPlayerManager(MusicPlayerManager musicPlayerManager) {
        this.musicPlayerManager = musicPlayerManager;
    }
}
