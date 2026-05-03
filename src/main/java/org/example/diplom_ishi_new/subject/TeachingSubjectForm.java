package org.example.diplom_ishi_new.subject;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.diplom_ishi_new.report.Semester;

public class TeachingSubjectForm {

    @NotBlank(message = "Fan nomini kiriting")
    private String name;

    @NotBlank(message = "Kafedrani kiriting")
    private String department;

    @NotBlank(message = "Fakultetni kiriting")
    private String faculty;

    @NotBlank(message = "Guruhni kiriting")
    private String groupName;

    @NotNull(message = "Talabalar sonini kiriting")
    @Min(value = 1, message = "Talabalar soni 1 dan kam bo'lmasin")
    private Integer studentCount;

    @NotNull(message = "Semestrni tanlang")
    private Semester semester;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }
}
