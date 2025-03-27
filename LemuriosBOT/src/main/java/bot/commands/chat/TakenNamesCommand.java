package bot.commands.chat;

import bot.commands.Command;
import bot.application.constants.Lemurioi;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


import java.awt.*;

import static bot.application.constants.Commands.TAKEN_NAMES;
import static bot.application.constants.MessageConstants.GTFO_MESSAGE;


@Slf4j
public class TakenNamesCommand extends Command {
    

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        log.info("{} has requested the available names command.", sender);

        int counter = 0;
        String[] lemurs = Lemurioi.usedNames().split("\n");
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + " Here you can find the taken Lemurious ");
        for (String lemur:lemurs) {
            embedBuilder.addField("Name: " + ++counter , lemur, true);
        }
        embedBuilder.setFooter(GTFO_MESSAGE.getValue()).setColor(Color.YELLOW);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        
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
