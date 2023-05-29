package bot.services.meme;

import bot.services.model.Meme;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemeRepository extends CrudRepository<Meme,Long> {
}
