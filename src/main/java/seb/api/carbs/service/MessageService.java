package seb.api.carbs.service;

import org.springframework.stereotype.Service;
import seb.api.carbs.seb.domain.Message;
import seb.api.carbs.seb.repo.MessageRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class MessageService {

    private MessageRepository messageRepository;

    @PersistenceContext
    EntityManager entityManager;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Iterable<Message> list() {
        return messageRepository.findAll();
    }

    public void save(List<Message> messages) {
        messageRepository.saveAll(messages);
    }
}
