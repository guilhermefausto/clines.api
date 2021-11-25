package br.com.caelum.clines.api.users;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.google.gson.Gson;

import br.com.caelum.clines.api.locations.LocationForm;
import br.com.caelum.clines.shared.domain.Country;
import br.com.caelum.clines.shared.domain.User;

@SpringBootTest
@TestPropertySource(properties = {"DB_NAME=clines_test","spring.jpa.hibernate.ddlAuto:create-drop"})
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityManager entityManager;
    
    private Gson gson = new Gson();

    @Test
    void shouldReturn404WhenNotExistUserById() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAnUsertById() throws Exception {
        var user = new User("Fulano", "fulano@email.com", "123456");

        User userEntity = entityManager.persist(user);

        mockMvc.perform(get("/users/"+userEntity.getId()))
                .andExpect(status().isOk())
                .andDo(log())
                .andExpect(jsonPath("$.name", equalTo(user.getName())))
                .andExpect(jsonPath("$.email", equalTo(user.getEmail())));
    }
    //listar usuarios
    @Test
    void listAllUsersShouldReturnListOfUsers() throws Exception {
    	var user1 = new User("Fulano 1", "fulano1@email.com", "123456");
    	var user2 = new User("Fulano 2", "fulano2@email.com", "123456");

        entityManager.persist(user1);
        entityManager.persist(user2);

        UserView userView1 = new UserView(user1.getName(),user1.getEmail());
        UserView userView2 = new UserView(user2.getName(),user2.getEmail());
        
        List<UserView> users = List.of(userView1, userView2);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(gson.toJson(users))
                );
    }
    
    @Test
    void listAllUsersShouldReturnAnEmptyList() throws Exception {
        List<UserView> users = new ArrayList<UserView>();

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(gson.toJson(users))
                );
    }
   
    //cadastrar
    @Test
    public void shouldReturnHttpStatus201AndHeaderAttributeLocationWhenValidFormIsInformed() throws Exception {
        UserForm userForm = new UserForm("Fulano 1", "fulano1@email.com", "123456");
        String userJson = gson.toJson(userForm);

        mockMvc.perform(post("/users/").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
    
    //cadastrar duplicado
    @Test
    public void shouldReturnHttpStatus409AndWhenUserAlreadyExists() throws Exception {
    	var user1 = new User("Fulano 3", "fulano3@email.com", "123456");

        entityManager.persist(user1);
    	
    	UserForm userForm = new UserForm("Fulano 3", "fulano3@email.com", "123456");
        String userJson = gson.toJson(userForm);

        mockMvc.perform(post("/users/").contentType(MediaType.APPLICATION_JSON).content(userJson))
                .andExpect(status().isConflict());
    }
}
