package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    // Сохраняем запрос на вещь
    @Transactional
    @Override
    public ItemRequestDto saveItemRequest(long userId, ItemRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId)));
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(user, requestDto);
        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.itemRequestToDto(saved);
    }

    // Получаем все запросы на вещи по пользователю
    @Override
    public List<ItemRequestDto> findAllByRequestor(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId));
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        List<Item> items = itemRepository.findAllByRequestIsIn(itemRequests);
        List<ItemRequestDto> ret = ItemRequestMapper.itemRequestToDto(itemRequests, items);
        return ret;
    }

    // Получаем все запросы на вещи за исключением запросов запрашивающего пользователя
    @Override
    public List<ItemRequestDto> findItemRequests(long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId, pageRequest);
        List<Item> items = itemRepository.findAllByRequestIsIn(itemRequests);
        return ItemRequestMapper.itemRequestToDto(itemRequests, items);
    }

    // Получаем данные о запросе
    @Override
    public ItemRequestDto getItemRequest(long userId, long itemRequestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос c id=%d не найдена.", itemRequestId)));
        return ItemRequestMapper.itemRequestToDto(itemRequest);
    }
}
