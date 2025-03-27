package bot.application.configuration;

import bot.application.configuration.chat.ChatCommandsConfiguration;
import bot.application.configuration.music.MusicCommandsConfiguration;
import org.springframework.context.annotation.Import;

@Import({ ChatCommandsConfiguration.class, MusicCommandsConfiguration.class})
public class ApplicationConfiguration {
}
