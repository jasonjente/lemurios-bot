package bot.commands.chat;

import bot.commands.Command;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


import static bot.application.constants.Commands.INVITE_LINK;


@Slf4j
public class CreateInviteCommand extends Command {
    

    @Override
    public void execute(final SlashCommandInteractionEvent event) {
        var caller = event.getUser().getName();
        log.info("CreateInviteCommand() - ENTER - User {} requested an invite link!", caller);
        var invite = event.getInteraction().getChannel().asTextChannel().createInvite().complete();
        var embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Lemurios BOT Invite Link.");
        embedBuilder.addField("Invite Link:", invite.getUrl(), false);
        embedBuilder.setColor(java.awt.Color.RED);
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
        
        log.info("CreateInviteCommand() - LEAVE");
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
