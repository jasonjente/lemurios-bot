package bot;

import bot.commands.chat.*;
import bot.commands.music.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ApplicationConfigurationTests {

    @Autowired
    private ApplicationContext applicationContext;


    /**
     * This method tests that the beans of the application configuration have been properly instantiated
     */
    @Test
    void shouldLoadContext() {
        // MusicCommandsConfiguration beans
        assertBeanExists("disconnectCommand", DisconnectCommand.class);
        assertBeanExists("joinCommand", JoinCommand.class);
        assertBeanExists("nowPlayingCommand", NowPlayingCommand.class);
        assertBeanExists("pauseCommand", PauseCommand.class);
        assertBeanExists("playCommand", PlayCommand.class);
        assertBeanExists("resumeCommand", ResumeCommand.class);
        assertBeanExists("skipCommand", SkipCommand.class);
        assertBeanExists("stopCommand", StopCommand.class);

        // ChatCommandsConfiguration beans
        assertBeanExists("assemblemursCommand", AssemblemursCommand.class);
        assertBeanExists("createInviteCommand", CreateInviteCommand.class);
        assertBeanExists("creditsCommand", CreditsCommand.class);
        assertBeanExists("helpCommand", HelpCommand.class);
        assertBeanExists("takenNamesCommand", TakenNamesCommand.class);
    }

    private <T> void assertBeanExists(final String beanName, final Class<T> beanClass) {
        T bean = applicationContext.getBean(beanName, beanClass);
        assertThat(bean).isNotNull();
    }
}