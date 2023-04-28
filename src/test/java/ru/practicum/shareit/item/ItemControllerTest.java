package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private Item item;
    private ItemDto addedItem;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@example.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        addedItem = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    @Test
    void createItem_Normal_return201() throws Exception {
        given(itemService.saveItem(anyLong(), any(Item.class))).willReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(addedItem))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name"));
    }

    @Test
    void createItem_EmptyName_ReturnBadRequest() throws Exception {
        ItemDto wrong = addedItem.toBuilder().name(null).build();
        given(itemService.saveItem(anyLong(), any(Item.class))).willReturn(item);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createItem_EmptyDescription_ReturnBadRequest() throws Exception {
        ItemDto wrong = addedItem.toBuilder().description(null).build();
        given(itemService.saveItem(anyLong(), any(Item.class))).willReturn(item);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createItem_EmptyAvailable_ReturnBadRequest() throws Exception {
        ItemDto wrong = addedItem.toBuilder().available(null).build();
        given(itemService.saveItem(anyLong(), any(Item.class))).willReturn(item);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_Normal() throws Exception {
        given(itemService.updateItem(anyLong(), any(Item.class))).willReturn(item);

        mockMvc.perform(patch("/items/{id}", 1L)
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(addedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void updateItem_UserIdIsNull_ReturnBadRequest() throws Exception {
        given(itemService.updateItem(anyLong(), any(Item.class))).willReturn(item);

        mockMvc.perform(patch("/items/{id}", 1L)
                .content(objectMapper.writeValueAsString(addedItem))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void findById_Normal() throws Exception {
        given(itemService.findById(anyLong())).willReturn(item);

        mockMvc.perform(get("/items/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void findById_WrongItemId_ReturnNotFound() throws Exception {
        given(itemService.findById(999L)).willThrow(NotFoundException.class);

        mockMvc.perform(get("/items/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void findAllByUserId_Normal() throws Exception {
        List<Item> items = List.of(item);
        given(itemService.findAllByUserId(1L)).willReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name"));

    }

    @Test
    void search_Normal() throws Exception {
        List<Item> items = List.of(item);
        given(itemService.search("name")).willReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name"));
    }

    @Test
    void search_EmptySearchText_ReturnEmptyList() throws Exception {

        given(itemService.search("name")).willReturn(new ArrayList<>());

        mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());
    }
}