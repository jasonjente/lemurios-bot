package bot.commands.concrete.chat;

import bot.commands.Command;
import bot.constants.Lemurioi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;

import static bot.constants.Constants.GTFO_MESSAGE;

@Service
public class TakenNamesCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(TakenNamesCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getAsTag();
        LOGGER.info("{} has requested the available names command.", sender);
        createHistoryEntry(event);

        int counter = 0;
        String[] lemurs = Lemurioi.usedNames().split("\n");
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + " Here you can find the taken Lemurious ");
        for(String lemur:lemurs) {
            embedBuilder.addField("Name: " + ++counter , lemur, true);
        }
        embedBuilder.setFooter(GTFO_MESSAGE.getValue()).setColor(Color.YELLOW);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
    }
}
