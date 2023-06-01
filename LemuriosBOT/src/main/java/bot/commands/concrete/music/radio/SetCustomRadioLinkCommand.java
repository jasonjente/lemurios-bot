package bot.commands.concrete.music.radio;

import bot.commands.Command;
import bot.constants.Commands;
import bot.services.dataservice.DataService;
import bot.services.model.CustomLink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class SetCustomRadioLinkCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetCustomRadioLinkCommand.class);
    private DataService dataService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested to save a new URL, full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());
        String guildId = event.getGuild().getId();
        String url = event.getInteraction().getOptions().get(0).getAsString();
        String genre = event.getInteraction().getOptions().get(1).getAsString();
        CustomLink customLink = dataService.findCustomLinkByDiscordServerAndGenre(event.getGuild().getId(), genre);
        if(customLink != null){
            customLink.setUrl(url);
        } else {
            customLink = new CustomLink(url, guildId, genre);
        }
        dataService.saveCustomLink(customLink);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Lemurios Music BOT - Saved Link!").setColor(Color.YELLOW);

        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

        createHistoryEntry(event);
        earnPoints(event);
        LOGGER.info("{} has requested to save a new URL, full command: {} - LEAVE", event.getUser().getName(), url);
    }

    @Override
    public String getCommandDescription() {
        return "Set a custom radio with a url provided.";
    }

    @Override
    public String getCommandName() {
        return Commands.SET_RADIO.getCommandName();
    }

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}
