package bot.application.configuration.music;

import bot.commands.concrete.music.*;
import bot.commands.concrete.music.radio.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Music commands.
 */
@Configuration
public class MusicCommandsConfiguration {

    /**
     * This method returns an instance of the DeleteAllCustomRadioLinkCommand bean.
     * @return an instance of the Delete All Custom Radio Link Command Bean.
     */
    @Bean
    DeleteAllCustomRadioLinkCommand deleteAllCustomRadioLinkCommand() {
        return new DeleteAllCustomRadioLinkCommand();
    }

    /**
     * This method returns an instance of the DeleteGenreCustomRadioLinkCommand bean.
     * @return an instance of the Delete Genre Custom Radio Link Command Bean.
     */
    @Bean
    DeleteGenreCustomRadioLinkCommand deleteGenreCustomRadioLinkCommand() {
        return new DeleteGenreCustomRadioLinkCommand();
    }

    /**
     * This method returns an instance of the GetCustomRadioLinkCommand bean.
     * @return an instance of the Get Custom Radio Link Command Bean.
     */
    @Bean
    GetCustomRadioLinkCommand getCustomRadioLinkCommand() {
        return new GetCustomRadioLinkCommand();
    }

    /**
     * This method returns an instance of the PlayCustomRadioCommand bean.
     * @return an instance of the Play Custom Radio Command Bean.
     */
    @Bean
    PlayCustomRadioCommand playCustomRadioCommand() {
        return new PlayCustomRadioCommand();
    }

    /**
     * This method returns an instance of the SetCustomRadioLinkCommand bean.
     * @return an instance of the Set Custom Radio Link Command Bean.
     */
    @Bean
    SetCustomRadioLinkCommand setCustomRadioLinkCommand() {
        return new SetCustomRadioLinkCommand();
    }

    /**
     * This method returns an instance of the DisconnectCommand bean.
     * @return an instance of the Disconnect Command Bean.
     */
    @Bean
    DisconnectCommand disconnectCommand() {
        return new DisconnectCommand();
    }

    /**
     * This method returns an instance of the JoinCommand bean.
     * @return an instance of the Join Command Bean.
     */
    @Bean
    JoinCommand joinCommand() {
        return new JoinCommand();
    }

    /**
     * This method returns an instance of the NowPlayingCommand bean.
     * @return an instance of the Now Playing Command Bean.
     */
    @Bean
    NowPlayingCommand nowPlayingCommand() {
        return new NowPlayingCommand();
    }

    /**
     * This method returns an instance of the PauseCommand bean.
     * @return an instance of the Pause Command Bean.
     */
    @Bean
    PauseCommand pauseCommand() {
        return new PauseCommand();
    }

    /**
     * This method returns an instance of the PlayCommand bean.
     * @return an instance of the Play Command Bean.
     */
    @Bean
    PlayCommand playCommand() {
        return new PlayCommand();
    }

    /**
     * This method returns an instance of the ResumeCommand bean.
     * @return an instance of the Resume Command Bean.
     */
    @Bean
    ResumeCommand resumeCommand() {
        return new ResumeCommand();
    }

    /**
     * This method returns an instance of the SkipCommand bean.
     * @return an instance of the Skip Command Bean.
     */
    @Bean
    SkipCommand skipCommand() {
        return new SkipCommand();
    }

    /**
     * This method returns an instance of the StopCommand bean.
     * @return an instance of the Stop Command Bean.
     */
    @Bean
    StopCommand stopCommand() {
        return new StopCommand();
    }
}

