package bot.commands;

import bot.services.dataservice.DataService;
import bot.services.leveling.Jackpot;
import bot.services.model.DiscordServer;
import bot.services.model.HistoryEntry;
import bot.services.leveling.repositories.HistoryEntryRepository;
import bot.services.leveling.LevelingService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

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
        DiscordServer discordServer = dataService.findOrCreateDiscordServerObject(event);
        entry.setCommandExecution(dataService.createCommandExecutionObject(event));
        entry.setDiscordServer(discordServer);
        historyEntryRepository.save(entry);
    }

    public void earnPoints(SlashCommandInteractionEvent event){
        levelingService.earnPoints(event);
    }
    public void earnPoints(SlashCommandInteractionEvent event, Integer points){
        levelingService.earnPoints(event, points);
    }

    public Jackpot jackpot(SlashCommandInteractionEvent event){
        Jackpot jackpot;
        if (isJackpot(generateRandomNumber(1, 10000))) {
            int randomNumber = generateRandomNumber(25, 250);
            earnPoints(event, randomNumber);
             jackpot = new Jackpot((int) (randomNumber * 1.75), true);
        } else {
            jackpot = new Jackpot(0, false);
        }
        return jackpot;
    }
    private static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static boolean isJackpot(int luckFactor) {
        int randomValue = generateRandomNumber(1, 100_000);
        return randomValue <= luckFactor;
    }
    public abstract String getCommandDescription();
    public abstract String getCommandName();
}
