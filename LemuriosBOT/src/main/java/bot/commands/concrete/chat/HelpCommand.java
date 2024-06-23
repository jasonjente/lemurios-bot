package bot.commands.concrete.chat;


import bot.LemuriosBOTListenerAdapter;
import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static bot.application.constants.Commands.*;
import static bot.application.constants.MessageConstants.*;


public class HelpCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("helpCommand - ENTER - {} called for help", event.getUser().getName());
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LEMURIOS BOT Help Center.")
                .setDescription(HELLO.getValue()+ event.getUser().getName() + HELP_COMMENT.getValue());

        Map<String,Command> commandMap = LemuriosBOTListenerAdapter.getCommands();

        for (Command command:commandMap.values()){
            embedBuilder.addField(command.getCommandName(),command.getCommandDescription(), false);
        }

        embedBuilder.setColor(java.awt.Color.PINK).setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT.");
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        earnPoints(event);
        LOGGER.info("helpCommand - LEAVE");
    }

    @Override
    public String getCommandDescription() {
        return "Shows commands Help.";
    }

    @Override
    public String getCommandName() {
        return HELP_COMMAND.getCommandName();
    }
}
