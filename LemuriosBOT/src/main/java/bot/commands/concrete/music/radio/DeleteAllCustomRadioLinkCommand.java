package bot.commands.concrete.music.radio;

import bot.commands.Command;
import bot.constants.Commands;
import bot.services.dataservice.DataService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteAllCustomRadioLinkCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteAllCustomRadioLinkCommand.class);

    private DataService dataService;

    @Override
    @Transactional
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has to delete a genre, full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());
        String guildId = event.getGuild().getId();
        boolean success = true;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            dataService.deleteCustomLinksByDiscordServer(guildId);
        } catch (Exception e){
            success = false;
        }
        String message = success ? "successful.":"not successful.";
        embedBuilder.setTitle("LEMURIOS BOT operation delete radio urls all was " + message);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        createHistoryEntry(event);

        LOGGER.info("{} has to delete a genre, full command: {} - LEAVE - success: {}", event.getUser().getName(), event.getFullCommandName(), success);
    }

    @Override
    public String getCommandDescription() {
        return "Delete all urls for this server";
    }

    @Override
    public String getCommandName() {
        return Commands.DELETE_ALL.getCommandName();
    }

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}
