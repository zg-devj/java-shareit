package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(long requestorId);

    @Query("select ri from ItemRequest as ri " +
            "where ri.requestor.id!=?1")
    List<ItemRequest> findItemRequests(long requestorId);
}
