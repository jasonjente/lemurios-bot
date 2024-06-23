package bot.application.services.meme;

import bot.application.domain.MemeServerRecord;
import bot.application.services.repositories.MemeRepository;
import bot.application.services.model.Meme;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemeService.class);
    @PersistenceContext
    private EntityManager entityManager;
    private final MemeRepository memeRepository;

    public Meme storeMeme(Meme meme){
        LOGGER.info("storeMeme() - ENTER");
        memeRepository.save(meme);
        LOGGER.info("storeMeme() - LEAVE");
        return meme;
    }

    //rollback if the user tries to upload duplicate names and disrespectfully throw everything in the trash.
    @Transactional(rollbackFor = DataIntegrityViolationException.class)
    public void storeMemes(final List<Meme> memes){
        LOGGER.info("storeMemes() - ENTER - total memes: {}", memes.size());
        memeRepository.saveAll(memes);
        LOGGER.info("storeMemes() - LEAVE");
    }

    @Transactional
    public Meme getRandomMeme(final SlashCommandInteractionEvent event){
        LOGGER.info("getMeme - ENTER");
        var discordServerId = event.getGuild().getId();
        var meme = retrieveMemeByServerId(discordServerId);
        LOGGER.info("getMeme - LEAVE");
        return meme;
    }

    private Meme retrieveMemeByServerId(final String discordServerId) {
        LOGGER.info("retrieveMemeByServerId - ENTER - discord server: {}", discordServerId);

            var meme = retrieveMemeNotReturnedForThisServer(discordServerId);
            if (meme != null) {
                // Mark the meme as returned for this discordServerId
                markMemeAsReturned(meme, discordServerId);
                LOGGER.info("retrieveMemeByServerId - LEAVE meme id: {}, filename: {}",
                        meme.getMemeId(), meme.getFilename());
                return meme;
            } else {
                LOGGER.info("Null object??");
            }

        LOGGER.info("retrieveMemeByServerId - found nothing - LEAVE");
        return null;
    }

    private Meme retrieveMemeNotReturnedForThisServer(final String discordServerId) {
        var query = entityManager.createQuery(
                "SELECT m " +
                        "FROM" +
                            " Meme m " +
                        "WHERE NOT EXISTS" +
                            "(SELECT msr" +
                                " FROM MemeServerRecord msr" +
                            " WHERE msr.meme = m AND" +
                                  " msr.discordServerId = :discordServerId)", Meme.class);
        query.setParameter("discordServerId", discordServerId);
        query.setMaxResults(1);
        var memes = query.getResultList();
        if (memes.isEmpty()) {
            query = entityManager.createQuery("SELECT m from Meme m where exists " +
                    "(select msr from MemeServerRecord msr where msr.memeId = m.memeId)", Meme.class);
            memes = query.getResultList();
        }
            return memes.get(0);

    }

    private void markMemeAsReturned(final Meme meme, final String discordServerId) {
        var memeServerRecord = new MemeServerRecord();
        memeServerRecord.setMeme(meme);
        memeServerRecord.setDiscordServerId(discordServerId);
        entityManager.persist(memeServerRecord);
    }

}
