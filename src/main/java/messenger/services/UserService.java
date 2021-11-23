package messenger.services;

import lombok.extern.slf4j.Slf4j;
import messenger.models.User;
import messenger.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        log.info("Save new user - " + user.getUsername());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.info("Find user by username - " + username);
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long Id){
        return userRepository.findById(Id);
    }

    @Transactional(readOnly = true)
    public List<User> getUserList(){
        return userRepository.findAll();
    }

    @Transactional
    public void addFriend(User user, User addUser){
        user.addUser(addUser);
        userRepository.save(user);
    }

    @Transactional
    public void changeUserInfo(User user, User userForm){
        if (userForm.getFirstName() != null){
            user.setFirstName(userForm.getFirstName());
        }
        if (userForm.getLastName() != null){
            user.setLastName(userForm.getLastName());
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Set<User> findUser(User user){
        List<User> users = new ArrayList<>();
        String str = user.getUsername();
        if (userRepository.findByUsername(str)!=null){
            users.add(userRepository.findByUsername(user.getUsername()));
        }
        String[] split = str.split(" ");
        if (userRepository.findByFirstName(split[0])!=null){
            users.addAll(userRepository.findByFirstName(user.getUsername()));
        }
        if (userRepository.findByLastName(split[0])!=null){
            users.addAll(userRepository.findByLastName(user.getUsername()));
        }
        try {
            if (userRepository.findByFirstName(split[1]) != null) {
                users.addAll(userRepository.findByFirstName(user.getUsername()));
            }
            if (userRepository.findByLastName(split[1]) != null) {
                users.addAll(userRepository.findByLastName(user.getUsername()));
            }
        } catch (Exception ignored) {}
        return new HashSet<>(users);
    }

    @Transactional(readOnly = true)
    public User getUser() {
        String username = "";
        try {
            var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
        } catch (NullPointerException e) {
            username = "test";
        }
        log.info("Get user by username - " + username);
        return userRepository.findByUsername(username);
    }
}
