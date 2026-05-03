package org.example.diplom_ishi_new.subject;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingGroupRepository extends JpaRepository<TeachingGroup, Long> {

    List<TeachingGroup> findAllByOrderByNameAsc();
}
