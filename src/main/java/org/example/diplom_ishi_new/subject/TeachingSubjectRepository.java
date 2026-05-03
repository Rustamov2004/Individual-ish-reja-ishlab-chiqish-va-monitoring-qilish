package org.example.diplom_ishi_new.subject;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingSubjectRepository extends JpaRepository<TeachingSubject, Long> {

    List<TeachingSubject> findByDepartmentIgnoreCaseOrderByNameAsc(String department);

    List<TeachingSubject> findAllByOrderByDepartmentAscNameAsc();
}
