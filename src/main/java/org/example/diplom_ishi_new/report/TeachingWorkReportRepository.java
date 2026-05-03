package org.example.diplom_ishi_new.report;

import java.util.List;
import java.util.Collection;
import org.example.diplom_ishi_new.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachingWorkReportRepository extends JpaRepository<TeachingWorkReport, Long> {

    List<TeachingWorkReport> findByTeacherOrderByCreatedAtDesc(User teacher);

    List<TeachingWorkReport> findAllByOrderByCreatedAtDesc();

    List<TeachingWorkReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<TeachingWorkReport> findByStatusInOrderByCreatedAtDesc(Collection<ReportStatus> statuses);

    void deleteByTeacher(User teacher);
}
