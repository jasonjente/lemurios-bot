package bot.commands.concrete.music;

import bot.commands.Command;
import bot.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Objects;

@Service
public class NowPlaying extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(NowPlaying.class);

    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Now Playing command - ENTER.", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("LEMURIOS BOT - Now Playing:").setColor(Color.ORANGE);
        try {
            List<String> songInQueue = musicPlayerManager.getSongPlaying(Objects.requireNonNull(event.getGuild()));
            if(!songInQueue.isEmpty()) {
                embedBuilder.addField("Now playing:", songInQueue.get(0), true);
                songInQueue.remove(0);
            }
            for (String song : songInQueue) {
                embedBuilder.addField("In Queue:", song, false);
            }
            LOGGER.info("{} has requested the Now Playing command. - LEAVE", event.getUser().getName());
        }catch (NullPointerException e){
            embedBuilder.addField("No songs are playing!", "The queue is empty, use the /play command while in a voice channel to summon the bot and start playing music.", true);
        }

        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }

    @Autowired
    public void setMusicPlayerManager(MusicPlayerManager musicPlayerManager) {
        this.musicPlayerManager = musicPlayerManager;
    }
}
