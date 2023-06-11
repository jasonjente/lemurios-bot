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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
    private static final double VALUE_MULTIPLIER = 1.75;

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

    protected Jackpot jackpot(SlashCommandInteractionEvent event){
        Jackpot jackpot;
        if (isJackpot(2)) {
            int pointsToEarn = generateRandomNumber(25, 250);
            earnPoints(event, pointsToEarn);
            jackpot = new Jackpot((int) (pointsToEarn * VALUE_MULTIPLIER), true);
        } else {
            jackpot = new Jackpot(0, false);
        }
        return jackpot;
    }
    private int generateRandomNumber(int min, int max) {
        Random random;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            random = new Random(2312312438644389749l);
        }
        return random.nextInt(max - min + 1);
    }

    private boolean isJackpot(double luckFactor) {
        int randomValue = generateRandomNumber(1, 100_000);
        return randomValue <= luckFactor;
    }
    public abstract String getCommandDescription();
    public abstract String getCommandName();
}
