package org.example.diplom_ishi_new.subject;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TeachingSubjectService {

    private final TeachingSubjectRepository repository;
    private final TeachingGroupRepository groupRepository;

    public TeachingSubjectService(TeachingSubjectRepository repository, TeachingGroupRepository groupRepository) {
        this.repository = repository;
        this.groupRepository = groupRepository;
    }

    public void create(TeachingSubjectForm form) {
        TeachingSubject subject = new TeachingSubject();
        subject.setName(form.getName().trim());
        subject.setDepartment(form.getDepartment().trim());
        subject.setFaculty(form.getFaculty().trim());
        subject.setGroupName(form.getGroupName().trim());
        subject.setStudentCount(form.getStudentCount());
        subject.setSemester(form.getSemester());
        repository.save(subject);
    }

    public List<TeachingSubject> getAll() {
        return repository.findAllByOrderByDepartmentAscNameAsc();
    }

    public List<TeachingSubject> getByDepartment(String department) {
        if (department == null || department.isBlank()) {
            return repository.findAllByOrderByDepartmentAscNameAsc();
        }
        return repository.findByDepartmentIgnoreCaseOrderByNameAsc(department.trim());
    }

    public void createSubject(String name) {
        TeachingSubject subject = new TeachingSubject();
        subject.setName(name.trim());
        subject.setDepartment("Umumiy");
        subject.setFaculty("-");
        subject.setGroupName("-");
        subject.setStudentCount(1);
        subject.setSemester(org.example.diplom_ishi_new.report.Semester.KUZGI);
        repository.save(subject);
    }

    public void updateSubject(Long id, String name) {
        TeachingSubject subject = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fan topilmadi"));
        subject.setName(name.trim());
        repository.save(subject);
    }

    public void deleteSubject(Long id) {
        repository.deleteById(id);
    }

    public void createGroup(String name) {
        TeachingGroup group = new TeachingGroup();
        group.setName(name.trim());
        groupRepository.save(group);
    }

    public void updateGroup(Long id, String name) {
        TeachingGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guruh topilmadi"));
        group.setName(name.trim());
        groupRepository.save(group);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    public List<TeachingGroup> getGroups() {
        return groupRepository.findAllByOrderByNameAsc();
    }
}
