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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class UserControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private MockMvc mockMvc;
    private User testUser;
    private int testUserPosition;
    private List<User> testUsers;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.userRepository.deleteAllInBatch();

        int size = (int) Math.round(Math.random()*1_000);
        this.testUsers = new ArrayList<>(size);
        AtomicLong idGenerator = new AtomicLong();
        for (int i = 0; i < size; i++) {
            User user = generateUser(idGenerator.incrementAndGet());
            this.testUsers.add(user);
            this.userRepository.save(user);
        }
        this.testUserPosition = Math.round(size / 3);
        this.testUser = testUsers.get(testUserPosition);
    }

    @Test
    public void getAllUsersTest() throws Exception {
        String expr = "$[" + testUserPosition + "]";
        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(testUsers.size())))
                .andExpect((jsonPath(expr + ".id", is(testUser.getId().intValue()))))
                .andExpect(jsonPath(expr + ".name", is(testUser.getName())))
                .andExpect(jsonPath(expr + ".login", is(testUser.getLogin())))
                .andExpect(jsonPath(expr + ".password", is(testUser.getPassword())));
    }

    @Test
    public void getUserTest() throws Exception {
        mockMvc.perform(get("/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.login", is(testUser.getLogin())))
                .andExpect(jsonPath("$.password", is(testUser.getPassword())));
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

    @Test
    public void deleteUserTest() throws Exception {
        User delUser = generateUser();
        this.userRepository.save(delUser);
        String delRequestStr = "/user/" + delUser.getId() + "/delete";
        this.mockMvc.perform(delete(delRequestStr))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(delUser.getId().intValue())))
                .andExpect(jsonPath("$.name", is(delUser.getName())))
                .andExpect(jsonPath("$.login", is(delUser.getLogin())))
                .andExpect(jsonPath("$.password", is(delUser.getPassword())));

        this.mockMvc.perform(delete(delRequestStr))
                .andExpect(status().isNoContent());
    }

    @Test
    public void editUserTest() throws Exception {
        String editRequestStr = "/user/edit";
        this.mockMvc.perform(post(editRequestStr)
                .contentType(contentType)
                .content(userToJson(testUser)))
                .andExpect(status().isNotModified());

        this.mockMvc.perform(post(editRequestStr)
                .contentType(contentType)
                .content(userToJson(generateUser())))
                .andExpect(status().isNotFound());

        testUser.setName("Petya");
        testUser.setLogin("petrucho");
        testUser.setPassword("P9tr_ch0");
        this.mockMvc.perform(post(editRequestStr)
                .contentType(contentType)
                .content(userToJson(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.login", is(testUser.getLogin())))
                .andExpect(jsonPath("$.password", is(testUser.getPassword())));
    }

    private User generateUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("Вася");
        user.setLogin("vasa");
        user.setPassword("123123");
        return user;
    }

    private User generateUser() {
        long id = Math.round(Math.random()*1_000_000);
        return generateUser(id);
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
