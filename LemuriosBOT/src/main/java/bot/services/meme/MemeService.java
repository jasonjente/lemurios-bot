package bot.services.meme;

import bot.services.model.Meme;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public interface MemeService {
    Meme storeMeme(Meme meme);

    void storeMemes(List<Meme> memes);

    Meme getRandomMeme(SlashCommandInteractionEvent event);
}
