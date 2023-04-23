package bot.commands.concrete;

import bot.commands.Command;
import bot.constants.Lemurioi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

import static bot.constants.Constants.*;

@Service
public class AssemblemursCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblemursCommand.class);


    @Override
    public void execute(MessageReceivedEvent event) {
        LOGGER.info("assemblemursCommand - ENTER");
        createHistoryEntry(event);
        if(userIsLemurWorthy(event)){
            doAssemble(event);
        } else {
            notifyUnworthy(event);
        }
        LOGGER.info("assemblemursCommand - LEAVE");
    }

    private boolean userIsLemurWorthy(MessageReceivedEvent event) {
        String sender = event.getAuthor().getAsTag();
        Role lemurs = event.getGuild().getRolesByName("LEMURIOI", true).get(0);
        if (!event.getMember().getRoles().contains(lemurs)) {
            LOGGER.info("{} is not worthy", sender);
            return false;
        }
        LOGGER.info("{} is worthy", sender);
        return true;
    }

    private void doAssemble(MessageReceivedEvent event) {
        Role lemurs = event.getGuild().getRolesByName("LEMURIOI", true).get(0);
        User author = event.getAuthor();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LEMURS ASSEMBLE")
                .setDescription(event.getAuthor().getAsTag() + "wants to play games. Please join them.")
                .setColor(Color.YELLOW)
                .setFooter("NOW GTFO HERE!");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        String textMessage = lemurs.getAsMention() + ASSEMBLEMURS_MESSAGE.getValue()
                + author.getName() + "#" + author.getDiscriminator() + " wants you to join him. ";
        event.getChannel().sendMessage(textMessage).queue();

        List<Member> membersWithLemursRole = event.getGuild().getMembersWithRoles(lemurs);
        for (Member member : membersWithLemursRole) {
            if (member.getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
                continue;
            }
            PrivateChannel channel = member.getUser().openPrivateChannel().complete();
            String message = HELLO.getValue() + member.getUser().getName() + " Lemurios-" + event.getAuthor().getAsTag()
                    + INVITE_MESSAGE.getValue()+ SORRY_FOR_SPAM_MESSAGE.getValue();
            channel.sendMessage(message).queue();
            LOGGER.info("Sent direct message to {}." , member.getUser().getAsTag());
        }
    }

    private void notifyUnworthy(MessageReceivedEvent event) {
        String sender = event.getAuthor().getAsTag();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + " sorry, but you are not worthy enough to call the Lemurs. :/")
                .setDescription("Sorry " + sender + ", it seems you dont belong to the lemurs group.")
                .addField("Q: What do I now?", "A: To become a member of lemurs you have to change your name in League of Legends to a name with this format (also legally):", false)
                .addField("1: How should my name look like?", "Lemurios XXX, where XXX are three **(3)** integers like 123, 012, 006", true)
                .addField("2: Is there any name I cannot choose?", "Lemurios 069 and Lemurios 420 are forbidden from being chosen", true)
                .addField("3: What are the available Lemurios XXX names?", "Here is a list with all claimed lemurs: \n" + Lemurioi.usedNames(), true)
                .addField("Finally ", "After these steps have been fulfilled, please contact Lemurios 002 or Lemurios 007 to proceed with your application.", true)
                .setColor(java.awt.Color.RED)
                .setFooter(GTFO_MESSAGE.getValue());

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

    }
}
