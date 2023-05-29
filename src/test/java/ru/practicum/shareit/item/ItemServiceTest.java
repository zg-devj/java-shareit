package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private ItemDto itemDto;
    private Item item;
    private ItemDto itemDtoMustBe;
    private ItemBookingDto itemBookingDtoMustBe;
    private Item item2;
//
//    private User user2;
//    private Item item3;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L).name("user").email("user@example.com")
                .build();
        itemDto = ItemDto.builder()
                .name("hammer").description("good steel hammer").available(true)
                .build();
        item = Item.builder()
                .id(1L)
                .name("hammer").description("good steel hammer").available(true)
                .owner(user)
                .build();
        itemDtoMustBe = ItemDto.builder()
                .id(1L)
                .name("hammer").description("good steel hammer").available(true)
                .requestId(null)
                .build();
        itemBookingDtoMustBe = ItemBookingDto.builder()
                .id(1L)
                .name("hammer").description("good steel hammer").available(true)
                .comments(new ArrayList<>())
                .lastBooking(null)
                .nextBooking(null)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("drill").description("best drill").available(false)
                .owner(user)
                .build();
//
//        user2 = User.builder()
//                .id(2L).name("user2").email("user2@example.com")
//                .build();
//
//        item3 = Item.builder()
//                .id(2L)
//                .name("trap").description("long best trap").available(true)
//                .owner(user2)
//                .build();
    }

    @Test
    void saveItem_Normal_WithoutRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto added = itemService.saveItem(1L, itemDto);

        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(itemDtoMustBe);
        Assertions.assertThat(added.getRequestId()).isNull();

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository);
    }

    @Test
    void saveItem_Normal_WithRequestId() {
        itemDto.setRequestId(1L);
        itemDtoMustBe.setRequestId(1L);

        User requestor = User.builder()
                .id(2L).name("requestor").email("requestor@example.com")
                .build();


        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .items(List.of(item))
                .requestor(requestor)
                .description("good hummer")
                .created(LocalDateTime.now())
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        item.setRequest(itemRequest);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto added = itemService.saveItem(1L, itemDto);


        Assertions.assertThat(added)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(itemDtoMustBe);
        Assertions.assertThat(added.getRequestId()).isEqualTo(itemRequest.getId());

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verifyNoMoreInteractions(userRepository, itemRepository, itemRequestRepository);
    }

    @Test
    void saveItem_NotExistUser_ReturnNotFoundException() {
        when(userRepository.findById(999L)).thenThrow(NotFoundException.class);

        Throwable thrown = Assertions.catchException(() -> itemService.saveItem(999L, itemDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);

        Mockito.verify(userRepository, Mockito.times(1)).findById(999L);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(itemRepository, itemRequestRepository);
    }

    @Test
    void saveItem_NotExistItemRequest_ReturnNotFoundException() {
        itemDto.setRequestId(999L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(999L))
                .thenThrow(NotFoundException.class);

        Throwable thrown = Assertions.catchException(() -> itemService.saveItem(1L, itemDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);

        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(999L);
        Mockito.verifyNoMoreInteractions(userRepository, itemRequestRepository);
        Mockito.verifyNoInteractions(itemRepository);
    }

    @Test
    void updateItem_Normal() {
        ItemDto dataToUpdate = itemDto.toBuilder()
                .id(1L)
                .name("hammer updated")
                .description("good steel hammer updated")
                .available(false)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updated = itemService.updateItem(1L, dataToUpdate);

        Assertions.assertThat(updated.getName()).isEqualTo("hammer updated");
        Assertions.assertThat(updated.getDescription()).isEqualTo("good steel hammer updated");
        Assertions.assertThat(updated.getAvailable()).isEqualTo(false);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void updateItem_ItemNotFound_ReturnNotFoundException() {
        ItemDto dataToUpdate = itemDto.toBuilder()
                .id(999L)
                .name("hammer updated")
                .description("good steel hammer updated")
                .available(false)
                .build();

        when(itemRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        Throwable thrown = Assertions.catchException(() -> itemService.updateItem(1L, dataToUpdate));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(999L);
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    //
    @Test
    void updateItem_WrongUser_ReturnForbiddenException() {
        ItemDto dataToUpdate = itemDto.toBuilder()
                .id(1L)
                .name("hammer updated")
                .description("good steel hammer updated")
                .available(false)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Throwable thrown = Assertions.catchException(() -> itemService.updateItem(99L, dataToUpdate));

        Assertions.assertThat(thrown)
                .isInstanceOf(ForbiddenException.class);


        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verifyNoMoreInteractions(itemRepository);
    }


    @Test
    void findItemById_Normal() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentsByItemIdOrderByCreatedAsc(1L))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.getLastBooking(anyLong(), anyLong(), any(BookingStatus.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.getNextBooking(anyLong(), anyLong(), any(BookingStatus.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new ArrayList<>());

        ItemBookingDto actual = itemService.findById(1L, 1L);

        Assertions.assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(itemBookingDtoMustBe);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findCommentsByItemIdOrderByCreatedAsc(1l);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getLastBooking(anyLong(), anyLong(), any(BookingStatus.class),
                        any(LocalDateTime.class), any(PageRequest.class));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getNextBooking(anyLong(), anyLong(), any(BookingStatus.class),
                        any(LocalDateTime.class), any(PageRequest.class));
        Mockito.verifyNoMoreInteractions(itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void findItemById_Normal_withBooking() {
        BookingShort last = new BookingShort(3L, 5L);
        BookingShort next = new BookingShort(4L, 6L);

        itemBookingDtoMustBe.setNextBooking(next);
        itemBookingDtoMustBe.setLastBooking(last);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentsByItemIdOrderByCreatedAsc(1L))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.getLastBooking(anyLong(), anyLong(), any(BookingStatus.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(last));
        when(bookingRepository.getNextBooking(anyLong(), anyLong(), any(BookingStatus.class),
                any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(next));

        ItemBookingDto actual = itemService.findById(1L, 1L);

        Assertions.assertThat(actual)
                .isNotNull()
                .usingRecursiveComparison().isEqualTo(itemBookingDtoMustBe);

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(commentRepository, Mockito.times(1)).findCommentsByItemIdOrderByCreatedAsc(1l);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getLastBooking(anyLong(), anyLong(), any(BookingStatus.class),
                        any(LocalDateTime.class), any(PageRequest.class));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getNextBooking(anyLong(), anyLong(), any(BookingStatus.class),
                        any(LocalDateTime.class), any(PageRequest.class));
        Mockito.verifyNoMoreInteractions(itemRepository, commentRepository, bookingRepository);
    }

    @Test
    void findById_WrongId_ReturnNotFoundException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        Throwable thrown = Assertions.catchException(() -> itemService.findById(999L, 1L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(String.format("Вещь c id=%d не найдена.", 999L));

        Mockito.verify(itemRepository, Mockito.times(1)).findById(999L);
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void findAllItemsByUserId() {
        List<Item> items = List.of(item, item2);

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any(PageRequest.class)))
                .thenReturn(items);

        List<ItemBookingDto> result = itemService.findAllByUserId(1L, 1, 20);

        Assertions.assertThat(result)
                .hasSize(2);

        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByOwnerIdOrderByIdAsc(anyLong(), any(PageRequest.class));
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void search_Normal() {
        List<Item> items = List.of(item, item2);
        when(itemRepository.search(any(), any(PageRequest.class)))
                .thenReturn(items);

        List<ItemDto> result = itemService.search("best", 0, 20);

        Assertions.assertThat(result)
                .hasSize(2);

        Mockito.verify(itemRepository, Mockito.times(1))
                .search(any(), any(PageRequest.class));
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void search_WithEmptyText_ReturnEmptyList() {
        List<Item> itemsEmpty = new ArrayList<>();

        List<ItemDto> result = itemService.search("", 0, 20);

        Assertions.assertThat(result)
                .hasSize(0).isEqualTo(new ArrayList<ItemDto>());

        Mockito.verifyNoInteractions(itemRepository);
    }

    @Test
    void addComment_WithEmptyCommentTest_ReturnBadRequestException() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("")
                .build();

        Throwable thrown = Assertions.catchException(() -> itemService
                .addComment(1L, 1L, commentNewDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Текст комментария не должен быть пустым.");

        Mockito.verifyNoInteractions(bookingRepository, commentRepository);
    }

    @Test
    void addComment_WhenUserDoNotTakeItem_ReturnBadRequestException() {
        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("New comment")
                .build();

        when(bookingRepository.findBookingForComment(anyLong(), anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Throwable thrown = Assertions.catchException(() -> itemService
                .addComment(1L, 1L, commentNewDto));

        Assertions.assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Вы не можете оставить комментарий к бронированию.");

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingForComment(anyLong(), anyLong(), any(LocalDateTime.class),
                        any(BookingStatus.class), any(PageRequest.class));
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
    }

    @Test
    void addComment_Normal() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder()
                .id(1L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(user)
                .build();

        CommentNewDto commentNewDto = CommentNewDto.builder()
                .text("New comment")
                .build();

        Comment comment = Comment.builder()
                .item(item)
                .created(now)
                .text("New comment")
                .author(user)
                .build();

        CommentDto commentDtoMustBe = CommentMapper.commentToDto(comment);

        Page<Booking> page = new PageImpl<>(List.of(booking));

        when(bookingRepository.findBookingForComment(anyLong(), anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(page);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 1L, commentNewDto);

        Assertions.assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(commentDtoMustBe);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findBookingForComment(anyLong(), anyLong(), any(LocalDateTime.class),
                        any(BookingStatus.class), any(PageRequest.class));
        Mockito.verify(commentRepository, Mockito.times(1))
                .save(any(Comment.class));
        Mockito.verifyNoMoreInteractions(bookingRepository, commentRepository);
    }
}