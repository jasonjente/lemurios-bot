package bot.commands.concrete;

import bot.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlayCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Play command.", event.getUser().getName());

        createHistoryEntry(event);
        functionalityNotReadyYet(event);
    }
}
