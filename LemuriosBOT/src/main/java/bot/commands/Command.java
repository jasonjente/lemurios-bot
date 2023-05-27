package bot.commands;

import bot.dataservice.DataService;
import bot.dataservice.model.DiscordServer;
import bot.dataservice.model.HistoryEntry;
import bot.dataservice.leveling.repositories.HistoryEntryRepository;
import bot.dataservice.leveling.service.LevelingService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public abstract class Command {

    @Autowired
    private HistoryEntryRepository historyEntryRepository;
    @Autowired
    private LevelingService levelingService;
    @Autowired
    private DataService dataService;

    public abstract void execute(SlashCommandInteractionEvent event);

    public void createHistoryEntry(SlashCommandInteractionEvent event){
        HistoryEntry entry = new HistoryEntry();
        entry.setCommandIssued(event.getCommandString());
        entry.setFullTag(event.getUser().getAsTag());
        entry.setCreatedOn(Timestamp.from(Instant.now()));
        DiscordServer discordServer = dataService.createDiscordServerObject(event);
        entry.setCommandExecution(dataService.createCommandExecutionObject(event));
        entry.setDiscordServer(discordServer);
        historyEntryRepository.save(entry);
        earnPoints(event);
    }

    public void earnPoints(SlashCommandInteractionEvent event){
        levelingService.earnPoints(event);
    }
    public abstract String getCommandDescription();
    public abstract String getCommandName();
}
