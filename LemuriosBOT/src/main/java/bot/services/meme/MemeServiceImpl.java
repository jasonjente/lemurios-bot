package bot.services.meme;

import bot.services.model.Meme;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service
public class MemeServiceImpl implements MemeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemeServiceImpl.class);
    @PersistenceContext
    private EntityManager entityManager;
    private MemeRepository memeRepository;

    @Override
    public Meme storeMeme(Meme meme){
        LOGGER.info("storeMeme() - ENTER");
        memeRepository.save(meme);
        LOGGER.info("storeMeme() - LEAVE");
        return meme;
    }

    @Override
    public void storeMemes(List<Meme> memes){
        LOGGER.info("storeMemes() - ENTER - total memes: {}", memes.size());
        memeRepository.saveAll(memes);
        LOGGER.info("storeMemes() - LEAVE");
    }

    @Transactional
    @Override
    public Meme getRandomMeme(SlashCommandInteractionEvent event){
        LOGGER.info("getMeme - ENTER");
        String discordServerId = event.getGuild().getId();
        Meme meme = retrieveMemeByServerId(discordServerId);
        LOGGER.info("getMeme - LEAVE");
        return meme;
    }

    private Meme retrieveMemeByServerId(String discordServerId) {
        LOGGER.info("retrieveMemeByServerId - ENTER - discord server: {}", discordServerId);

            Meme meme = retrieveMemeNotReturnedForThisServer(discordServerId);
            if (meme != null) {
                // Mark the meme as returned for this discordServerId
                markMemeAsReturned(meme, discordServerId);
                LOGGER.info("retrieveMemeByServerId - LEAVE meme id: {}, filename: {}", meme.getMemeId(), meme.getFilename());
                return meme;
            } else {
                LOGGER.info("Null object??");
            }

        LOGGER.info("retrieveMemeByServerId - found nothing - LEAVE");
        return null;
    }

    private Meme retrieveMemeNotReturnedForThisServer(String discordServerId) {
        TypedQuery<Meme> query = entityManager.createQuery(
                "SELECT m FROM Meme m WHERE NOT EXISTS (SELECT msr FROM MemeServerRecord msr WHERE msr.meme = m AND msr.discordServerId = :discordServerId)",
                Meme.class);
        query.setParameter("discordServerId", discordServerId);
        query.setMaxResults(1);
        List<Meme> memes = query.getResultList();
        if (memes.isEmpty()) {
            query = entityManager.createQuery("SELECT m from Meme m where exists (select msr from MemeServerRecord msr where msr.memeId = m.memeId)", Meme.class);
            memes = query.getResultList();
        }
            return memes.get(0);

    }

    private void markMemeAsReturned(Meme meme, String discordServerId) {
        MemeServerRecord memeServerRecord = new MemeServerRecord();
        memeServerRecord.setMeme(meme);
        memeServerRecord.setDiscordServerId(discordServerId);
        entityManager.persist(memeServerRecord);
    }
    @Autowired
    public void setMemeRepository(MemeRepository memeRepository) {
        this.memeRepository = memeRepository;
    }
}
