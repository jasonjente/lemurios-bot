package bot.commands.chat;

import bot.commands.Command;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;

import static bot.application.constants.Commands.CREDITS_COMMAND;

@Setter
@Slf4j
public class CreditsCommand extends Command {
    

    BuildProperties buildProperties;
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        log.info("creditsCommand - ENTER");
        String version = buildProperties.getVersion();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Lemurios BOT Credits");
        embedBuilder.setDescription("This bot was created by OSO ZW XATZO, Development started in April 2023.");
        embedBuilder.addField("Version", version, false);
        embedBuilder.addField("Github", "https://github.com/jasonjente/lemurios-bot", false);
        embedBuilder.setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT.");
        embedBuilder.setColor(java.awt.Color.RED);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

        log.info("creditsCommand - LEAVE");
    }

    @Override
    public String getCommandDescription() {
        return "View the credits.";
    }

    @Override
    public String getCommandName() {
        return CREDITS_COMMAND.getCommandName();
    }

    @Autowired
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }
}
