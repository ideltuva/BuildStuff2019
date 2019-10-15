package seb.api.carbs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import seb.api.carbs.seb.domain.Message;
import seb.api.carbs.service.MessageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class KafkaReceiver {

    @Autowired
    MessageService messageService;

    @KafkaListener(topics = "${app.topic.name}", containerFactory="kafkaListenerContainerFactory")
    public void receiveTopic(ConsumerRecord<?, ?> consumerRecord) throws IOException {
        System.out.println(consumerRecord.value());

        ObjectMapper mapper = new ObjectMapper();

        TypeReference<List<Message>> typeReference = new TypeReference<List<Message>>() {};

        InputStream sk = IOUtils.toInputStream(consumerRecord.value().toString(), "UTF-8");

        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

            //Files.write(Paths.get("message"+timestamp+".json"), consumerRecord.value().toString().getBytes());

            List<Message> messages = mapper.readValue(sk, typeReference);
            messageService.save(messages);

            System.out.println("Messages saved!");
        } catch (IOException e) {
            System.out.println("Unable to save messages: " + e.getMessage());
            e.printStackTrace();
        }


    }


}
