package bot.commands.chat;

import bot.application.constants.Lemurioi;
import bot.commands.Command;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static bot.application.constants.Commands.ASSEMLEMURS_COMMAND;
import static bot.application.constants.MessageConstants.*;

@Slf4j
public class AssemblemursCommand extends Command {
    
    private static final String ROLE_NAME = "LEMURIOI";
    private static final boolean ENABLE_SEND_PRIVATE_MESSAGES = true;
    private static final long SECONDS = 60;
    private static final long MINUTES = 1;
    //Better to handle in seconds than minutes
    private static final long MAX_TIME_OUT_FOR_ASSEMBLEMURS = MINUTES * SECONDS;
    private static final boolean ENABLE_TIMEOUTS = true;
    private static final Map<String, LocalDateTime> timeoutMap = new HashMap<>();


    @Override
    public void execute(final SlashCommandInteractionEvent event) {
        log.info("assemblemursCommand - ENTER");
        if (userIsLemurWorthy(event)) {
            doAssemble(event);
        } else {
            notifyUnworthy(event);
        }
        log.info("assemblemursCommand - LEAVE");
    }

    @Override
    public String getCommandDescription() {
        return "pings all lemurs that belong in the LEMURIOI role. " +
                "\nTo call the other lemurs you will have to be a member of lemurs :).";
    }

    @Override
    public String getCommandName() {
        return ASSEMLEMURS_COMMAND.getCommandName();
    }

    private boolean userIsLemurWorthy(final SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        Role lemurs = event.getGuild().getRolesByName(ROLE_NAME, true).get(0);
        if (!event.getMember().getRoles().contains(lemurs)) {
            log.info("{} is not worthy", sender);
            return false;
        }
        log.info("{} is worthy", sender);
        return true;
    }

    private void doAssemble(final SlashCommandInteractionEvent event) {
        var lemurs = event.getGuild().getRolesByName(ROLE_NAME, true).get(0);
        var author = event.getUser();
        var embedBuilder = new EmbedBuilder()
                .setTitle("ASSEMBLEMURS")
                .setColor(Color.YELLOW);

        if (ENABLE_TIMEOUTS) {
            LocalDateTime lastTimeUsed;

            if (timeoutMap.containsKey(author.getId())) {
                lastTimeUsed = timeoutMap.get(author.getId());
            } else {
                lastTimeUsed = LocalDateTime.MIN;
            }

            LocalDateTime currentTime = LocalDateTime.now();
            var duration = Duration.between(lastTimeUsed, currentTime);
            long timeDiffInSeconds = duration.getSeconds();

            if (timeDiffInSeconds > MAX_TIME_OUT_FOR_ASSEMBLEMURS) {
                notifyChannel(event, lemurs, author, embedBuilder);
                sentPrivateMessagesToTheUsers(event, lemurs);
                timeoutMap.put(author.getId(), LocalDateTime.now());
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

    private void notifyChannel(final SlashCommandInteractionEvent event, final Role lemurs,
                               final User author, final EmbedBuilder embedBuilder) {
        if (event.getOptions().isEmpty()) {
            var textMessage = lemurs.getAsMention() + ASSEMBLEMURS_MESSAGE.getValue()
                    + author.getAsMention() + " wants you to join them. ";
            event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.addField("Hello!", textMessage,
                    true).build()).queue();
        } else {
            var game = event.getOptions().get(0).getAsString();
            var textMessage = lemurs.getAsMention() + ASSEMBLEMURS_MESSAGE.getValue()
                    + author.getAsMention() + " wants you to play " + game + " with them!";
            event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.addField("Hello!", textMessage,
                    true).build()).queue();
        }
    }

    private void sentPrivateMessagesToTheUsers(final SlashCommandInteractionEvent event, final Role lemurs) {
        if (ENABLE_SEND_PRIVATE_MESSAGES) {
            var membersWithLemursRole = event.getGuild().loadMembers()
                    .get()
                    .stream()
                    .filter(member -> member.getRoles().contains(lemurs))
                    .toList();
            for (var member : membersWithLemursRole) {
                //prevents bot from sending to itself or to the caller or
                // the users that are in the same voice channel as the caller
                if (member.getUser().getId().equals(event.getJDA().getSelfUser().getId())
                        || member.getUser().equals(event.getUser())
                        || isUserInSameVoiceChannelAsUser(event, member)) {
                    continue;
                }
                var channel = member.getUser().openPrivateChannel().complete();
                var message = HELLO.getValue() + member.getUser().getName() + " Lemurios - "
                        + event.getUser().getAsMention()
                        + INVITE_MESSAGE.getValue();
                channel.sendMessage(message).queue();
                log.info("Sent direct message to {}.", member.getUser().getName());
            }
        }
    }

    public boolean isUserInSameVoiceChannelAsUser(final SlashCommandInteractionEvent event, final Member lemurMember) {
        try {
            Objects.requireNonNull(event.getInteraction().getMember()).getVoiceState();
            if (event.getInteraction().getMember().getVoiceState() != null
                    && event.getInteraction().getMember().getVoiceState().inAudioChannel()) {
                var voiceChannel = event.getInteraction().getMember().getVoiceState().getChannel().asVoiceChannel();
                var members = voiceChannel.getMembers();
                var isLemur = members.contains(lemurMember);
                log.info("Checking user {} is in the same voice channel as the caller {}: {}",
                        lemurMember.getUser().getName(), event.getUser().getName(), isLemur);
                return isLemur;
            }
        } catch (NullPointerException exception) {
            //not in a voice channel so suppress and return false
        }

        return false;
    }

    private void notifyUnworthy(final SlashCommandInteractionEvent event) {
        String sender = event.getUser().getName();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(sender + " sorry, but you are not worthy enough to call the Lemurs. :/")
                .setDescription("Sorry " + sender + ", it seems you dont belong to the lemurs group.")
                .addField("Q: What do I now?",
                        "A: To become a member of lemurs you have to change your name " +
                                "in League of Legends to a name with this format (also legally):", false)
                .addField("1: How should my name look like?",
                        "Lemurios XXX, where XXX are three **(3)** integers like 123, 012, 006", true)
                .addField("2: Is there any name I cannot choose?",
                        "Lemurios 069 and Lemurios 420 are forbidden from being chosen", true)
                .addField("3: What are the available Lemurios XXX names?",
                        "Here is a list with all claimed lemurs: \n" + Lemurioi.usedNames(), true)
                .addField("Finally ",
                        "After these steps have been fulfilled, please contact Lemurios 002 or Lemurios 007" +
                                " to proceed with your application.", true)
                .setColor(java.awt.Color.RED)
                .setFooter(GTFO_MESSAGE.getValue());

        event.getInteraction().getHook().editOriginalEmbeds(embedBuilder.build()).queue();

    }
}
