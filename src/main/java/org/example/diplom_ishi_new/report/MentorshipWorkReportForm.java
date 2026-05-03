package org.example.diplom_ishi_new.report;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class MentorshipWorkReportForm {

    private String bajariladiganIshlar;
    private String muddat;
    private String otkazishJoyi;
    private String ishHajmi;
    private String rejadanTashqariIshlar;
    private String ratingBall;
    private MultipartFile bajariladiganIshlarFile;
    private MultipartFile rejadanTashqariIshlarFile;
    private List<MultipartFile> bajariladiganIshlarFiles = new ArrayList<>();
    private List<MultipartFile> rejadanTashqariIshlarFiles = new ArrayList<>();
    private List<MultipartFile> hisobotFiles = new ArrayList<>();

    public String getBajariladiganIshlar() { return bajariladiganIshlar; }
    public void setBajariladiganIshlar(String bajariladiganIshlar) { this.bajariladiganIshlar = bajariladiganIshlar; }
    public String getMuddat() { return muddat; }
    public void setMuddat(String muddat) { this.muddat = muddat; }
    public String getOtkazishJoyi() { return otkazishJoyi; }
    public void setOtkazishJoyi(String otkazishJoyi) { this.otkazishJoyi = otkazishJoyi; }
    public String getIshHajmi() { return ishHajmi; }
    public void setIshHajmi(String ishHajmi) { this.ishHajmi = ishHajmi; }
    public String getRejadanTashqariIshlar() { return rejadanTashqariIshlar; }
    public void setRejadanTashqariIshlar(String rejadanTashqariIshlar) { this.rejadanTashqariIshlar = rejadanTashqariIshlar; }
    public String getRatingBall() { return ratingBall; }
    public void setRatingBall(String ratingBall) { this.ratingBall = ratingBall; }
    public MultipartFile getBajariladiganIshlarFile() { return bajariladiganIshlarFile; }
    public void setBajariladiganIshlarFile(MultipartFile bajariladiganIshlarFile) { this.bajariladiganIshlarFile = bajariladiganIshlarFile; }
    public MultipartFile getRejadanTashqariIshlarFile() { return rejadanTashqariIshlarFile; }
    public void setRejadanTashqariIshlarFile(MultipartFile rejadanTashqariIshlarFile) { this.rejadanTashqariIshlarFile = rejadanTashqariIshlarFile; }
    public List<MultipartFile> getBajariladiganIshlarFiles() { return bajariladiganIshlarFiles; }
    public void setBajariladiganIshlarFiles(List<MultipartFile> bajariladiganIshlarFiles) { this.bajariladiganIshlarFiles = bajariladiganIshlarFiles; }
    public List<MultipartFile> getRejadanTashqariIshlarFiles() { return rejadanTashqariIshlarFiles; }
    public void setRejadanTashqariIshlarFiles(List<MultipartFile> rejadanTashqariIshlarFiles) { this.rejadanTashqariIshlarFiles = rejadanTashqariIshlarFiles; }
    public List<MultipartFile> getHisobotFiles() { return hisobotFiles; }
    public void setHisobotFiles(List<MultipartFile> hisobotFiles) { this.hisobotFiles = hisobotFiles; }
}
