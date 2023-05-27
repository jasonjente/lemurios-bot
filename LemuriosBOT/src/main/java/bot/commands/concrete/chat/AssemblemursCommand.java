package bot.commands.concrete.chat;

import bot.commands.Command;
import bot.constants.Lemurioi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bot.constants.Commands.ASSEMLEMURS_COMMAND;
import static bot.constants.Constants.*;

@Service
public class AssemblemursCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblemursCommand.class);
    private static final String ROLE_NAME = "LEMURIOI";
    private static final boolean ENABLE_SEND_PRIVATE_MESSAGES = true;
    private static Map<String, LocalDateTime> timeoutMap = new HashMap<>();
    private static final long MAX_TIME_OUT_FOR_ASSEMBLEMURS = 1;
    private static final boolean ENABLE_TIMEOUTS = true;


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("assemblemursCommand - ENTER");
        if(userIsLemurWorthy(event)){
            doAssemble(event);
            createHistoryEntry(event);
        } else {
            notifyUnworthy(event);
        }
        LOGGER.info("assemblemursCommand - LEAVE");
    }

    @Override
    public String getCommandDescription() {
        return "pings all lemurs that belong in the LEMURIOI role. \nTo call the other lemurs you will have to be a member of lemurs :).";
    }

    @Override
    public String getCommandName() {
        return ASSEMLEMURS_COMMAND.getCommandName();
    }

    private boolean userIsLemurWorthy(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getAsTag();
        Role lemurs = event.getGuild().getRolesByName(ROLE_NAME, true).get(0);
        if (!event.getMember().getRoles().contains(lemurs)) {
            LOGGER.info("{} is not worthy", sender);
            return false;
        }
        LOGGER.info("{} is worthy", sender);
        return true;
    }

    private void doAssemble(SlashCommandInteractionEvent event) {
        Role lemurs = event.getGuild().getRolesByName(ROLE_NAME, true).get(0);
        User author = event.getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("LEMURS ASSEMBLE")
                .setColor(Color.YELLOW);

        if(ENABLE_TIMEOUTS) {
            LocalDateTime lastTimeUsed;

            if (timeoutMap.containsKey(author.getId())) {
                lastTimeUsed = timeoutMap.get(author.getId());
            } else {
                lastTimeUsed = LocalDateTime.MIN;
            }

            LocalDateTime currentTime = LocalDateTime.now();
            Duration duration = Duration.between(lastTimeUsed, currentTime);
            long minutesDifference = duration.toMinutes();

            if (minutesDifference > MAX_TIME_OUT_FOR_ASSEMBLEMURS) {
                notifyChannel(event, lemurs, author, embedBuilder);
                sentPrivateMessagesToTheUsers(event, lemurs);
                timeoutMap.put(author.getId(), currentTime);
            } else {
                String user = author.getName();
                String message = user + ", you can use this command every " + MAX_TIME_OUT_FOR_ASSEMBLEMURS
                        + " minutes and the last time you used the command was: " + duration.getSeconds() + " seconds ago.";
                embedBuilder.addField("[ANTI-SPAM Timeout]", message, true);
                event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();
            }
        } else {
            notifyChannel(event, lemurs, author, embedBuilder);
            sentPrivateMessagesToTheUsers(event, lemurs);
        }
    }

    private void notifyChannel(SlashCommandInteractionEvent event, Role lemurs, User author, EmbedBuilder embedBuilder) {
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        String textMessage = lemurs.getAsMention() + ASSEMBLEMURS_MESSAGE.getValue()
                + author.getName() + "#" + author.getDiscriminator() + " wants you to join him. ";
        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.addField("Hello!", textMessage, true).build()).queue();
    }

    private void sentPrivateMessagesToTheUsers(SlashCommandInteractionEvent event, Role lemurs) {
        if (ENABLE_SEND_PRIVATE_MESSAGES){
            List<Member> membersWithLemursRole = event.getGuild().getMembersWithRoles(lemurs);
            for (Member member : membersWithLemursRole) {
                //prevents bot from sending to itself or to the caller
                if (member.getUser().getId().equals(event.getJDA().getSelfUser().getId()) || member.getUser().equals(event.getUser())) {
                    continue;
                }
                PrivateChannel channel = member.getUser().openPrivateChannel().complete();
                String message = HELLO.getValue() + member.getUser().getName() + " Lemurios-" + event.getUser().getAsTag()
                        + INVITE_MESSAGE.getValue();
                channel.sendMessage(message).queue();
                LOGGER.info("Sent direct message to {}.", member.getUser().getAsTag());
            }
        }
    }

    private void notifyUnworthy(SlashCommandInteractionEvent event) {
        String sender = event.getUser().getAsTag();
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

        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

    }
}
