package bot.dataservice;

import bot.dataservice.model.CommandExecution;
import bot.dataservice.model.CustomLink;
import bot.dataservice.model.DiscordServer;
import bot.dataservice.model.ServerUser;
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
