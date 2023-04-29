package bot.commands.concrete;

import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class CreditsCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditsCommand.class);

    BuildProperties buildProperties;
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("creditsCommand - ENTER");
        String version = buildProperties.getVersion();
        createHistoryEntry(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Lemurios BOT Credits");
        embedBuilder.setDescription("This bot was created by OSO ZW XATZO, Development started in April 2023.");
        embedBuilder.addField("Version", version, false);
        embedBuilder.addField("Github", "https://github.com/jasonjente?tab=repositories", false);
        embedBuilder.setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT.");
        embedBuilder.setColor(java.awt.Color.RED);
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        LOGGER.info("creditsCommand - LEAVE");
    }

    @Autowired
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }
}
