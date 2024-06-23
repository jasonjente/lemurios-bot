package bot.commands.concrete.music;

import bot.commands.Command;
import bot.application.utils.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static bot.application.constants.Commands.NOW_PLAYING;


public class NowPlayingCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(NowPlayingCommand.class);

    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Now Playing command - ENTER.", event.getUser().getName());
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
            LOGGER.info("{} has requested the Now Playing command. - LEAVE", event.getUser().getName());
        }catch (NullPointerException e){
            embedBuilder.addField("No songs are playing!", "The queue is empty, use the /play command while in a voice channel to summon the bot and start playing music.", true).setColor(Color.RED);
        }
        earnPoints(event);
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
