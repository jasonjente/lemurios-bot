package bot.commands.concrete.music.radio;

import bot.commands.Command;
import bot.application.constants.Commands;
import bot.application.services.data.DataService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteGenreCustomRadioLinkCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteGenreCustomRadioLinkCommand.class);

    private DataService dataService;

    @Override
    @Transactional
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has to delete a genre, full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());
        String guildId = event.getGuild().getId();
        String genre = event.getOptions().get(0).getAsString();
        boolean success = true;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            dataService.deleteCustomLinkByDiscordServerAndGenre(guildId, genre);
        } catch (Exception e){
            LOGGER.error("ERROR: ",e);
            success = false;
        }
        String message = success ? "successful.":"not successful.";
        embedBuilder.setTitle("LEMURIOS BOT operation delete genre " + genre + " was " + message);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        earnPoints(event);
        LOGGER.info("{} has to delete a genre, full command: {} - LEAVE", event.getUser().getName(), event.getFullCommandName());
    }

    @Override
    public String getCommandDescription() {
        return "Delete the URL associated with a genre.";
    }

    @Override
    public String getCommandName() {
        return Commands.DELETE_GENRE.getCommandName();
    }

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}
