package bot.commands.concrete.chat;

import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static bot.constants.Commands.INVITE_LINK;

@Service
public class CreateInviteCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateInviteCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String caller = event.getUser().getAsTag();
        LOGGER.info("CreateInviteCommand() - ENTER - User {} requested an invite link!", caller);
        Invite invite = event.getInteraction().getChannel().asTextChannel().createInvite().complete();
        createHistoryEntry(event);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Lemurios BOT Invite Link.");
        embedBuilder.addField("Invite Link:", invite.getUrl(), false);
        embedBuilder.setColor(java.awt.Color.RED);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        LOGGER.info("CreateInviteCommand() - LEAVE");
    }

    @Override
    public String getCommandDescription() {
        return "Creates an invite for this server.";
    }

    @Override
    public String getCommandName() {
        return INVITE_LINK.getCommandName();
    }
}
