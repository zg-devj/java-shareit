package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Все вещи по ids запросов
    List<Item> findAllByRequestIsIn(Collection<ItemRequest> request);

    // Список вещей пользователя
    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);

    // Найти вещь по ID и не принадлежащую владельцу
    Optional<Item> findByIdAndOwnerNot(Long itemId, User user);

    // Поиск вещий по названию или описанию
    @Query("select i from Item i " +
            "where i.available=true and length(?1)>0 and " +
            "(upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text, Pageable pageable);
}

