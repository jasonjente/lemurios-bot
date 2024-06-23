package bot.application.configuration.meme;

import bot.commands.concrete.meme.MemeCommand;
import bot.commands.concrete.meme.UploadBatchMemesCommand;
import bot.commands.concrete.meme.UploadMemeCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemeCommandsConfiguration {

    /**
     * This method returns an instance of the MemeCommand bean.
     * @return an instance of the Meme Command Bean.
     */
    @Bean
    MemeCommand memeCommand() {
        return new MemeCommand();
    }

    /**
     * This method returns an instance of the UploadMemeCommand bean.
     * @return an instance of the Upload Meme Command Bean.
     */
    @Bean
    UploadMemeCommand uploadMemeCommand() {
        return new UploadMemeCommand();
    }

    /**
     * This method returns an instance of the UploadBatchMemesCommand bean.
     * @return an instance of the Upload Batch Memes Command Bean.
     */
    @Bean
    UploadBatchMemesCommand uploadBatchMemesCommand() {
        return new UploadBatchMemesCommand();
    }
}
