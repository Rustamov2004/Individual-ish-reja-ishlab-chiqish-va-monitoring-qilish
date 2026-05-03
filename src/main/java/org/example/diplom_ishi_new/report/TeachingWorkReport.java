package org.example.diplom_ishi_new.report;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.diplom_ishi_new.user.User;

@Entity
@Table(name = "teaching_work_reports")
public class TeachingWorkReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    private String academicYear;

    @Column(nullable = false)
    private String workName;

    @Column(nullable = false)
    private String faculty;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private Integer studentCount;

    private Integer maruza;
    private Integer maruzaJami;
    private Integer maruzaAmalda;
    private Integer amaliyMashgulot;
    private Integer amaliyMashgulotJami;
    private Integer amaliyMashgulotAmalda;
    private Integer laboratoriyaIshi;
    private Integer laboratoriyaIshiJami;
    private Integer laboratoriyaIshiAmalda;
    private Integer maslahat;
    private Integer maslahatJami;
    private Integer maslahatAmalda;
    private Integer nazorat;
    private Integer nazoratJami;
    private Integer nazoratAmalda;
    private Integer taqrizlar;
    private Integer taqrizlarJami;
    private Integer taqrizlarAmalda;
    private Integer kursIshi;
    private Integer kursIshiJami;
    private Integer kursIshiAmalda;
    private Integer bitiruvIshi;
    private Integer bitiruvIshiJami;
    private Integer bitiruvIshiAmalda;
    private Integer dakBmiRahbarligi;
    private Integer dakBmiRahbarligiJami;
    private Integer dakBmiRahbarligiAmalda;
    private Integer amaliyot;
    private Integer amaliyotJami;
    private Integer amaliyotAmalda;
    private Integer iti;
    private Integer itiJami;
    private Integer itiAmalda;
    private Integer bmiGaTaqriz;
    private Integer bmiGaTaqrizJami;
    private Integer bmiGaTaqrizAmalda;
    private Integer qaytaTopshirish;
    private Integer qaytaTopshirishJami;
    private Integer qaytaTopshirishAmalda;
    private Integer ratingBall;
    private Integer ratingBallJami;
    private Integer ratingBallAmalda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(length = 2000)
    private String headResponse;

    @Column(length = 2000)
    private String reportResponse;

    private String reportFileName;

    @Column(length = 1000)
    private String reportFilePath;

    @Column(length = 4000)
    private String amaldaFiles;

    private String section;

    @Column(length = 2000)
    private String methodicalBajariladiganIshlar;

    private String methodicalMuddat;

    private String methodicalIshlarHajmi;

    @Column(length = 2000)
    private String methodicalRejadanTashqariIshlar;

    private String methodicalRatingBall;

    @Column(length = 2000)
    private String researchBajariladiganIshlar;

    private String researchMuddat;

    private String researchIjroBelgisi;

    private String researchIshHajmi;

    @Column(length = 2000)
    private String researchRejadanTashqariIshlar;

    private String researchRatingBall;

    @Column(length = 2000)
    private String mentorshipBajariladiganIshlar;

    private String mentorshipMuddat;

    private String mentorshipOtkazishJoyi;

    private String mentorshipIshHajmi;

    @Column(length = 2000)
    private String mentorshipRejadanTashqariIshlar;

    private String mentorshipRatingBall;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastActionAt;

    protected TeachingWorkReport() {
    }

    public Long getId() {
        return id;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getMaruza() {
        return maruza;
    }

    public void setMaruza(Integer maruza) {
        this.maruza = maruza;
    }
    public Integer getMaruzaJami() { return maruzaJami; }
    public void setMaruzaJami(Integer maruzaJami) { this.maruzaJami = maruzaJami; }
    public Integer getMaruzaAmalda() { return maruzaAmalda; }
    public void setMaruzaAmalda(Integer maruzaAmalda) { this.maruzaAmalda = maruzaAmalda; }

    public Integer getAmaliyMashgulot() {
        return amaliyMashgulot;
    }

    public void setAmaliyMashgulot(Integer amaliyMashgulot) {
        this.amaliyMashgulot = amaliyMashgulot;
    }
    public Integer getAmaliyMashgulotJami() { return amaliyMashgulotJami; }
    public void setAmaliyMashgulotJami(Integer amaliyMashgulotJami) { this.amaliyMashgulotJami = amaliyMashgulotJami; }
    public Integer getAmaliyMashgulotAmalda() { return amaliyMashgulotAmalda; }
    public void setAmaliyMashgulotAmalda(Integer amaliyMashgulotAmalda) { this.amaliyMashgulotAmalda = amaliyMashgulotAmalda; }

    public Integer getLaboratoriyaIshi() {
        return laboratoriyaIshi;
    }

    public void setLaboratoriyaIshi(Integer laboratoriyaIshi) {
        this.laboratoriyaIshi = laboratoriyaIshi;
    }
    public Integer getLaboratoriyaIshiJami() { return laboratoriyaIshiJami; }
    public void setLaboratoriyaIshiJami(Integer laboratoriyaIshiJami) { this.laboratoriyaIshiJami = laboratoriyaIshiJami; }
    public Integer getLaboratoriyaIshiAmalda() { return laboratoriyaIshiAmalda; }
    public void setLaboratoriyaIshiAmalda(Integer laboratoriyaIshiAmalda) { this.laboratoriyaIshiAmalda = laboratoriyaIshiAmalda; }

    public Integer getMaslahat() {
        return maslahat;
    }

    public void setMaslahat(Integer maslahat) {
        this.maslahat = maslahat;
    }
    public Integer getMaslahatJami() { return maslahatJami; }
    public void setMaslahatJami(Integer maslahatJami) { this.maslahatJami = maslahatJami; }
    public Integer getMaslahatAmalda() { return maslahatAmalda; }
    public void setMaslahatAmalda(Integer maslahatAmalda) { this.maslahatAmalda = maslahatAmalda; }

    public Integer getNazorat() {
        return nazorat;
    }

    public void setNazorat(Integer nazorat) {
        this.nazorat = nazorat;
    }
    public Integer getNazoratJami() { return nazoratJami; }
    public void setNazoratJami(Integer nazoratJami) { this.nazoratJami = nazoratJami; }
    public Integer getNazoratAmalda() { return nazoratAmalda; }
    public void setNazoratAmalda(Integer nazoratAmalda) { this.nazoratAmalda = nazoratAmalda; }

    public Integer getTaqrizlar() {
        return taqrizlar;
    }

    public void setTaqrizlar(Integer taqrizlar) {
        this.taqrizlar = taqrizlar;
    }
    public Integer getTaqrizlarJami() { return taqrizlarJami; }
    public void setTaqrizlarJami(Integer taqrizlarJami) { this.taqrizlarJami = taqrizlarJami; }
    public Integer getTaqrizlarAmalda() { return taqrizlarAmalda; }
    public void setTaqrizlarAmalda(Integer taqrizlarAmalda) { this.taqrizlarAmalda = taqrizlarAmalda; }

    public Integer getKursIshi() {
        return kursIshi;
    }

    public void setKursIshi(Integer kursIshi) {
        this.kursIshi = kursIshi;
    }
    public Integer getKursIshiJami() { return kursIshiJami; }
    public void setKursIshiJami(Integer kursIshiJami) { this.kursIshiJami = kursIshiJami; }
    public Integer getKursIshiAmalda() { return kursIshiAmalda; }
    public void setKursIshiAmalda(Integer kursIshiAmalda) { this.kursIshiAmalda = kursIshiAmalda; }

    public Integer getBitiruvIshi() {
        return bitiruvIshi;
    }

    public void setBitiruvIshi(Integer bitiruvIshi) {
        this.bitiruvIshi = bitiruvIshi;
    }
    public Integer getBitiruvIshiJami() { return bitiruvIshiJami; }
    public void setBitiruvIshiJami(Integer bitiruvIshiJami) { this.bitiruvIshiJami = bitiruvIshiJami; }
    public Integer getBitiruvIshiAmalda() { return bitiruvIshiAmalda; }
    public void setBitiruvIshiAmalda(Integer bitiruvIshiAmalda) { this.bitiruvIshiAmalda = bitiruvIshiAmalda; }

    public Integer getDakBmiRahbarligi() {
        return dakBmiRahbarligi;
    }

    public void setDakBmiRahbarligi(Integer dakBmiRahbarligi) {
        this.dakBmiRahbarligi = dakBmiRahbarligi;
    }
    public Integer getDakBmiRahbarligiJami() { return dakBmiRahbarligiJami; }
    public void setDakBmiRahbarligiJami(Integer dakBmiRahbarligiJami) { this.dakBmiRahbarligiJami = dakBmiRahbarligiJami; }
    public Integer getDakBmiRahbarligiAmalda() { return dakBmiRahbarligiAmalda; }
    public void setDakBmiRahbarligiAmalda(Integer dakBmiRahbarligiAmalda) { this.dakBmiRahbarligiAmalda = dakBmiRahbarligiAmalda; }

    public Integer getAmaliyot() {
        return amaliyot;
    }

    public void setAmaliyot(Integer amaliyot) {
        this.amaliyot = amaliyot;
    }
    public Integer getAmaliyotJami() { return amaliyotJami; }
    public void setAmaliyotJami(Integer amaliyotJami) { this.amaliyotJami = amaliyotJami; }
    public Integer getAmaliyotAmalda() { return amaliyotAmalda; }
    public void setAmaliyotAmalda(Integer amaliyotAmalda) { this.amaliyotAmalda = amaliyotAmalda; }

    public Integer getIti() {
        return iti;
    }

    public void setIti(Integer iti) {
        this.iti = iti;
    }
    public Integer getItiJami() { return itiJami; }
    public void setItiJami(Integer itiJami) { this.itiJami = itiJami; }
    public Integer getItiAmalda() { return itiAmalda; }
    public void setItiAmalda(Integer itiAmalda) { this.itiAmalda = itiAmalda; }

    public Integer getBmiGaTaqriz() {
        return bmiGaTaqriz;
    }

    public void setBmiGaTaqriz(Integer bmiGaTaqriz) {
        this.bmiGaTaqriz = bmiGaTaqriz;
    }
    public Integer getBmiGaTaqrizJami() { return bmiGaTaqrizJami; }
    public void setBmiGaTaqrizJami(Integer bmiGaTaqrizJami) { this.bmiGaTaqrizJami = bmiGaTaqrizJami; }
    public Integer getBmiGaTaqrizAmalda() { return bmiGaTaqrizAmalda; }
    public void setBmiGaTaqrizAmalda(Integer bmiGaTaqrizAmalda) { this.bmiGaTaqrizAmalda = bmiGaTaqrizAmalda; }

    public Integer getQaytaTopshirish() {
        return qaytaTopshirish;
    }

    public void setQaytaTopshirish(Integer qaytaTopshirish) {
        this.qaytaTopshirish = qaytaTopshirish;
    }
    public Integer getQaytaTopshirishJami() { return qaytaTopshirishJami; }
    public void setQaytaTopshirishJami(Integer qaytaTopshirishJami) { this.qaytaTopshirishJami = qaytaTopshirishJami; }
    public Integer getQaytaTopshirishAmalda() { return qaytaTopshirishAmalda; }
    public void setQaytaTopshirishAmalda(Integer qaytaTopshirishAmalda) { this.qaytaTopshirishAmalda = qaytaTopshirishAmalda; }

    public Integer getRatingBall() {
        return ratingBall;
    }

    public void setRatingBall(Integer ratingBall) {
        this.ratingBall = ratingBall;
    }
    public Integer getRatingBallJami() { return ratingBallJami; }
    public void setRatingBallJami(Integer ratingBallJami) { this.ratingBallJami = ratingBallJami; }
    public Integer getRatingBallAmalda() { return ratingBallAmalda; }
    public void setRatingBallAmalda(Integer ratingBallAmalda) { this.ratingBallAmalda = ratingBallAmalda; }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getHeadResponse() {
        return headResponse;
    }

    public void setHeadResponse(String headResponse) {
        this.headResponse = headResponse;
    }

    public String getReportResponse() {
        return reportResponse;
    }

    public void setReportResponse(String reportResponse) {
        this.reportResponse = reportResponse;
    }

    public String getReportFileName() {
        return reportFileName;
    }

    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    public String getAmaldaFiles() {
        return amaldaFiles;
    }

    public void setAmaldaFiles(String amaldaFiles) {
        this.amaldaFiles = amaldaFiles;
    }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getMethodicalBajariladiganIshlar() { return methodicalBajariladiganIshlar; }
    public void setMethodicalBajariladiganIshlar(String methodicalBajariladiganIshlar) { this.methodicalBajariladiganIshlar = methodicalBajariladiganIshlar; }
    public String getMethodicalMuddat() { return methodicalMuddat; }
    public void setMethodicalMuddat(String methodicalMuddat) { this.methodicalMuddat = methodicalMuddat; }
    public String getMethodicalIshlarHajmi() { return methodicalIshlarHajmi; }
    public void setMethodicalIshlarHajmi(String methodicalIshlarHajmi) { this.methodicalIshlarHajmi = methodicalIshlarHajmi; }
    public String getMethodicalRejadanTashqariIshlar() { return methodicalRejadanTashqariIshlar; }
    public void setMethodicalRejadanTashqariIshlar(String methodicalRejadanTashqariIshlar) { this.methodicalRejadanTashqariIshlar = methodicalRejadanTashqariIshlar; }
    public String getMethodicalRatingBall() { return methodicalRatingBall; }
    public void setMethodicalRatingBall(String methodicalRatingBall) { this.methodicalRatingBall = methodicalRatingBall; }
    public String getResearchBajariladiganIshlar() { return researchBajariladiganIshlar; }
    public void setResearchBajariladiganIshlar(String researchBajariladiganIshlar) { this.researchBajariladiganIshlar = researchBajariladiganIshlar; }
    public String getResearchMuddat() { return researchMuddat; }
    public void setResearchMuddat(String researchMuddat) { this.researchMuddat = researchMuddat; }
    public String getResearchIjroBelgisi() { return researchIjroBelgisi; }
    public void setResearchIjroBelgisi(String researchIjroBelgisi) { this.researchIjroBelgisi = researchIjroBelgisi; }
    public String getResearchIshHajmi() { return researchIshHajmi; }
    public void setResearchIshHajmi(String researchIshHajmi) { this.researchIshHajmi = researchIshHajmi; }
    public String getResearchRejadanTashqariIshlar() { return researchRejadanTashqariIshlar; }
    public void setResearchRejadanTashqariIshlar(String researchRejadanTashqariIshlar) { this.researchRejadanTashqariIshlar = researchRejadanTashqariIshlar; }
    public String getResearchRatingBall() { return researchRatingBall; }
    public void setResearchRatingBall(String researchRatingBall) { this.researchRatingBall = researchRatingBall; }
    public String getMentorshipBajariladiganIshlar() { return mentorshipBajariladiganIshlar; }
    public void setMentorshipBajariladiganIshlar(String mentorshipBajariladiganIshlar) { this.mentorshipBajariladiganIshlar = mentorshipBajariladiganIshlar; }
    public String getMentorshipMuddat() { return mentorshipMuddat; }
    public void setMentorshipMuddat(String mentorshipMuddat) { this.mentorshipMuddat = mentorshipMuddat; }
    public String getMentorshipOtkazishJoyi() { return mentorshipOtkazishJoyi; }
    public void setMentorshipOtkazishJoyi(String mentorshipOtkazishJoyi) { this.mentorshipOtkazishJoyi = mentorshipOtkazishJoyi; }
    public String getMentorshipIshHajmi() { return mentorshipIshHajmi; }
    public void setMentorshipIshHajmi(String mentorshipIshHajmi) { this.mentorshipIshHajmi = mentorshipIshHajmi; }
    public String getMentorshipRejadanTashqariIshlar() { return mentorshipRejadanTashqariIshlar; }
    public void setMentorshipRejadanTashqariIshlar(String mentorshipRejadanTashqariIshlar) { this.mentorshipRejadanTashqariIshlar = mentorshipRejadanTashqariIshlar; }
    public String getMentorshipRatingBall() { return mentorshipRatingBall; }
    public void setMentorshipRatingBall(String mentorshipRatingBall) { this.mentorshipRatingBall = mentorshipRatingBall; }

    public boolean isMethodicalPenalty() {
        String deadline = switch (section == null ? "" : section) {
            case "Ilmiy-tadqiqot ishlari" -> researchMuddat;
            case "Ustoz-shogird ishlari" -> mentorshipMuddat;
            default -> methodicalMuddat;
        };
        if ((!"Ilmiy-uslubiy ishlar".equals(section) && !"Ilmiy-tadqiqot ishlari".equals(section) && !"Ustoz-shogird ishlari".equals(section))
                || deadline == null
                || deadline.isBlank()
                || headResponse == null
                || !headResponse.toLowerCase().contains("tasdiq")
                || (amaldaFiles != null && !amaldaFiles.isBlank())
                || (reportResponse != null && !reportResponse.isBlank())) {
            return false;
        }

        try {
            String[] rows = deadline.split("\\Q|||ROW|||\\E");
            LocalDate latestDeadline = null;
            for (String row : rows) {
                if (row == null || row.isBlank()) {
                    continue;
                }
                String[] parts = row.trim().split("\\s+-\\s+");
                String candidate = parts.length >= 2 ? parts[parts.length - 1].trim() : row.trim();
                LocalDate parsed = LocalDate.parse(candidate);
                if (latestDeadline == null || parsed.isAfter(latestDeadline)) {
                    latestDeadline = parsed;
                }
            }
            return latestDeadline != null && latestDeadline.isBefore(LocalDate.now());
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastActionAt() {
        return lastActionAt;
    }

    public void setLastActionAt(LocalDateTime lastActionAt) {
        this.lastActionAt = lastActionAt;
    }
}
