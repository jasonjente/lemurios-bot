package bot.commands.concrete;

import bot.commands.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PauseCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(PauseCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested the Pause command.", event.getUser().getName());

        createHistoryEntry(event);
        functionalityNotReadyYet(event);
    }
}
