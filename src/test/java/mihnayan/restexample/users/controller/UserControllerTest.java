package mihnayan.restexample.users.controller;

import mihnayan.restexample.App;
import mihnayan.restexample.users.dao.UserRepository;
import mihnayan.restexample.users.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.json.Json;
import javax.json.JsonObject;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class UserControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.userRepository.deleteAllInBatch();
    }

    public void userTest() {
        long userId = Math.round(Math.random() * 1_000_000);
        this.userRepository.save(generateUser());
        User user = this.userRepository.getUserById(userId);
        assertNotNull(user);
        assertEquals("Вася", user.getName());
    }

    @Test
    public void addUserTest() throws Exception {
        User newUser = generateUser();
        this.mockMvc.perform(put("/user/add")
                .contentType(contentType)
                .content(userToJson(newUser)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/user/" + newUser.getId()));
    }

    private User generateUser() {
        User user = new User();
        user.setId(Math.round(Math.random() * 1_000_000));
        user.setName("Вася");
        user.setLogin("vasa");
        user.setPassword("123123");
        return user;
    }

    private String userToJson(User user) {
        JsonObject json = Json.createObjectBuilder()
                .add("id", user.getId())
                .add("name", user.getName())
                .add("login", user.getLogin())
                .add("password", user.getPassword()).build();

        return json.toString();
    }

}
