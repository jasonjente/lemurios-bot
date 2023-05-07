package bot.leveling.service;

import bot.leveling.model.LeaderboardResult;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public interface LevelingService {
    void earnPoints(SlashCommandInteractionEvent event);

    List<LeaderboardResult> getLeaderboardForGuild(SlashCommandInteractionEvent event);
}
