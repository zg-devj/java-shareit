package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    // сохраняем запрос на вещь
    @Transactional
    @Override
    public ItemRequestDto saveItemRequest(Long userId, ItemRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId)));
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(user, requestDto);
        ItemRequest saved = itemRequestRepository.save(itemRequest);
        ItemRequestDto retuned = ItemRequestMapper.itemRequestToDto(saved);
        return retuned;
    }

    @Override
    public List<ItemRequestDto> findAllByRequestor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId)));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);
        List<ItemRequestDto> retunedList = ItemRequestMapper.itemRequestToDto(itemRequests);
        return retunedList;
    }

    @Override
    public List<ItemRequestDto> findItemRequests(int from, int size) {
        int page = from / size;
        System.out.println(page);
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequests();
        List<ItemRequestDto> retunedList = ItemRequestMapper.itemRequestToDto(itemRequests);
        return retunedList;
    }
}
