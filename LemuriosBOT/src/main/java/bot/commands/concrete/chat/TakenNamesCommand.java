package bot.commands.concrete.chat;

import bot.commands.Command;
import bot.application.constants.Lemurioi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import static bot.application.constants.Commands.TAKEN_NAMES;
import static bot.application.constants.MessageConstants.GTFO_MESSAGE;


public class TakenNamesCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(TakenNamesCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        LOGGER.info("{} has requested the available names command.", sender);

        int counter = 0;
        String[] lemurs = Lemurioi.usedNames().split("\n");
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + " Here you can find the taken Lemurious ");
        for (String lemur:lemurs) {
            embedBuilder.addField("Name: " + ++counter , lemur, true);
        }
        embedBuilder.setFooter(GTFO_MESSAGE.getValue()).setColor(Color.YELLOW);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        earnPoints(event);
    }

    @Override
    public String getCommandDescription() {
        return "View Lemurios XXX taken names.";
    }

    @Override
    public String getCommandName() {
        return TAKEN_NAMES.getCommandName();
    }
}
