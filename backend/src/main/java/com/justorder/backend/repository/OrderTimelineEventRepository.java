package com.justorder.backend.repository;

import com.justorder.backend.model.OrderTimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderTimelineEventRepository extends JpaRepository<OrderTimelineEvent, Long> {
    List<OrderTimelineEvent> findByOrderIdOrderByTimestampDesc(Long orderId);
}
