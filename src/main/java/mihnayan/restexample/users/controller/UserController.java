package mihnayan.restexample.users.controller;

import mihnayan.restexample.users.entity.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller class for User
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return null;
    }

    @RequestMapping(value = "{id}/get", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Вася");
        user.setLogin("vasa");
        user.setPassword("123123");
        return user;
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.DELETE)
    public void deleteUser() {

    }

    @RequestMapping(value = "/add", method = RequestMethod.PUT)
    public void addUser(User user) {

    }

    @RequestMapping(value = "{id}/edit", method = RequestMethod.POST)
    public void editUser(User user) {

    }
}
