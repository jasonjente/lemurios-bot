package bot.commands.concrete.chat;

import bot.commands.Command;
import bot.application.services.model.LeaderboardResult;
import bot.application.services.leveling.LevelingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static bot.application.constants.Commands.LEADERBOARD_COMMAND;


public class LeaderboardCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardCommand.class);
    private LevelingService levelingService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("LeaderboardCommand() - Enter");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        List<LeaderboardResult> leaderboardResults = levelingService.getLeaderboardForGuild(event);

        var counter = 0;
        for (LeaderboardResult result : leaderboardResults){
            if (counter == 10){
                break;
            }
            String valueMessage = "Points: " + result.getPoints() + " , Level: " + result.getLevel();
            embedBuilder.addField("User: " + result.getUserTag(), valueMessage,false);
            counter++;
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
