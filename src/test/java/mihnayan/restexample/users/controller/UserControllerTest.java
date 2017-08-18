package mihnayan.restexample.users.controller;

import mihnayan.App;
import mihnayan.restexample.users.dao.UserRepository;
import mihnayan.restexample.users.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.userRepository.deleteAllInBatch();
    }

    @Test
    public void userTest() {
        long userId = Math.round(Math.random() * 1_000_000);
        this.userRepository.save(getUser(userId));
        User user = this.userRepository.getUserById(userId);
        assertNotNull(user);
        assertEquals("Вася", user.getName());
    }

    private User getUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("Вася");
        user.setLogin("vasa");
        user.setPassword("123123");
        return user;
    }
}
