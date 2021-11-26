package messenger.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private List<Message> messageList = new ArrayList<>();

    @ManyToMany
    private List<User> userList = new ArrayList<>();

    public void addMessage(Message message){
        messageList.add(message);
    }

    public void addUser(User user){
        userList.add(user);
    }
}
