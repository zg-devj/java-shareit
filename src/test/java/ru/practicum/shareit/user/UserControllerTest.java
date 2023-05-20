package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_Normal_Return201() throws Exception {
        UserDto user = getNormalUser();

        Mockito.when(userService.saveUser(user)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"));
    }

    @Test
    void createUser_EmptyName_ReturnBadRequest() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("")
                .email("user@example.com")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createUser_WrongEmail_ReturnBadRequest() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("user")
                .email("example.com")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateUser_Normal() throws Exception {
        UserDto userUpdated = UserDto.builder()
                .id(1L)
                .name("updatedUser")
                .email("user@example.com")
                .build();
        Mockito.when(userService.updateUser(userUpdated))
                .thenReturn(userUpdated);
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", 1L)
                .content(objectMapper.writeValueAsString(userUpdated))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void get_FindById_Noraml() throws Exception {
        UserDto user = getNormalUser();

        Mockito.when(userService.findUserById(1L)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("user"));
    }

    @Test
    void delete_Normal() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void get_FindAll_Normal() throws Exception {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@example.com")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("user2")
                .email("user2@example.com")
                .build();

        List<UserDto> userList = List.of(user1, user2);

        Mockito.when(userService.findAllUsers()).thenReturn(userList);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .json(objectMapper.writeValueAsString(Arrays.asList(user1, user2))));
    }

    private UserDto getNormalUser() {
        return UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
    }
}