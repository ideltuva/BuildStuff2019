package seb.api.carbs.seb.repo;

import org.springframework.data.repository.CrudRepository;
import seb.api.carbs.seb.domain.Message;

public interface MessageRepository  extends CrudRepository<Message, Long> {

}
