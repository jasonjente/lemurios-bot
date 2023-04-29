package bot.commands.concrete.music;

import bot.commands.Command;
import bot.music.MusicPlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class StopCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopCommand.class);
    private MusicPlayerManager musicPlayerManager;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Stop command - ENTER.", event.getUser().getName());
        musicPlayerManager.stop(Objects.requireNonNull(event.getGuild()));
        createHistoryEntry(event);
        LOGGER.info("{} has requested the Stop command - LEAVE.", event.getUser().getName());
    }

    @Autowired
    private void setMusicBot(MusicPlayerManager musicPlayerManager){
        this.musicPlayerManager = musicPlayerManager;
    }
}
