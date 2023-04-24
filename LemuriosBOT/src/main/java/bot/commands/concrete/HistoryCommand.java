package bot.commands.concrete;

import bot.commands.Command;
import bot.commands.history.HistoryEntry;
import bot.commands.history.HistoryEntryRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

import static bot.constants.Constants.GTFO_MESSAGE;

@Service
public class HistoryCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryCommand.class);
    @Autowired
    private HistoryEntryRepository repository;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getAsTag();
        createHistoryEntry(event);
        LOGGER.info("{} has requested the history of commands.", sender);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Command History:")
                .setDescription("Last 25 commands executed:").setColor(Color.BLACK);
        try {
            java.util.List<HistoryEntry> historyEntryList = (List<HistoryEntry>) repository.findAll();
            int max = 0;
            for(HistoryEntry entry:historyEntryList){
                if(max == 25){
                    break;
                }
                embedBuilder.addField("Command: " + entry.getCommandIssued(), " On " + entry.getCreatedOn() + " by " + entry.getFullTag(),false);
                max++;
            }
        }catch (Exception e){
            LOGGER.error("Error connecting to History Archival ", e);
            embedBuilder.addField("Error connecting to History Archival :((", "If this error persist please contact our adminstrators @oso zw xatzo", true);
        }
        embedBuilder.setFooter(GTFO_MESSAGE.getValue());
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
