package bot;

import bot.application.configuration.chat.ChatCommandsConfiguration;
import bot.application.configuration.meme.MemeCommandsConfiguration;
import bot.application.configuration.music.MusicCommandsConfiguration;
import org.springframework.context.annotation.Import;

@Import({ ChatCommandsConfiguration.class, MemeCommandsConfiguration.class, MusicCommandsConfiguration.class})
public class ApplicationConfiguration {
}
