package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //    User save(User user);
//
//    Optional<User> findById(Long userId);
//
    Optional<User> findByEmail(String email);

    //    List<User> findAll();
//
//    User update(User user);
//
//    void delete(Long userId);
//
//    boolean existsById(Long userId);
//
    // Если найден email не принадлежащий пользователю, то обновить данные нельзя
    @Query("select case when count(u) >0 then true else false end " +
            "from User as u " +
            "where u.id!=?1 and lower(u.email) like lower(?2)")
    boolean canNotUpdate(Long userId, String email);
}
