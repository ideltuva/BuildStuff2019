package seb.api.carbs.seb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Data
@AllArgsConstructor



@Entity
@Table(name = "messagefromkafka")
public class Message {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY  )
    private Integer id;
    private String message;

    public Message(Integer id) {
        this.id = id;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message() {

    }
}
