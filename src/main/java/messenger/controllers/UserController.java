package messenger.controllers;

import messenger.models.User;
import messenger.services.RoleService;
import messenger.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import messenger.components.UserValidator;
import messenger.services.UserService;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @GetMapping("/registration")
    public String registration(Model model) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userForm.setRoles(roleService.getUserRole());
        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (securityService.isAuthenticated()) {
            return "redirect:/";
        }

        if (error != null)
            model.addAttribute("error", "Ваш логин и/или пароль не верны.");

        if (logout != null)
            model.addAttribute("message", "Вы успешно вышли из аккаунта.");

        return "login";
    }

    @GetMapping("/profile")
    public String getUser(Model model) {
        var user = userService.getUser();
        model.addAttribute("user", user);
        model.addAttribute("userForm", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String setUserRole(@ModelAttribute("userForm") User userForm, Model model) {
        var user = userService.getUser();
        userService.changeUserInfo(user, userForm);
        model.addAttribute("user", user);
        model.addAttribute("userForm", user);
        return "profile";
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable int id, Model model) {
        var user = userService.findById((long) id);
        if (user.isEmpty()) {
            model.addAttribute("reason", "Не найден пользователь с id " + id);
            return "error";
        } else {
            if (userService.getUser() == user.get())
                return "profile";
            else {
                model.addAttribute("user", user.get());
                if (userService.getUser().getFriends().contains(user.get()))
                    model.addAttribute("userForm", null);
                else
                    model.addAttribute("userForm", user.get());
            }
            model.addAttribute("friends", userService.getUser().getFriends());
            model.addAttribute("flag", true);
            return "users";
        }
    }

    @PostMapping("/user/add")
    public String addUser(@ModelAttribute("userForm") User userForm, Model model) {
        userService.addFriend(userService.getUser(), userForm);
        userService.addFriend(userForm, userService.getUser());
        return "redirect:/user/" + userForm.getId().toString();
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getUserList());
        model.addAttribute("friends", userService.getUser().getFriends());
        return "users";
    }
    @GetMapping("/users/search")
    public String getUsersForSearch(Model model) {
        model.addAttribute("users", userService.getUserList());
        return addAttributeForSearch(model);
    }

    @PostMapping("/users/search")
    public String searchUser(@ModelAttribute("userForm") User userForm, Model model) {
        model.addAttribute("users", userService.findUser(userForm));
        return addAttributeForSearch(model);
    }

    private String addAttributeForSearch(Model model) {
        model.addAttribute("friends", userService.getUser().getFriends());
        model.addAttribute("userForm", new User());
        model.addAttribute("profile", userService.getUser());
        model.addAttribute("search", true);
        return "users";
    }
}
