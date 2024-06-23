package bot.commands.concrete.chat;

import bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bot.application.constants.Commands.INVITE_LINK;


public class CreateInviteCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateInviteCommand.class);

    @Override
    public void execute(final SlashCommandInteractionEvent event) {
        var caller = event.getUser().getName();
        LOGGER.info("CreateInviteCommand() - ENTER - User {} requested an invite link!", caller);
        var invite = event.getInteraction().getChannel().asTextChannel().createInvite().complete();
        var embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Lemurios BOT Invite Link.");
        embedBuilder.addField("Invite Link:", invite.getUrl(), false);
        embedBuilder.setColor(java.awt.Color.RED);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        earnPoints(event);
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
