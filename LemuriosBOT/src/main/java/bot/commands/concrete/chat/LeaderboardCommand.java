package bot.commands.concrete.chat;

import bot.commands.Command;
import bot.services.model.LeaderboardResult;
import bot.services.leveling.LevelingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static bot.constants.Commands.LEADERBOARD_COMMAND;

@Service
public class LeaderboardCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardCommand.class);
    private LevelingService levelingService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("LeaderboardCommand() - Enter");
        createHistoryEntry(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        List<LeaderboardResult> leaderboardResults = levelingService.getLeaderboardForGuild(event);

        for (LeaderboardResult result : leaderboardResults){
            String valueMessage = "Points: " + result.getPoints() + " , Level: " + result.getLevel();
            embedBuilder.addField("User: " + result.getUserTag(), valueMessage,false);
        }

        embedBuilder.setColor(java.awt.Color.PINK).setFooter("NOW GTFO HERE!\n With Best Regards Lemurios BOT.");
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        earnPoints(event);
        LOGGER.info("LeaderboardCommand() - Leave");
    }

    @Override
    public String getCommandDescription() {
        return "Prints the Level Leaderboard for this server!";
    }

    @Override
    public String getCommandName() {
        return LEADERBOARD_COMMAND.getCommandName();
    }

    @Autowired
    public void setLevelingService(LevelingService levelingService) {
        this.levelingService = levelingService;
    }
}
