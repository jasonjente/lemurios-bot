package bot.services.leveling;

import bot.services.model.LeaderboardResult;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public interface LevelingService {
    void earnPoints(SlashCommandInteractionEvent event);

    List<LeaderboardResult> getLeaderboardForGuild(SlashCommandInteractionEvent event);
}
