package mihnayan.restexample.users.controller;

import mihnayan.restexample.users.dao.UserRepository;
import mihnayan.restexample.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;

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
    public Collection<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") Long id) {
        return this.userRepository.getUserById(id);
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        User user = userRepository.getUserById(id);
        if (user == null) return ResponseEntity.noContent().build();

        userRepository.delete(user.getId());
        return ResponseEntity.ok(user);
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

}
