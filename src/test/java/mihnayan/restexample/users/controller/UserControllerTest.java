package mihnayan.restexample.users.controller;

import mihnayan.restexample.App;
import mihnayan.restexample.users.dao.RoleRepository;
import mihnayan.restexample.users.dao.UserRepository;
import mihnayan.restexample.users.entity.Role;
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
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static io.qala.datagen.RandomValue.*;
import static io.qala.datagen.StringModifier.Impls.*;
import static io.qala.datagen.RandomShortApi.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@WebAppConfiguration
public class UserControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private MockMvc mockMvc;

    private AtomicLong idGenerator = new AtomicLong();
    private List<User> testUsers;
    private List<Role> roles;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    public UserControllerTest() {
        int size = 100 + (int) Math.round(Math.random()*1_000);
        this.testUsers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            User user = generateUser();
            this.testUsers.add(user);
        }
    }

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.userRepository.deleteAllInBatch();
        roleRepository.deleteAllInBatch();

        userRepository.save(this.testUsers);

        this.roles = generateRoles(7);
        roleRepository.save(roles);
    }

    @Test
    public void getAllUsersTest() throws Exception {
        int testUserPosition = Math.round(testUsers.size() / 3);
        User testUser = testUsers.get(testUserPosition);

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
        User anyUser = generateUser();
        mockMvc.perform(get("/user/" + anyUser.getId()))
                .andExpect(status().isNotFound());

        User testUser = testUsers.get(Math.round(testUsers.size() / 5));
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
    public void addUserWithRoles() throws Exception {
        User newUser = generateUser();
        this.mockMvc.perform(put("/user/add")
                .contentType(contentType)
                .content(userToJsonWithRoles(newUser, 1, 2, 3)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/user/" + newUser.getId()));

        mockMvc.perform(get("/user/" + newUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(newUser.getId().intValue())))
                .andExpect(jsonPath("$.name", is(newUser.getName())))
                .andExpect(jsonPath("$.login", is(newUser.getLogin())))
                .andExpect(jsonPath("$.password", is(newUser.getPassword())))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0].id", isOneOf(1,2,3)))
                .andExpect(jsonPath("$.roles[1].id", isOneOf(1,2,3)))
                .andExpect(jsonPath("$.roles[2].id", isOneOf(1,2,3)))
                .andExpect(jsonPath("$.roles[2].name",
                        isOneOf("role #1","role #2","role #3")));
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
        User testUser = testUsers.get(Math.round(testUsers.size() / 7));
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

    @Test
    public void editUserWithRolesTest() throws Exception {
        User newUser = generateUser();
        this.mockMvc.perform(put("/user/add")
                .contentType(contentType)
                .content(userToJsonWithRoles(newUser, 1, 2, 3)))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/user/" + newUser.getId()));

        String editRequestStr = "/user/edit";
        this.mockMvc.perform(post(editRequestStr)
                .contentType(contentType)
                .content(userToJsonWithRoles(newUser, 1, 2, 3)))
                .andExpect(status().isNotModified());

        this.mockMvc.perform(post(editRequestStr)
                .contentType(contentType)
                .content(userToJsonWithRoles(newUser, 5,6,7)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0].id", isOneOf(5,6,7)))
                .andExpect(jsonPath("$.roles[1].id", isOneOf(5,6,7)))
                .andExpect(jsonPath("$.roles[2].id", isOneOf(5,6,7)))
                .andExpect(jsonPath("$.roles[2].name",
                        isOneOf("role #5","role #6","role #7")));
    }

    private User generateUser() {
        User user = new User();
        user.setId(idGenerator.incrementAndGet());
        user.setName(length(30).with(spaces()).alphanumeric());
        user.setLogin(english(15));
        user.setPassword(length(15).with(specialSymbol()).english());

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

    private String userToJsonWithRoles(User user, int role1, int role2, int role3) {
        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObject json = factory.createObjectBuilder()
                .add("id", user.getId())
                .add("name", user.getName())
                .add("login", user.getLogin())
                .add("password", user.getPassword())
                .add("roles", factory.createArrayBuilder()
                        .add(factory.createObjectBuilder()
                                .add("id", role1)
                                .add("name", "role #" + role1).build())
                        .add(factory.createObjectBuilder()
                                .add("id", role2)
                                .add("name", "role #" + role2).build())
                        .add(factory.createObjectBuilder()
                                .add("id", role3)
                                .add("name", "role #" + role3).build())
                        .build())
                .build();

        return json.toString();
    }

    private List<Role> generateRoles(int rolesCount) {
        List<Role> roles = new ArrayList<>(rolesCount);
        for (int i = 1; i <= rolesCount; i++) {
            roles.add(new Role(i, "role #" + i));
        }
        return roles;
    }

}
