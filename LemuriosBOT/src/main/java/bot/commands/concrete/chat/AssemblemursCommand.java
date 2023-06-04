package bot.commands.concrete.chat;

import bot.commands.Command;
import bot.constants.Lemurioi;
import bot.services.leveling.Jackpot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
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
import java.util.Objects;

import static bot.constants.Commands.ASSEMLEMURS_COMMAND;
import static bot.constants.Constants.*;

@Service
public class AssemblemursCommand extends Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblemursCommand.class);
    private static final String ROLE_NAME = "LEMURIOI";
    private static final boolean ENABLE_SEND_PRIVATE_MESSAGES = true;
    private static final long SECONDS = 60;
    private static final long MINUTES = 1;
    //Better to handle in seconds than minutes
    private static final long MAX_TIME_OUT_FOR_ASSEMBLEMURS = MINUTES * SECONDS;
    private static final boolean ENABLE_TIMEOUTS = true;
    private static final Map<String, LocalDateTime> timeoutMap = new HashMap<>();


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
            long timeDiffInSeconds = duration.getSeconds();

            if (timeDiffInSeconds > MAX_TIME_OUT_FOR_ASSEMBLEMURS) {
                notifyChannel(event, lemurs, author, embedBuilder);
                sentPrivateMessagesToTheUsers(event, lemurs);
                timeoutMap.put(author.getId(), LocalDateTime.now());
                Jackpot jackpot = jackpot(event);

                if(jackpot.isWon()){
                    String winningMessage = "Congratulations " + event.getUser().getName() + " ,you won the jackpot for a total of " + jackpot.getPoints();
                    LOGGER.info("{} won the jackpot with total winings: {}", event.getUser().getName(), jackpot.getPoints());
                    event.getChannel().sendMessage(winningMessage).queue();
                    earnPoints(event, jackpot.getPoints());
                } else {
                    earnPoints(event);
                }
            } else {
                String user = author.getName();
                String message = user + ", you can use this command every " + MAX_TIME_OUT_FOR_ASSEMBLEMURS
                        + " seconds and the last time you used the command was: " + duration.getSeconds() + " seconds ago.";
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
        if(event.getOptions().isEmpty()) {
            String textMessage = lemurs.getAsMention() + ASSEMBLEMURS_MESSAGE.getValue()
                    + author.getAsMention() + "#" + author.getDiscriminator() + " wants you to join him. ";
            event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.addField("Hello!", textMessage, true).build()).queue();
        }else {
            String game = event.getOptions().get(0).getAsString();
            String textMessage = lemurs.getAsMention() + ASSEMBLEMURS_MESSAGE.getValue()
                    + author.getAsMention() + "#" + author.getDiscriminator() + " wants you to play " + game + " with him!";
            event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.addField("Hello!", textMessage, true).build()).queue();
        }
    }

    private void sentPrivateMessagesToTheUsers(SlashCommandInteractionEvent event, Role lemurs) {
        if (ENABLE_SEND_PRIVATE_MESSAGES){
            List<Member> membersWithLemursRole = event.getGuild().getMembersWithRoles(lemurs);
            for (Member member : membersWithLemursRole) {
                //prevents bot from sending to itself or to the caller
                if (member.getUser().getId().equals(event.getJDA().getSelfUser().getId())
                        || member.getUser().equals(event.getUser())
                        || isUserInSameVoiceChannelAsUser(event, member)) {
                    continue;
                }
                PrivateChannel channel = member.getUser().openPrivateChannel().complete();
                String message = HELLO.getValue() + member.getUser().getName() + " Lemurios - " + event.getUser().getAsMention()
                        + INVITE_MESSAGE.getValue();
                channel.sendMessage(message).queue();
                LOGGER.info("Sent direct message to {}.", member.getUser().getAsTag());
            }
        }
    }

    public boolean isUserInSameVoiceChannelAsUser(SlashCommandInteractionEvent event, Member lemurMember){
        try {
            Objects.requireNonNull(event.getInteraction().getMember()).getVoiceState();
            if (event.getInteraction().getMember().getVoiceState() != null
                    && event.getInteraction().getMember().getVoiceState().inAudioChannel()) {
                VoiceChannel voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
                List<Member> members = voiceChannel.getMembers();
                LOGGER.info("Checking if user {} is in the same voice channel as the caller {}", lemurMember.getUser().getAsTag(), event.getUser().getAsTag());
                return members.contains(lemurMember);
            }
        }catch (NullPointerException exception){
            //not in a voice channel so suppress and return false
        }

        return false;
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
