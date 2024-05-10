package bot.application.services.repositories;

import bot.application.services.model.Meme;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemeRepository extends CrudRepository<Meme,Long> {
}
