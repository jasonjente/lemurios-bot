package bot.application.configuration.chat;

import bot.commands.chat.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatCommandsConfiguration {

    /**
     * This method returns an instance of the AssemblemursCommand bean.
     *
     * @return an instance of the Assemblemurs command Bean.
     */
    @Bean
    AssemblemursCommand assemblemursCommand() {
        return new AssemblemursCommand();
    }

    /**
     * This method returns an instance of the CreateInviteCommand bean.
     *
     * @return an instance of the Create Invite Command Bean.
     */
    @Bean
    CreateInviteCommand createInviteCommand() {
        return new CreateInviteCommand();
    }

    /**
     * This method returns an instance of the CreditsCommand bean.
     *
     * @return an instance of the Credits Command Bean.
     */
    @Bean
    CreditsCommand creditsCommand() {
        return new CreditsCommand();
    }

    /**
     * This method returns an instance of the HelpCommand bean.
     *
     * @return an instance of the Help Command Bean.
     */
    @Bean
    HelpCommand helpCommand() {
        return new HelpCommand();
    }

    /**
     * This method returns an instance of the TakenNamesCommand bean.
     *
     * @return an instance of the Taken Names Command bean.
     */
    @Bean
    TakenNamesCommand takenNamesCommand() {
        return new TakenNamesCommand();
    }
}
