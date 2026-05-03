package org.example.diplom_ishi_new.report;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class MethodicalWorkReportForm {

    private String bajariladiganIshlar;
    private String muddat;
    private String ishlarHajmi;
    private String rejadanTashqariIshlar;
    private String ratingBall;
    private MultipartFile bajariladiganIshlarFile;
    private MultipartFile muddatFile;
    private MultipartFile ishlarHajmiFile;
    private MultipartFile rejadanTashqariIshlarFile;
    private MultipartFile ratingBallFile;
    private List<MultipartFile> bajariladiganIshlarFiles = new ArrayList<>();
    private List<MultipartFile> rejadanTashqariIshlarFiles = new ArrayList<>();
    private List<MultipartFile> hisobotFiles = new ArrayList<>();

    public String getBajariladiganIshlar() { return bajariladiganIshlar; }
    public void setBajariladiganIshlar(String bajariladiganIshlar) { this.bajariladiganIshlar = bajariladiganIshlar; }
    public String getMuddat() { return muddat; }
    public void setMuddat(String muddat) { this.muddat = muddat; }
    public String getIshlarHajmi() { return ishlarHajmi; }
    public void setIshlarHajmi(String ishlarHajmi) { this.ishlarHajmi = ishlarHajmi; }
    public String getRejadanTashqariIshlar() { return rejadanTashqariIshlar; }
    public void setRejadanTashqariIshlar(String rejadanTashqariIshlar) { this.rejadanTashqariIshlar = rejadanTashqariIshlar; }
    public String getRatingBall() { return ratingBall; }
    public void setRatingBall(String ratingBall) { this.ratingBall = ratingBall; }
    public MultipartFile getBajariladiganIshlarFile() { return bajariladiganIshlarFile; }
    public void setBajariladiganIshlarFile(MultipartFile bajariladiganIshlarFile) { this.bajariladiganIshlarFile = bajariladiganIshlarFile; }
    public MultipartFile getMuddatFile() { return muddatFile; }
    public void setMuddatFile(MultipartFile muddatFile) { this.muddatFile = muddatFile; }
    public MultipartFile getIshlarHajmiFile() { return ishlarHajmiFile; }
    public void setIshlarHajmiFile(MultipartFile ishlarHajmiFile) { this.ishlarHajmiFile = ishlarHajmiFile; }
    public MultipartFile getRejadanTashqariIshlarFile() { return rejadanTashqariIshlarFile; }
    public void setRejadanTashqariIshlarFile(MultipartFile rejadanTashqariIshlarFile) { this.rejadanTashqariIshlarFile = rejadanTashqariIshlarFile; }
    public MultipartFile getRatingBallFile() { return ratingBallFile; }
    public void setRatingBallFile(MultipartFile ratingBallFile) { this.ratingBallFile = ratingBallFile; }
    public List<MultipartFile> getBajariladiganIshlarFiles() { return bajariladiganIshlarFiles; }
    public void setBajariladiganIshlarFiles(List<MultipartFile> bajariladiganIshlarFiles) { this.bajariladiganIshlarFiles = bajariladiganIshlarFiles; }
    public List<MultipartFile> getRejadanTashqariIshlarFiles() { return rejadanTashqariIshlarFiles; }
    public void setRejadanTashqariIshlarFiles(List<MultipartFile> rejadanTashqariIshlarFiles) { this.rejadanTashqariIshlarFiles = rejadanTashqariIshlarFiles; }
    public List<MultipartFile> getHisobotFiles() { return hisobotFiles; }
    public void setHisobotFiles(List<MultipartFile> hisobotFiles) { this.hisobotFiles = hisobotFiles; }
}
