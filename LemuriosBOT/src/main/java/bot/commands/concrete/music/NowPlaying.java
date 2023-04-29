package bot.commands.concrete.music;

import bot.commands.Command;
import bot.music.MusicPlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NowPlaying extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(NowPlaying.class);

    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Now Playing command - ENTER.", event.getUser().getName());
        List<String> songInQueue = musicPlayerManager.getSongPlaying(event.getGuild());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("LEMURIOS BOT - Now Playing:");
        for(String song:songInQueue){
            embedBuilder.addField("Title:", song, false);
        }

        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        LOGGER.info("{} has requested the Now Playing command. - LEAVE", event.getUser().getName());
    }

    @Autowired
    public void setMusicPlayerManager(MusicPlayerManager musicPlayerManager) {
        this.musicPlayerManager = musicPlayerManager;
    }
}
