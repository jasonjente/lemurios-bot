package bot.commands.concrete.music.radio;

import bot.commands.Command;
import bot.constants.Commands;
import bot.services.leveling.repositories.CustomLinkRepository;
import bot.services.model.CustomLink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.awt.*;

@Service
public class GetCustomRadioLinkCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetCustomRadioLinkCommand.class);

    private CustomLinkRepository customLinkRepository;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("{} has requested to save a new URL, full command: {} - ENTER", event.getUser().getName(), event.getFullCommandName());
        String guildId = event.getGuild().getId();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        List<CustomLink> customLinkList = customLinkRepository.getCustomLinksByDiscordServer(guildId);
        if(!customLinkList.isEmpty()) {
            embedBuilder.setTitle("Lemurios Music BOT - Here is your playlist Link!").setColor(Color.YELLOW);
            for (CustomLink customLink:customLinkList){
                embedBuilder.addField("Genre:", customLink.getGenre() + " , url: "+ customLink.getUrl(), false);
            }
        }else {
            embedBuilder.setTitle("Lemurios Music BOT - Set a Link for your playlist with a genre using the /set-radio command!").setColor(Color.YELLOW);
        }

        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        createHistoryEntry(event);

        LOGGER.info("{} has requested to get the saved URLs, full command total results: {} - LEAVE", event.getUser().getName(), customLinkList.size());
    }

    @Override
    public String getCommandDescription() {
        return "Returns all radio URLS and genres!";
    }

    @Override
    public String getCommandName() {
        return Commands.GET_RADIO.getCommandName();
    }

    @Autowired
    public void setCustomLinkRepository(CustomLinkRepository customLinkRepository) {
        this.customLinkRepository = customLinkRepository;
    }
}
