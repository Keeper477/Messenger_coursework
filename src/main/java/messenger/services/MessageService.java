package messenger.services;

import messenger.entities.ChatRoom;
import messenger.entities.Message;
import messenger.repositories.ChatRoomRepository;
import messenger.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import messenger.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Transactional(readOnly = true)
    public List<ChatRoom> getChatRoomListForUser(User currentUser) {
        return chatRoomRepository.findAll().stream()
                .filter(m -> m.getUserList().contains(currentUser)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ChatRoom> getChatRoomById(Long id) {
        return chatRoomRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ChatRoom> getChatRoomForUser(User currentUser, User messagingToUser) {
        var dialog = chatRoomRepository.findAll().stream()
                .filter(m -> m.getUserList().contains(currentUser) && m.getUserList().contains(messagingToUser))
                .collect(Collectors.toList());
        if (dialog.isEmpty())
            return Optional.empty();
        else
            return Optional.of(dialog.get(0));
    }

    @Transactional
    public ChatRoom addNewChatRoomIfEmpty(User currentUser, User messagingToUser) {
        var dialog = getChatRoomForUser(currentUser, messagingToUser);
        if (dialog.isEmpty()) {
            var d = new ChatRoom();
            d.addUser(currentUser);
            d.addUser(messagingToUser);
            chatRoomRepository.save(d);
            return d;
        } else {
            return dialog.get();
        }
    }

    @Transactional
    public void addNewMessageToChatRoom(Message message, ChatRoom chatRoom) {
        message.setId(messageRepository.count() + 1);
        messageRepository.save(message);
        chatRoom.addMessage(message);
        chatRoomRepository.save(chatRoom);
    }
}
