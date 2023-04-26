package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void post_Create_Normal_Return201() throws Exception {
        User user = getNormalUser();

        when(userService.saveUser(user)).thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"));
    }

    @Test
    void post_Create_EmptyName_ReturnBadRequest() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("")
                .email("user@example.com")
                .build();
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void post_Create_WrongEmail_ReturnBadRequest() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("example.com")
                .build();
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void patch_Update() throws Exception {
        User userUpdated = User.builder()
                .id(1L)
                .name("updatedUser")
                .email("user@example.com")
                .build();
        when(userService.updateUser(userUpdated))
                .thenReturn(userUpdated);
        mockMvc.perform(patch("/users/{id}", 1L)
                .content(objectMapper.writeValueAsString(userUpdated))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void get_FindById_Noraml() throws Exception {
        User user = getNormalUser();

        when(userService.findUserById(1L)).thenReturn(user);
        mockMvc.perform(get("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"));
    }

    private User getNormalUser() {
        return User.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
    }

    @Test
    void delete_Normal() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    void get_FindAll_Normal() throws Exception {
        User user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@example.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@example.com")
                .build();

        List<User> userList = List.of(user1, user2);

        when(userService.findAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(content()
                        .json(objectMapper.writeValueAsString(Arrays.asList(user1, user2))));
    }
}