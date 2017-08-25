package mihnayan.restexample.users.controller;

import mihnayan.restexample.users.dao.UserRepository;
import mihnayan.restexample.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
        User user = this.userRepository.getUserById(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
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

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResponseEntity<?> editUser(@RequestBody User user) {
        User editedUser = userRepository.getUserById(user.getId());
        if (editedUser == null) return ResponseEntity.notFound().build();
        if (editedUser.equals(user)
                && editedUser.getRoles().equals(user.getRoles())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

}
