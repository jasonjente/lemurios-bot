package bot.services.dataservice;

import bot.services.model.CommandExecution;
import bot.services.model.CustomLink;
import bot.services.model.DiscordServer;
import bot.services.model.ServerUser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public interface DataService {
    ServerUser createServerUserObject(String tag, DiscordServer discordServer, CommandExecution commandExecution, Integer pointsEarned);

    int calculateLevel(ServerUser ret);

    CommandExecution createCommandExecutionObject(SlashCommandInteractionEvent event);

    DiscordServer createDiscordServerObject(SlashCommandInteractionEvent event);

    void deleteCustomLinksByDiscordServer(String guildId);

    void deleteCustomLinkByDiscordServerAndGenre(String guildId, String genre);

    void saveCustomLink(CustomLink customLink);

    CustomLink findCustomLinkByDiscordServerAndGenre(String id, String genre);

    List<CustomLink> findCustomLinksByDiscordServerAndGenre(String guildId);

}
