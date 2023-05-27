package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ItemRequestController.class})
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    private LocalDateTime now = LocalDateTime
            .of(2023, 5, 26, 10, 0, 0, 0);

    ItemRequestDto requestDto;
    ItemRequestDto responseDto;
    ItemRequestDto responseDto2;

    @BeforeEach
    void setUp() {
        requestDto = ItemRequestDto.builder()
                .description("want hammer")
                .build();

        responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("want hammer")
                .created(now.format(Utils.dtFormatter))
                .build();

        responseDto2 = ItemRequestDto.builder()
                .id(2L)
                .description("want drill")
                .created(now.format(Utils.dtFormatter))
                .build();
    }

    @Test
    void create_ItemRequest_Normal_returnCode201() throws Exception {
        when(itemRequestService.saveItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated())));
    }

    @Test
    void create_ItemRequest_WrongUserId_returnCode404() throws Exception {
        when(itemRequestService.saveItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 99L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ItemRequest_UserNull_returnCode400() throws Exception {
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllByRequestor_Normal_returnCode200() throws Exception {

        when(itemRequestService.findAllByRequestor(anyLong()))
                .thenReturn(List.of(responseDto, responseDto2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(responseDto, responseDto2))));
    }

    @Test
    void findAllByRequestor_WrongId_returnCode404() throws Exception {
        when(itemRequestService.findAllByRequestor(anyLong()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 99L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByRequestor_UserNull_returnCode400() throws Exception {
        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}