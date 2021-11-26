package messenger.controllers;

import messenger.entities.Message;
import messenger.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import messenger.services.UserService;

import java.util.stream.Collectors;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String getIndex(Model model) {
        var chatRooms = messageService.getChatRoomListForUser(userService.getUser());
        model.addAttribute("chat_rooms", chatRooms);
        model.addAttribute("profile", userService.getUser());
        model.addAttribute("flag", false);
        return "index";
    }

    @GetMapping("/chat_room/{id}")
    public String getChatRoom(@PathVariable int id, Model model) {
        var chatRooms = messageService.getChatRoomListForUser(userService.getUser());
        model.addAttribute("chat_rooms", chatRooms);
        model.addAttribute("profile", userService.getUser());

        var chatRoom = messageService.getChatRoomById((long) id);
        if (chatRoom.isPresent()) {


            model.addAttribute("messages", chatRoom.get().getMessageList());

            model.addAttribute("other_user", chatRoom.get().getUserList().stream()
                    .filter(e -> e != userService.getUser()).collect(Collectors.toList()).get(0));
            var mes = new Message();
            mes.setUser(userService.getUser());
            model.addAttribute("chat_room_id", id);
            model.addAttribute("message", mes);
            model.addAttribute("flag", true);
            return "index";
        } else {
            model.addAttribute("reason", "Не найден диалог с id " + id);
            return "error";
        }
    }

    @PostMapping("/chat_room/{id}")
    public String addMessage(@PathVariable int id, @ModelAttribute("message") Message message, Model model) {
        var chatRoom = messageService.getChatRoomById((long) id);
        if (chatRoom.isPresent()) {
            messageService.addNewMessageToChatRoom(message, chatRoom.get());
            return "redirect:/chat_room/" + id;
        } else {
            model.addAttribute("reason", "Не найден диалог с id " + id);
            return "error";
        }
    }

    @PostMapping("/chat_room/new/{id}")
    public String addChatRoom(@PathVariable int id, Model model) {
        var user = userService.findById((long) id);
        if (user.isPresent()) {
            if (!userService.getUser().getFriends().contains(user.get())){
                model.addAttribute("reason",
                        "Пользователь " + user.get().getUsername() + " у вас не в друзьях!");
                return "error";
            }
            var chatRoom = messageService.addNewChatRoomIfEmpty(userService.getUser(), user.get());
            return "redirect:/chat_room/" + chatRoom.getId();
        } else {
            model.addAttribute("reason", "Не найден пользователь с id " + id);
            return "error";
        }
    }
}
