package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserRepository userRepository;

    // сохраняем запрос на вещь
    @Transactional
    @Override
    public ItemRequestDto saveItemRequest(Long userId, ItemRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id=%d не найдена.", userId)));
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(user, requestDto);
        ItemRequest saved = repository.save(itemRequest);
        return ItemRequestMapper.itemRequestToDto(saved);
    }
}
