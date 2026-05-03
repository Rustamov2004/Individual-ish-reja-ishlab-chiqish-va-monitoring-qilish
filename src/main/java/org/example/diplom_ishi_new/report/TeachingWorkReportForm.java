package org.example.diplom_ishi_new.report;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class TeachingWorkReportForm {

    @NotNull(message = "Semestrni tanlang")
    private Semester semester;

    @NotBlank(message = "O'quv yilini kiriting")
    private String academicYear;

    @NotBlank(message = "O'quv ishlari nomini kiriting")
    private String workName;

    @NotBlank(message = "Fakultetni kiriting")
    private String faculty;

    @NotBlank(message = "Guruhni kiriting")
    private String groupName;

    @NotNull(message = "Talabalar sonini kiriting")
    @Min(value = 1, message = "Talabalar soni 1 dan kam bo'lmasin")
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
    private MultipartFile maruzaAmaldaFile;
    private MultipartFile amaliyMashgulotAmaldaFile;
    private MultipartFile laboratoriyaIshiAmaldaFile;
    private MultipartFile maslahatAmaldaFile;
    private MultipartFile nazoratAmaldaFile;
    private MultipartFile taqrizlarAmaldaFile;
    private MultipartFile kursIshiAmaldaFile;
    private MultipartFile bitiruvIshiAmaldaFile;
    private MultipartFile dakBmiRahbarligiAmaldaFile;
    private MultipartFile amaliyotAmaldaFile;
    private MultipartFile itiAmaldaFile;
    private MultipartFile bmiGaTaqrizAmaldaFile;
    private MultipartFile qaytaTopshirishAmaldaFile;
    private MultipartFile ratingBallAmaldaFile;
    private String teachingRowsPayload;

    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public String getWorkName() { return workName; }
    public void setWorkName(String workName) { this.workName = workName; }
    public String getFaculty() { return faculty; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    public Integer getMaruza() { return maruza; }
    public void setMaruza(Integer maruza) { this.maruza = maruza; }
    public Integer getMaruzaJami() { return maruzaJami; }
    public void setMaruzaJami(Integer maruzaJami) { this.maruzaJami = maruzaJami; }
    public Integer getMaruzaAmalda() { return maruzaAmalda; }
    public void setMaruzaAmalda(Integer maruzaAmalda) { this.maruzaAmalda = maruzaAmalda; }
    public Integer getAmaliyMashgulot() { return amaliyMashgulot; }
    public void setAmaliyMashgulot(Integer amaliyMashgulot) { this.amaliyMashgulot = amaliyMashgulot; }
    public Integer getAmaliyMashgulotJami() { return amaliyMashgulotJami; }
    public void setAmaliyMashgulotJami(Integer amaliyMashgulotJami) { this.amaliyMashgulotJami = amaliyMashgulotJami; }
    public Integer getAmaliyMashgulotAmalda() { return amaliyMashgulotAmalda; }
    public void setAmaliyMashgulotAmalda(Integer amaliyMashgulotAmalda) { this.amaliyMashgulotAmalda = amaliyMashgulotAmalda; }
    public Integer getLaboratoriyaIshi() { return laboratoriyaIshi; }
    public void setLaboratoriyaIshi(Integer laboratoriyaIshi) { this.laboratoriyaIshi = laboratoriyaIshi; }
    public Integer getLaboratoriyaIshiJami() { return laboratoriyaIshiJami; }
    public void setLaboratoriyaIshiJami(Integer laboratoriyaIshiJami) { this.laboratoriyaIshiJami = laboratoriyaIshiJami; }
    public Integer getLaboratoriyaIshiAmalda() { return laboratoriyaIshiAmalda; }
    public void setLaboratoriyaIshiAmalda(Integer laboratoriyaIshiAmalda) { this.laboratoriyaIshiAmalda = laboratoriyaIshiAmalda; }
    public Integer getMaslahat() { return maslahat; }
    public void setMaslahat(Integer maslahat) { this.maslahat = maslahat; }
    public Integer getMaslahatJami() { return maslahatJami; }
    public void setMaslahatJami(Integer maslahatJami) { this.maslahatJami = maslahatJami; }
    public Integer getMaslahatAmalda() { return maslahatAmalda; }
    public void setMaslahatAmalda(Integer maslahatAmalda) { this.maslahatAmalda = maslahatAmalda; }
    public Integer getNazorat() { return nazorat; }
    public void setNazorat(Integer nazorat) { this.nazorat = nazorat; }
    public Integer getNazoratJami() { return nazoratJami; }
    public void setNazoratJami(Integer nazoratJami) { this.nazoratJami = nazoratJami; }
    public Integer getNazoratAmalda() { return nazoratAmalda; }
    public void setNazoratAmalda(Integer nazoratAmalda) { this.nazoratAmalda = nazoratAmalda; }
    public Integer getTaqrizlar() { return taqrizlar; }
    public void setTaqrizlar(Integer taqrizlar) { this.taqrizlar = taqrizlar; }
    public Integer getTaqrizlarJami() { return taqrizlarJami; }
    public void setTaqrizlarJami(Integer taqrizlarJami) { this.taqrizlarJami = taqrizlarJami; }
    public Integer getTaqrizlarAmalda() { return taqrizlarAmalda; }
    public void setTaqrizlarAmalda(Integer taqrizlarAmalda) { this.taqrizlarAmalda = taqrizlarAmalda; }
    public Integer getKursIshi() { return kursIshi; }
    public void setKursIshi(Integer kursIshi) { this.kursIshi = kursIshi; }
    public Integer getKursIshiJami() { return kursIshiJami; }
    public void setKursIshiJami(Integer kursIshiJami) { this.kursIshiJami = kursIshiJami; }
    public Integer getKursIshiAmalda() { return kursIshiAmalda; }
    public void setKursIshiAmalda(Integer kursIshiAmalda) { this.kursIshiAmalda = kursIshiAmalda; }
    public Integer getBitiruvIshi() { return bitiruvIshi; }
    public void setBitiruvIshi(Integer bitiruvIshi) { this.bitiruvIshi = bitiruvIshi; }
    public Integer getBitiruvIshiJami() { return bitiruvIshiJami; }
    public void setBitiruvIshiJami(Integer bitiruvIshiJami) { this.bitiruvIshiJami = bitiruvIshiJami; }
    public Integer getBitiruvIshiAmalda() { return bitiruvIshiAmalda; }
    public void setBitiruvIshiAmalda(Integer bitiruvIshiAmalda) { this.bitiruvIshiAmalda = bitiruvIshiAmalda; }
    public Integer getDakBmiRahbarligi() { return dakBmiRahbarligi; }
    public void setDakBmiRahbarligi(Integer dakBmiRahbarligi) { this.dakBmiRahbarligi = dakBmiRahbarligi; }
    public Integer getDakBmiRahbarligiJami() { return dakBmiRahbarligiJami; }
    public void setDakBmiRahbarligiJami(Integer dakBmiRahbarligiJami) { this.dakBmiRahbarligiJami = dakBmiRahbarligiJami; }
    public Integer getDakBmiRahbarligiAmalda() { return dakBmiRahbarligiAmalda; }
    public void setDakBmiRahbarligiAmalda(Integer dakBmiRahbarligiAmalda) { this.dakBmiRahbarligiAmalda = dakBmiRahbarligiAmalda; }
    public Integer getAmaliyot() { return amaliyot; }
    public void setAmaliyot(Integer amaliyot) { this.amaliyot = amaliyot; }
    public Integer getAmaliyotJami() { return amaliyotJami; }
    public void setAmaliyotJami(Integer amaliyotJami) { this.amaliyotJami = amaliyotJami; }
    public Integer getAmaliyotAmalda() { return amaliyotAmalda; }
    public void setAmaliyotAmalda(Integer amaliyotAmalda) { this.amaliyotAmalda = amaliyotAmalda; }
    public Integer getIti() { return iti; }
    public void setIti(Integer iti) { this.iti = iti; }
    public Integer getItiJami() { return itiJami; }
    public void setItiJami(Integer itiJami) { this.itiJami = itiJami; }
    public Integer getItiAmalda() { return itiAmalda; }
    public void setItiAmalda(Integer itiAmalda) { this.itiAmalda = itiAmalda; }
    public Integer getBmiGaTaqriz() { return bmiGaTaqriz; }
    public void setBmiGaTaqriz(Integer bmiGaTaqriz) { this.bmiGaTaqriz = bmiGaTaqriz; }
    public Integer getBmiGaTaqrizJami() { return bmiGaTaqrizJami; }
    public void setBmiGaTaqrizJami(Integer bmiGaTaqrizJami) { this.bmiGaTaqrizJami = bmiGaTaqrizJami; }
    public Integer getBmiGaTaqrizAmalda() { return bmiGaTaqrizAmalda; }
    public void setBmiGaTaqrizAmalda(Integer bmiGaTaqrizAmalda) { this.bmiGaTaqrizAmalda = bmiGaTaqrizAmalda; }
    public Integer getQaytaTopshirish() { return qaytaTopshirish; }
    public void setQaytaTopshirish(Integer qaytaTopshirish) { this.qaytaTopshirish = qaytaTopshirish; }
    public Integer getQaytaTopshirishJami() { return qaytaTopshirishJami; }
    public void setQaytaTopshirishJami(Integer qaytaTopshirishJami) { this.qaytaTopshirishJami = qaytaTopshirishJami; }
    public Integer getQaytaTopshirishAmalda() { return qaytaTopshirishAmalda; }
    public void setQaytaTopshirishAmalda(Integer qaytaTopshirishAmalda) { this.qaytaTopshirishAmalda = qaytaTopshirishAmalda; }
    public Integer getRatingBall() { return ratingBall; }
    public void setRatingBall(Integer ratingBall) { this.ratingBall = ratingBall; }
    public Integer getRatingBallJami() { return ratingBallJami; }
    public void setRatingBallJami(Integer ratingBallJami) { this.ratingBallJami = ratingBallJami; }
    public Integer getRatingBallAmalda() { return ratingBallAmalda; }
    public void setRatingBallAmalda(Integer ratingBallAmalda) { this.ratingBallAmalda = ratingBallAmalda; }
    public MultipartFile getMaruzaAmaldaFile() { return maruzaAmaldaFile; }
    public void setMaruzaAmaldaFile(MultipartFile maruzaAmaldaFile) { this.maruzaAmaldaFile = maruzaAmaldaFile; }
    public MultipartFile getAmaliyMashgulotAmaldaFile() { return amaliyMashgulotAmaldaFile; }
    public void setAmaliyMashgulotAmaldaFile(MultipartFile amaliyMashgulotAmaldaFile) { this.amaliyMashgulotAmaldaFile = amaliyMashgulotAmaldaFile; }
    public MultipartFile getLaboratoriyaIshiAmaldaFile() { return laboratoriyaIshiAmaldaFile; }
    public void setLaboratoriyaIshiAmaldaFile(MultipartFile laboratoriyaIshiAmaldaFile) { this.laboratoriyaIshiAmaldaFile = laboratoriyaIshiAmaldaFile; }
    public MultipartFile getMaslahatAmaldaFile() { return maslahatAmaldaFile; }
    public void setMaslahatAmaldaFile(MultipartFile maslahatAmaldaFile) { this.maslahatAmaldaFile = maslahatAmaldaFile; }
    public MultipartFile getNazoratAmaldaFile() { return nazoratAmaldaFile; }
    public void setNazoratAmaldaFile(MultipartFile nazoratAmaldaFile) { this.nazoratAmaldaFile = nazoratAmaldaFile; }
    public MultipartFile getTaqrizlarAmaldaFile() { return taqrizlarAmaldaFile; }
    public void setTaqrizlarAmaldaFile(MultipartFile taqrizlarAmaldaFile) { this.taqrizlarAmaldaFile = taqrizlarAmaldaFile; }
    public MultipartFile getKursIshiAmaldaFile() { return kursIshiAmaldaFile; }
    public void setKursIshiAmaldaFile(MultipartFile kursIshiAmaldaFile) { this.kursIshiAmaldaFile = kursIshiAmaldaFile; }
    public MultipartFile getBitiruvIshiAmaldaFile() { return bitiruvIshiAmaldaFile; }
    public void setBitiruvIshiAmaldaFile(MultipartFile bitiruvIshiAmaldaFile) { this.bitiruvIshiAmaldaFile = bitiruvIshiAmaldaFile; }
    public MultipartFile getDakBmiRahbarligiAmaldaFile() { return dakBmiRahbarligiAmaldaFile; }
    public void setDakBmiRahbarligiAmaldaFile(MultipartFile dakBmiRahbarligiAmaldaFile) { this.dakBmiRahbarligiAmaldaFile = dakBmiRahbarligiAmaldaFile; }
    public MultipartFile getAmaliyotAmaldaFile() { return amaliyotAmaldaFile; }
    public void setAmaliyotAmaldaFile(MultipartFile amaliyotAmaldaFile) { this.amaliyotAmaldaFile = amaliyotAmaldaFile; }
    public MultipartFile getItiAmaldaFile() { return itiAmaldaFile; }
    public void setItiAmaldaFile(MultipartFile itiAmaldaFile) { this.itiAmaldaFile = itiAmaldaFile; }
    public MultipartFile getBmiGaTaqrizAmaldaFile() { return bmiGaTaqrizAmaldaFile; }
    public void setBmiGaTaqrizAmaldaFile(MultipartFile bmiGaTaqrizAmaldaFile) { this.bmiGaTaqrizAmaldaFile = bmiGaTaqrizAmaldaFile; }
    public MultipartFile getQaytaTopshirishAmaldaFile() { return qaytaTopshirishAmaldaFile; }
    public void setQaytaTopshirishAmaldaFile(MultipartFile qaytaTopshirishAmaldaFile) { this.qaytaTopshirishAmaldaFile = qaytaTopshirishAmaldaFile; }
    public MultipartFile getRatingBallAmaldaFile() { return ratingBallAmaldaFile; }
    public void setRatingBallAmaldaFile(MultipartFile ratingBallAmaldaFile) { this.ratingBallAmaldaFile = ratingBallAmaldaFile; }
    public String getTeachingRowsPayload() { return teachingRowsPayload; }
    public void setTeachingRowsPayload(String teachingRowsPayload) { this.teachingRowsPayload = teachingRowsPayload; }
}
