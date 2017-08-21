package mihnayan.restexample.users.controller;

import mihnayan.restexample.users.dao.UserRepository;
import mihnayan.restexample.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller class for User
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") Long id) {
        return this.userRepository.getUserById(id);
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.DELETE)
    public void deleteUser() {

    }

    @RequestMapping(value = "/add", method = RequestMethod.PUT)
    public ResponseEntity<?> addUser(@RequestBody User user) {
        userRepository.save(user);
        URI locatiton = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("/user/" + user.getId())
                .build().toUri();
        return ResponseEntity.created(locatiton).build();
    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.POST)
    public void editUser(User user) {

    }

    private User generateUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("Вася");
        user.setLogin("vasa");
        user.setPassword("123123");
        return user;
    }
}
