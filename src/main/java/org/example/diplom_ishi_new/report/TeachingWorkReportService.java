package org.example.diplom_ishi_new.report;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.example.diplom_ishi_new.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TeachingWorkReportService {

    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String ROW_DELIMITER = "|||ROW|||";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(?:[\\.,]\\d+)?");
    private static final List<String> PROGRESS_SECTIONS = List.of(
            "O'quv ishlari",
            "Ilmiy-uslubiy ishlar",
            "Ilmiy-tadqiqot ishlari",
            "Ustoz-shogird ishlari"
    );
    private static final List<String> TEACHING_PAYLOAD_FIELDS = List.of(
            "semester", "workName", "faculty", "groupName", "studentCount", "maruza",
            "amaliyMashgulot", "laboratoriyaIshi", "maslahat", "nazorat", "taqrizlar",
            "kursIshi", "bitiruvIshi", "dakBmiRahbarligi", "amaliyot", "iti",
            "bmiGaTaqriz", "qaytaTopshirish", "ratingBall"
    );

    private final TeachingWorkReportRepository repository;
    private final String pythonPath;

    public TeachingWorkReportService(TeachingWorkReportRepository repository,
                                     @Value("${report.python.path:python}") String pythonPath) {
        this.repository = repository;
        this.pythonPath = pythonPath;
    }

    public void createReport(User teacher, TeachingWorkReportForm form) {
        TeachingWorkReport report = new TeachingWorkReport();
        report.setTeacher(teacher);
        report.setSection("O'quv ishlari");
        report.setSemester(form.getSemester());
        report.setAcademicYear(clean(form.getAcademicYear()));
        report.setWorkName(defaultText(form.getWorkName()));
        report.setFaculty(defaultText(form.getFaculty(), "-"));
        report.setGroupName(defaultText(form.getGroupName(), "-"));
        report.setStudentCount(form.getStudentCount() == null ? 1 : form.getStudentCount());
        report.setMaruza(form.getMaruza());
        report.setMaruzaJami(form.getMaruzaJami());
        report.setMaruzaAmalda(form.getMaruzaAmalda());
        report.setAmaliyMashgulot(form.getAmaliyMashgulot());
        report.setAmaliyMashgulotJami(form.getAmaliyMashgulotJami());
        report.setAmaliyMashgulotAmalda(form.getAmaliyMashgulotAmalda());
        report.setLaboratoriyaIshi(form.getLaboratoriyaIshi());
        report.setLaboratoriyaIshiJami(form.getLaboratoriyaIshiJami());
        report.setLaboratoriyaIshiAmalda(form.getLaboratoriyaIshiAmalda());
        report.setMaslahat(form.getMaslahat());
        report.setMaslahatJami(form.getMaslahatJami());
        report.setMaslahatAmalda(form.getMaslahatAmalda());
        report.setNazorat(form.getNazorat());
        report.setNazoratJami(form.getNazoratJami());
        report.setNazoratAmalda(form.getNazoratAmalda());
        report.setTaqrizlar(form.getTaqrizlar());
        report.setTaqrizlarJami(form.getTaqrizlarJami());
        report.setTaqrizlarAmalda(form.getTaqrizlarAmalda());
        report.setKursIshi(form.getKursIshi());
        report.setKursIshiJami(form.getKursIshiJami());
        report.setKursIshiAmalda(form.getKursIshiAmalda());
        report.setBitiruvIshi(form.getBitiruvIshi());
        report.setBitiruvIshiJami(form.getBitiruvIshiJami());
        report.setBitiruvIshiAmalda(form.getBitiruvIshiAmalda());
        report.setDakBmiRahbarligi(form.getDakBmiRahbarligi());
        report.setDakBmiRahbarligiJami(form.getDakBmiRahbarligiJami());
        report.setDakBmiRahbarligiAmalda(form.getDakBmiRahbarligiAmalda());
        report.setAmaliyot(form.getAmaliyot());
        report.setAmaliyotJami(form.getAmaliyotJami());
        report.setAmaliyotAmalda(form.getAmaliyotAmalda());
        report.setIti(form.getIti());
        report.setItiJami(form.getItiJami());
        report.setItiAmalda(form.getItiAmalda());
        report.setBmiGaTaqriz(form.getBmiGaTaqriz());
        report.setBmiGaTaqrizJami(form.getBmiGaTaqrizJami());
        report.setBmiGaTaqrizAmalda(form.getBmiGaTaqrizAmalda());
        report.setQaytaTopshirish(form.getQaytaTopshirish());
        report.setQaytaTopshirishJami(form.getQaytaTopshirishJami());
        report.setQaytaTopshirishAmalda(form.getQaytaTopshirishAmalda());
        report.setRatingBall(form.getRatingBall());
        report.setRatingBallJami(form.getRatingBallJami());
        report.setRatingBallAmalda(form.getRatingBallAmalda());
        report.setStatus(ReportStatus.YUBORILGAN);
        report.setCreatedAt(LocalDateTime.now());
        report.setLastActionAt(report.getCreatedAt());
        report.setAmaldaFiles(null);
        tryAttachReportFile(report);
        repository.save(report);
    }

    public void createReports(User teacher, TeachingWorkReportForm form) {
        List<TeachingWorkReportForm> forms = parseTeachingRows(form);
        if (forms.isEmpty()) {
            createReport(teacher, form);
            return;
        }
        for (TeachingWorkReportForm rowForm : forms) {
            createReport(teacher, rowForm);
        }
    }

    private List<TeachingWorkReportForm> parseTeachingRows(TeachingWorkReportForm fallbackForm) {
        if (fallbackForm.getTeachingRowsPayload() == null || fallbackForm.getTeachingRowsPayload().isBlank()) {
            return List.of();
        }
        List<TeachingWorkReportForm> forms = new ArrayList<>();
        String[] rows = fallbackForm.getTeachingRowsPayload().split("\\R");
        for (String rowPayload : rows) {
            Map<String, String> row = parseTeachingPayloadRow(rowPayload);
            if (hasTeachingRowValue(row)) {
                TeachingWorkReportForm rowForm = toTeachingForm(row);
                rowForm.setAcademicYear(fallbackForm.getAcademicYear());
                rowForm.setSemester(fallbackForm.getSemester());
                forms.add(rowForm);
            }
        }
        return forms;
    }

    private Map<String, String> parseTeachingPayloadRow(String rowPayload) {
        Map<String, String> row = new HashMap<>();
        String[] values = rowPayload.split("\\|", -1);
        for (int i = 0; i < TEACHING_PAYLOAD_FIELDS.size(); i++) {
            row.put(TEACHING_PAYLOAD_FIELDS.get(i), i < values.length ? decodePayloadValue(values[i]) : "");
        }
        return row;
    }

    private String decodePayloadValue(String value) {
        try {
            return java.net.URLDecoder.decode(value == null ? "" : value, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    private boolean hasTeachingRowValue(Map<String, String> row) {
        return row.values().stream().anyMatch(value -> value != null && !value.isBlank());
    }

    private TeachingWorkReportForm toTeachingForm(Map<String, String> row) {
        TeachingWorkReportForm form = new TeachingWorkReportForm();
        form.setSemester(parseSemester(row.get("semester")));
        form.setWorkName(clean(row.get("workName")));
        form.setFaculty(defaultText(row.get("faculty"), "-"));
        form.setGroupName(defaultText(row.get("groupName"), "-"));
        Integer studentCount = parseInteger(row.get("studentCount"));
        form.setStudentCount(studentCount == null ? 1 : studentCount);
        setTeachingNumber(form, row, "maruza");
        setTeachingNumber(form, row, "amaliyMashgulot");
        setTeachingNumber(form, row, "laboratoriyaIshi");
        setTeachingNumber(form, row, "maslahat");
        setTeachingNumber(form, row, "nazorat");
        setTeachingNumber(form, row, "taqrizlar");
        setTeachingNumber(form, row, "kursIshi");
        setTeachingNumber(form, row, "bitiruvIshi");
        setTeachingNumber(form, row, "dakBmiRahbarligi");
        setTeachingNumber(form, row, "amaliyot");
        setTeachingNumber(form, row, "iti");
        setTeachingNumber(form, row, "bmiGaTaqriz");
        setTeachingNumber(form, row, "qaytaTopshirish");
        setTeachingNumber(form, row, "ratingBall");
        return form;
    }

    private Semester parseSemester(String value) {
        if (value == null || value.isBlank()) {
            return Semester.KUZGI;
        }
        try {
            return Semester.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return Semester.KUZGI;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void setTeachingNumber(TeachingWorkReportForm form, Map<String, String> row, String field) {
        Integer value = parseInteger(row.get(field));
        switch (field) {
            case "maruza" -> { form.setMaruza(value); form.setMaruzaJami(value); }
            case "amaliyMashgulot" -> { form.setAmaliyMashgulot(value); form.setAmaliyMashgulotJami(value); }
            case "laboratoriyaIshi" -> { form.setLaboratoriyaIshi(value); form.setLaboratoriyaIshiJami(value); }
            case "maslahat" -> { form.setMaslahat(value); form.setMaslahatJami(value); }
            case "nazorat" -> { form.setNazorat(value); form.setNazoratJami(value); }
            case "taqrizlar" -> { form.setTaqrizlar(value); form.setTaqrizlarJami(value); }
            case "kursIshi" -> { form.setKursIshi(value); form.setKursIshiJami(value); }
            case "bitiruvIshi" -> { form.setBitiruvIshi(value); form.setBitiruvIshiJami(value); }
            case "dakBmiRahbarligi" -> { form.setDakBmiRahbarligi(value); form.setDakBmiRahbarligiJami(value); }
            case "amaliyot" -> { form.setAmaliyot(value); form.setAmaliyotJami(value); }
            case "iti" -> { form.setIti(value); form.setItiJami(value); }
            case "bmiGaTaqriz" -> { form.setBmiGaTaqriz(value); form.setBmiGaTaqrizJami(value); }
            case "qaytaTopshirish" -> { form.setQaytaTopshirish(value); form.setQaytaTopshirishJami(value); }
            case "ratingBall" -> { form.setRatingBall(value); form.setRatingBallJami(value); }
            default -> { }
        }
    }

    public void createMethodicalReport(User teacher, MethodicalWorkReportForm form) {
        TeachingWorkReport report = new TeachingWorkReport();
        report.setTeacher(teacher);
        report.setSection("Ilmiy-uslubiy ishlar");
        report.setSemester(Semester.KUZGI);
        report.setWorkName("Ilmiy-uslubiy ishlar");
        report.setFaculty("-");
        report.setGroupName("-");
        report.setStudentCount(1);
        report.setMethodicalBajariladiganIshlar(clean(form.getBajariladiganIshlar()));
        report.setMethodicalMuddat(clean(form.getMuddat()));
        report.setMethodicalIshlarHajmi(clean(form.getIshlarHajmi()));
        report.setMethodicalRejadanTashqariIshlar(clean(form.getRejadanTashqariIshlar()));
        report.setMethodicalRatingBall(clean(form.getRatingBall()));
        report.setStatus(ReportStatus.YUBORILGAN);
        report.setCreatedAt(LocalDateTime.now());
        report.setLastActionAt(report.getCreatedAt());
        report.setAmaldaFiles(null);
        repository.save(report);
    }

    public void createResearchReport(User teacher, ResearchWorkReportForm form) {
        TeachingWorkReport report = new TeachingWorkReport();
        report.setTeacher(teacher);
        report.setSection("Ilmiy-tadqiqot ishlari");
        report.setSemester(Semester.KUZGI);
        report.setWorkName("Ilmiy-tadqiqot ishlari");
        report.setFaculty("-");
        report.setGroupName("-");
        report.setStudentCount(1);
        report.setResearchBajariladiganIshlar(clean(form.getBajariladiganIshlar()));
        report.setResearchMuddat(clean(form.getMuddat()));
        report.setResearchIjroBelgisi(clean(form.getIjroBelgisi()));
        report.setResearchIshHajmi(clean(form.getIshHajmi()));
        report.setResearchRejadanTashqariIshlar(clean(form.getRejadanTashqariIshlar()));
        report.setResearchRatingBall(clean(form.getRatingBall()));
        report.setStatus(ReportStatus.YUBORILGAN);
        report.setCreatedAt(LocalDateTime.now());
        report.setLastActionAt(report.getCreatedAt());
        report.setAmaldaFiles(null);
        repository.save(report);
    }

    public void createMentorshipReport(User teacher, MentorshipWorkReportForm form) {
        TeachingWorkReport report = new TeachingWorkReport();
        report.setTeacher(teacher);
        report.setSection("Ustoz-shogird ishlari");
        report.setSemester(Semester.KUZGI);
        report.setWorkName("Ustoz-shogird ishlari");
        report.setFaculty("-");
        report.setGroupName("-");
        report.setStudentCount(1);
        report.setMentorshipBajariladiganIshlar(clean(form.getBajariladiganIshlar()));
        report.setMentorshipMuddat(clean(form.getMuddat()));
        report.setMentorshipOtkazishJoyi(clean(form.getOtkazishJoyi()));
        report.setMentorshipIshHajmi(clean(form.getIshHajmi()));
        report.setMentorshipRejadanTashqariIshlar(clean(form.getRejadanTashqariIshlar()));
        report.setMentorshipRatingBall(clean(form.getRatingBall()));
        report.setStatus(ReportStatus.YUBORILGAN);
        report.setCreatedAt(LocalDateTime.now());
        report.setLastActionAt(report.getCreatedAt());
        report.setAmaldaFiles(null);
        repository.save(report);
    }

    private String saveMethodicalFiles(User teacher, LocalDateTime createdAt, MethodicalWorkReportForm form) {
        List<String> savedFiles = new ArrayList<>();
        String safeTeacher = teacher.getUsername().replaceAll("[^a-zA-Z0-9_-]", "_");
        Path uploadDir = Paths.get("reports", "uploads", safeTeacher + "_" + FILE_FORMAT.format(createdAt));
        saveRowFiles(uploadDir, savedFiles, "methodicalHisobot", "Hisobot", form.getHisobotFiles(), null);
        return savedFiles.isEmpty() ? null : String.join("\n", savedFiles);
    }

    private String saveResearchFiles(User teacher, LocalDateTime createdAt, ResearchWorkReportForm form) {
        List<String> savedFiles = new ArrayList<>();
        String safeTeacher = teacher.getUsername().replaceAll("[^a-zA-Z0-9_-]", "_");
        Path uploadDir = Paths.get("reports", "uploads", safeTeacher + "_" + FILE_FORMAT.format(createdAt));
        saveRowFiles(uploadDir, savedFiles, "researchHisobot", "Hisobot", form.getHisobotFiles(), null);
        return savedFiles.isEmpty() ? null : String.join("\n", savedFiles);
    }

    private String saveMentorshipFiles(User teacher, LocalDateTime createdAt, MentorshipWorkReportForm form) {
        List<String> savedFiles = new ArrayList<>();
        String safeTeacher = teacher.getUsername().replaceAll("[^a-zA-Z0-9_-]", "_");
        Path uploadDir = Paths.get("reports", "uploads", safeTeacher + "_" + FILE_FORMAT.format(createdAt));
        saveRowFiles(uploadDir, savedFiles, "mentorshipHisobot", "Hisobot", form.getHisobotFiles(), null);
        return savedFiles.isEmpty() ? null : String.join("\n", savedFiles);
    }

    private void saveRowFiles(Path uploadDir, List<String> savedFiles, String key, String label,
                              List<MultipartFile> files, MultipartFile fallbackFile) {
        boolean hasRowFiles = files != null && files.stream().anyMatch(file -> file != null && !file.isEmpty());
        if (!hasRowFiles) {
            saveAmaldaFile(uploadDir, savedFiles, key, label, fallbackFile);
            return;
        }
        for (int i = 0; i < files.size(); i++) {
            saveAmaldaFile(uploadDir, savedFiles, key + "_" + i, label + " " + (i + 1), files.get(i));
        }
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private String defaultText(String value) {
        return defaultText(value, "-");
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String saveAmaldaFiles(User teacher, LocalDateTime createdAt, TeachingWorkReportForm form) {
        List<String> savedFiles = new ArrayList<>();
        String safeTeacher = teacher.getUsername().replaceAll("[^a-zA-Z0-9_-]", "_");
        Path uploadDir = Paths.get("reports", "uploads", safeTeacher + "_" + FILE_FORMAT.format(createdAt));

        saveAmaldaFile(uploadDir, savedFiles, "maruza", "Ma'ruza", form.getMaruzaAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "amaliyMashgulot", "Amaliy mashg'ulot", form.getAmaliyMashgulotAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "laboratoriyaIshi", "Laboratoriya ishi", form.getLaboratoriyaIshiAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "maslahat", "Maslahat", form.getMaslahatAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "nazorat", "Nazorat", form.getNazoratAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "taqrizlar", "Taqrizlar", form.getTaqrizlarAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "kursIshi", "Kurs ishi", form.getKursIshiAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "bitiruvIshi", "Bitiruv ishi", form.getBitiruvIshiAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "dakBmiRahbarligi", "DAK(BMI) rahbarligi", form.getDakBmiRahbarligiAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "amaliyot", "Amaliyot", form.getAmaliyotAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "iti", "ITI", form.getItiAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "bmiGaTaqriz", "BMI ga taqriz", form.getBmiGaTaqrizAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "qaytaTopshirish", "Qayta topshirish", form.getQaytaTopshirishAmaldaFile());
        saveAmaldaFile(uploadDir, savedFiles, "ratingBall", "To'plangan reyting bali", form.getRatingBallAmaldaFile());

        return savedFiles.isEmpty() ? null : String.join("\n", savedFiles);
    }

    private void saveAmaldaFile(Path uploadDir, List<String> savedFiles, String key, String label, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }

        try {
            Files.createDirectories(uploadDir);
            String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safeName = originalName.replaceAll("[\\\\/:*?\"<>|]", "_");
            Path filePath = uploadDir.resolve(label.replaceAll("[^a-zA-Z0-9_-]", "_") + "_" + safeName);
            file.transferTo(filePath.toAbsolutePath());
            savedFiles.add(key + "|" + label + "|" + safeName + "|" + filePath.toAbsolutePath());
        } catch (IOException exception) {
            throw new IllegalStateException("Amalda faylini saqlab bo'lmadi", exception);
        }
    }

    public List<AmaldaFileItem> getAmaldaFileItems(TeachingWorkReport report) {
        List<AmaldaFileItem> items = new ArrayList<>();
        if (report.getAmaldaFiles() == null || report.getAmaldaFiles().isBlank()) {
            return items;
        }

        String[] lines = report.getAmaldaFiles().split("\\R");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = line.split("\\|", 4);
            if (parts.length == 4) {
                items.add(new AmaldaFileItem(i, parts[0], parts[1], parts[2], parts[3], true));
            } else if (parts.length == 3) {
                items.add(new AmaldaFileItem(i, keyForLabel(parts[0]), parts[0], parts[1], parts[2], true));
            } else {
                items.add(new AmaldaFileItem(i, "", line, "", "", false));
            }
        }
        return items;
    }

    public Map<String, AmaldaFileItem> getAmaldaFileMap(TeachingWorkReport report) {
        Map<String, AmaldaFileItem> map = new HashMap<>();
        for (AmaldaFileItem item : getAmaldaFileItems(report)) {
            if (item.getKey() != null && !item.getKey().isBlank()) {
                map.put(item.getKey(), item);
            }
        }
        return map;
    }

    public List<ResearchRow> getResearchRows(TeachingWorkReport report) {
        String[] works = splitRows(report.getResearchBajariladiganIshlar());
        String[] deadlines = splitRows(report.getResearchMuddat());
        String[] executions = splitRows(report.getResearchIjroBelgisi());
        String[] volumes = splitRows(report.getResearchIshHajmi());
        String[] extraWorks = splitRows(report.getResearchRejadanTashqariIshlar());
        String[] ratings = splitRows(report.getResearchRatingBall());
        int size = maxLength(works, deadlines, executions, volumes, extraWorks, ratings);
        List<ResearchRow> rows = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            rows.add(new ResearchRow(
                    valueAt(works, i),
                    valueAt(deadlines, i),
                    valueAt(executions, i),
                    valueAt(volumes, i),
                    valueAt(extraWorks, i),
                    valueAt(ratings, i)
            ));
        }
        return rows;
    }

    public Map<String, String> getResearchTotals(List<ResearchRow> rows) {
        Map<String, String> totals = new HashMap<>();
        totals.put("muddat", sumValues(rows.stream().map(ResearchRow::getMuddat).toList()));
        totals.put("ijroBelgisi", sumValues(rows.stream().map(ResearchRow::getIjroBelgisi).toList()));
        totals.put("ishHajmi", sumValues(rows.stream().map(ResearchRow::getIshHajmi).toList()));
        totals.put("rejadanTashqariIshlar", sumValues(rows.stream().map(ResearchRow::getRejadanTashqariIshlar).toList()));
        totals.put("ratingBall", sumValues(rows.stream().map(ResearchRow::getRatingBall).toList()));
        return totals;
    }

    public List<MethodicalRow> getMethodicalRows(TeachingWorkReport report) {
        String[] works = splitRows(report.getMethodicalBajariladiganIshlar());
        String[] deadlines = splitRows(report.getMethodicalMuddat());
        String[] volumes = splitRows(report.getMethodicalIshlarHajmi());
        String[] extraWorks = splitRows(report.getMethodicalRejadanTashqariIshlar());
        String[] ratings = splitRows(report.getMethodicalRatingBall());
        int size = maxLength(works, deadlines, volumes, extraWorks, ratings);
        List<MethodicalRow> rows = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            rows.add(new MethodicalRow(
                    valueAt(works, i),
                    valueAt(deadlines, i),
                    valueAt(volumes, i),
                    valueAt(extraWorks, i),
                    valueAt(ratings, i)
            ));
        }
        return rows;
    }

    public Map<String, String> getMethodicalTotals(List<MethodicalRow> rows) {
        Map<String, String> totals = new HashMap<>();
        totals.put("muddat", sumValues(rows.stream().map(MethodicalRow::getMuddat).toList()));
        totals.put("ishlarHajmi", sumValues(rows.stream().map(MethodicalRow::getIshlarHajmi).toList()));
        totals.put("rejadanTashqariIshlar", sumValues(rows.stream().map(MethodicalRow::getRejadanTashqariIshlar).toList()));
        totals.put("ratingBall", sumValues(rows.stream().map(MethodicalRow::getRatingBall).toList()));
        return totals;
    }

    public List<MentorshipRow> getMentorshipRows(TeachingWorkReport report) {
        String[] works = splitRows(report.getMentorshipBajariladiganIshlar());
        String[] deadlines = splitRows(report.getMentorshipMuddat());
        String[] places = splitRows(report.getMentorshipOtkazishJoyi());
        String[] volumes = splitRows(report.getMentorshipIshHajmi());
        String[] extraWorks = splitRows(report.getMentorshipRejadanTashqariIshlar());
        String[] ratings = splitRows(report.getMentorshipRatingBall());
        int size = maxLength(works, deadlines, places, volumes, extraWorks, ratings);
        List<MentorshipRow> rows = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            rows.add(new MentorshipRow(
                    valueAt(works, i),
                    valueAt(deadlines, i),
                    valueAt(places, i),
                    valueAt(volumes, i),
                    valueAt(extraWorks, i),
                    valueAt(ratings, i)
            ));
        }
        return rows;
    }

    public Map<String, String> getMentorshipTotals(List<MentorshipRow> rows) {
        Map<String, String> totals = new HashMap<>();
        totals.put("muddat", sumValues(rows.stream().map(MentorshipRow::getMuddat).toList()));
        totals.put("otkazishJoyi", sumValues(rows.stream().map(MentorshipRow::getOtkazishJoyi).toList()));
        totals.put("ishHajmi", sumValues(rows.stream().map(MentorshipRow::getIshHajmi).toList()));
        totals.put("rejadanTashqariIshlar", sumValues(rows.stream().map(MentorshipRow::getRejadanTashqariIshlar).toList()));
        totals.put("ratingBall", sumValues(rows.stream().map(MentorshipRow::getRatingBall).toList()));
        return totals;
    }

    private String sumValues(List<String> values) {
        BigDecimal total = BigDecimal.ZERO;
        for (String value : values) {
            if (value == null || value.isBlank() || "-".equals(value.trim())) {
                continue;
            }
            Matcher matcher = NUMBER_PATTERN.matcher(value);
            while (matcher.find()) {
                total = total.add(new BigDecimal(matcher.group().replace(',', '.')));
            }
        }
        return total.stripTrailingZeros().toPlainString();
    }

    private String[] splitRows(String value) {
        if (value == null || value.isBlank()) {
            return new String[]{""};
        }
        return value.split("\\Q" + ROW_DELIMITER + "\\E", -1);
    }

    private int maxLength(String[]... values) {
        int max = 1;
        for (String[] value : values) {
            max = Math.max(max, value.length);
        }
        return max;
    }

    private String valueAt(String[] values, int index) {
        if (index >= values.length || values[index] == null || values[index].isBlank()) {
            return "-";
        }
        return values[index];
    }

    private String keyForLabel(String label) {
        return switch (label) {
            case "Ma'ruza" -> "maruza";
            case "Amaliy mashg'ulot" -> "amaliyMashgulot";
            case "Laboratoriya ishi" -> "laboratoriyaIshi";
            case "Maslahat" -> "maslahat";
            case "Nazorat" -> "nazorat";
            case "Taqrizlar" -> "taqrizlar";
            case "Kurs ishi" -> "kursIshi";
            case "Bitiruv ishi" -> "bitiruvIshi";
            case "DAK(BMI) rahbarligi" -> "dakBmiRahbarligi";
            case "Amaliyot" -> "amaliyot";
            case "ITI" -> "iti";
            case "BMI ga taqriz" -> "bmiGaTaqriz";
            case "Qayta topshirish" -> "qaytaTopshirish";
            case "To'plangan reyting bali" -> "ratingBall";
            case "Bajariladigan ishlar" -> "methodicalBajariladiganIshlar";
            default -> "";
        };
    }

    public Path getAmaldaFilePath(TeachingWorkReport report, int fileIndex) {
        List<AmaldaFileItem> items = getAmaldaFileItems(report);
        if (fileIndex < 0 || fileIndex >= items.size() || !items.get(fileIndex).isDownloadable()) {
            throw new IllegalArgumentException("Fayl topilmadi");
        }
        return Paths.get(items.get(fileIndex).getPath());
    }

    public static class AmaldaFileItem {
        private final int index;
        private final String key;
        private final String label;
        private final String fileName;
        private final String path;
        private final boolean downloadable;

        public AmaldaFileItem(int index, String key, String label, String fileName, String path, boolean downloadable) {
            this.index = index;
            this.key = key;
            this.label = label;
            this.fileName = fileName;
            this.path = path;
            this.downloadable = downloadable;
        }

        public int getIndex() { return index; }
        public String getKey() { return key; }
        public String getLabel() { return label; }
        public String getFileName() { return fileName; }
        public String getPath() { return path; }
        public boolean isDownloadable() { return downloadable; }
    }

    public static class ResearchRow {
        private final String bajariladiganIshlar;
        private final String muddat;
        private final String ijroBelgisi;
        private final String ishHajmi;
        private final String rejadanTashqariIshlar;
        private final String ratingBall;

        public ResearchRow(String bajariladiganIshlar, String muddat, String ijroBelgisi,
                           String ishHajmi, String rejadanTashqariIshlar, String ratingBall) {
            this.bajariladiganIshlar = bajariladiganIshlar;
            this.muddat = muddat;
            this.ijroBelgisi = ijroBelgisi;
            this.ishHajmi = ishHajmi;
            this.rejadanTashqariIshlar = rejadanTashqariIshlar;
            this.ratingBall = ratingBall;
        }

        public String getBajariladiganIshlar() { return bajariladiganIshlar; }
        public String getMuddat() { return muddat; }
        public String getIjroBelgisi() { return ijroBelgisi; }
        public String getIshHajmi() { return ishHajmi; }
        public String getRejadanTashqariIshlar() { return rejadanTashqariIshlar; }
        public String getRatingBall() { return ratingBall; }
    }

    public static class MethodicalRow {
        private final String bajariladiganIshlar;
        private final String muddat;
        private final String ishlarHajmi;
        private final String rejadanTashqariIshlar;
        private final String ratingBall;

        public MethodicalRow(String bajariladiganIshlar, String muddat, String ishlarHajmi,
                             String rejadanTashqariIshlar, String ratingBall) {
            this.bajariladiganIshlar = bajariladiganIshlar;
            this.muddat = muddat;
            this.ishlarHajmi = ishlarHajmi;
            this.rejadanTashqariIshlar = rejadanTashqariIshlar;
            this.ratingBall = ratingBall;
        }

        public String getBajariladiganIshlar() { return bajariladiganIshlar; }
        public String getMuddat() { return muddat; }
        public String getIshlarHajmi() { return ishlarHajmi; }
        public String getRejadanTashqariIshlar() { return rejadanTashqariIshlar; }
        public String getRatingBall() { return ratingBall; }
    }

    public static class MentorshipRow {
        private final String bajariladiganIshlar;
        private final String muddat;
        private final String otkazishJoyi;
        private final String ishHajmi;
        private final String rejadanTashqariIshlar;
        private final String ratingBall;

        public MentorshipRow(String bajariladiganIshlar, String muddat, String otkazishJoyi,
                             String ishHajmi, String rejadanTashqariIshlar, String ratingBall) {
            this.bajariladiganIshlar = bajariladiganIshlar;
            this.muddat = muddat;
            this.otkazishJoyi = otkazishJoyi;
            this.ishHajmi = ishHajmi;
            this.rejadanTashqariIshlar = rejadanTashqariIshlar;
            this.ratingBall = ratingBall;
        }

        public String getBajariladiganIshlar() { return bajariladiganIshlar; }
        public String getMuddat() { return muddat; }
        public String getOtkazishJoyi() { return otkazishJoyi; }
        public String getIshHajmi() { return ishHajmi; }
        public String getRejadanTashqariIshlar() { return rejadanTashqariIshlar; }
        public String getRatingBall() { return ratingBall; }
    }

    public List<TeachingWorkReport> getTeacherReports(User teacher) {
        return repository.findByTeacherOrderByCreatedAtDesc(teacher);
    }

    public List<TeachingWorkReport> getTeacherReportsBySection(User teacher, String section) {
        return getTeacherReports(teacher).stream()
                .filter(report -> section.equals(report.getSection()))
                .collect(Collectors.toList());
    }

    public List<TeachingWorkReport> getTeacherMonitoringReportsBySection(User teacher, String section) {
        return getTeacherReports(teacher).stream()
                .filter(report -> section.equals(report.getSection()))
                .filter(report -> !isReportSubmitted(report))
                .collect(Collectors.toList());
    }

    public List<TeachingWorkReport> getCompletedTeacherReportsBySection(User teacher, String section) {
        return getTeacherReports(teacher).stream()
                .filter(this::isReportReviewed)
                .filter(report -> section.equals(report.getSection()))
                .collect(Collectors.toList());
    }

    public List<TeachingWorkReport> getReviewedTeacherSubmittedReportsBySection(User teacher, String section) {
        return getTeacherReports(teacher).stream()
                .filter(report -> section.equals(report.getSection()))
                .filter(this::isReportSubmitted)
                .filter(this::isReportReviewed)
                .collect(Collectors.toList());
    }

    public List<TeachingWorkReport> getAllReports() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public List<TeachingWorkReport> getReportsForPlans(String query) {
        List<TeachingWorkReport> reports = getAllReports();
        if (query == null || query.isBlank()) {
            return reports;
        }
        String normalizedQuery = query.trim().toLowerCase();
        return reports.stream()
                .filter(report -> {
                    String fullName = report.getTeacher().getFullName() == null ? "" : report.getTeacher().getFullName();
                    String username = report.getTeacher().getUsername() == null ? "" : report.getTeacher().getUsername();
                    return fullName.toLowerCase().contains(normalizedQuery)
                            || username.toLowerCase().contains(normalizedQuery);
                })
                .collect(Collectors.toList());
    }

    public List<TeachingWorkReport> getPendingReports() {
        return repository.findByStatusOrderByCreatedAtDesc(ReportStatus.YUBORILGAN);
    }

    public List<TeachingWorkReport> getHeadNotifications() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .filter(report -> report.getStatus() == ReportStatus.YUBORILGAN
                        || (report.getAmaldaFiles() != null
                        && !report.getAmaldaFiles().isBlank()
                        && (report.getReportResponse() == null || report.getReportResponse().isBlank())))
                .collect(Collectors.toList());
    }

    public List<TeachingWorkReport> getTeacherUploadNotifications(User teacher) {
        return getTeacherReports(teacher).stream()
                .filter(this::isApproved)
                .filter(report -> report.getAmaldaFiles() == null || report.getAmaldaFiles().isBlank())
                .filter(report -> "O'quv ishlari".equals(report.getSection())
                        || "Ilmiy-uslubiy ishlar".equals(report.getSection())
                        || "Ilmiy-tadqiqot ishlari".equals(report.getSection())
                        || "Ustoz-shogird ishlari".equals(report.getSection()))
                .collect(Collectors.toList());
    }

    public List<TeachingWorkReport> getMonitoringReports() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .filter(report -> report.getStatus() == ReportStatus.YUBORILGAN
                        || (isApproved(report) && !isReportReviewed(report)))
                .collect(Collectors.toList());
    }

    public List<TeachingWorkReport> getReviewedReports() {
        return repository.findByStatusInOrderByCreatedAtDesc(Arrays.asList(
                ReportStatus.TASDIQLANDI,
                ReportStatus.RAD_ETILDI,
                ReportStatus.KORIB_CHIQILDI
        ));
    }

    public void respond(Long id, String response) {
        approve(id, response);
    }

    public void approve(Long id, String response) {
        TeachingWorkReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hisobot topilmadi"));
        report.setHeadResponse(cleanResponse(response, "tasdiqlangan"));
        report.setStatus(ReportStatus.KORIB_CHIQILDI);
        report.setLastActionAt(LocalDateTime.now());
        repository.save(report);
    }

    public void reject(Long id, String response) {
        TeachingWorkReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hisobot topilmadi"));
        report.setHeadResponse(cleanResponse(response, "rad etilgan"));
        report.setStatus(ReportStatus.KORIB_CHIQILDI);
        report.setLastActionAt(LocalDateTime.now());
        repository.save(report);
    }

    public void approveSubmittedReport(Long id, String response) {
        TeachingWorkReport report = findById(id);
        if (report.getAmaldaFiles() == null || report.getAmaldaFiles().isBlank()) {
            throw new IllegalStateException("Avval o'qituvchi hisobot fayllarini yuborishi kerak");
        }
        report.setReportResponse(requiredDecisionResponse(response, "Hisobot tasdiqlandi"));
        report.setLastActionAt(LocalDateTime.now());
        repository.save(report);
    }

    public void rejectSubmittedReport(Long id, String response) {
        TeachingWorkReport report = findById(id);
        if (report.getAmaldaFiles() == null || report.getAmaldaFiles().isBlank()) {
            throw new IllegalStateException("Avval o'qituvchi hisobot fayllarini yuborishi kerak");
        }
        report.setReportResponse(requiredDecisionResponse(response, "Hisobot rad etildi"));
        report.setLastActionAt(LocalDateTime.now());
        repository.save(report);
    }

    private String cleanResponse(String response, String fallback) {
        return response == null || response.isBlank() ? fallback : response.trim();
    }

    private String requiredDecisionResponse(String response, String decision) {
        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException("Hisobot uchun sharh yozish majburiy");
        }
        return decision + ": " + response.trim();
    }

    public void attachTeachingAmaldaFiles(Long id, TeachingWorkReportForm form) {
        TeachingWorkReport report = findById(id);
        if (!isApproved(report) || !"O'quv ishlari".equals(report.getSection())) {
            throw new IllegalStateException("Fayl faqat tasdiqlangan o'quv ishlari uchun yuklanadi");
        }
        report.setAmaldaFiles(saveAmaldaFiles(report.getTeacher(), LocalDateTime.now(), form));
        report.setReportResponse(null);
        report.setLastActionAt(LocalDateTime.now());
        repository.save(report);
    }

    public void attachMethodicalAmaldaFiles(Long id, MethodicalWorkReportForm form) {
        TeachingWorkReport report = findById(id);
        if (!isApproved(report) || !"Ilmiy-uslubiy ishlar".equals(report.getSection())) {
            throw new IllegalStateException("Fayl faqat tasdiqlangan ilmiy-uslubiy ishlar uchun yuklanadi");
        }
        if (isReportDeadlineExpired(report)) {
            throw new IllegalStateException("Hisobot yuborish muddati tugagan");
        }
        report.setAmaldaFiles(saveMethodicalFiles(report.getTeacher(), LocalDateTime.now(), form));
        report.setReportResponse(null);
        report.setLastActionAt(LocalDateTime.now());
        repository.save(report);
    }

    public void attachResearchAmaldaFiles(Long id, ResearchWorkReportForm form) {
        TeachingWorkReport report = findById(id);
        if (!isApproved(report) || !"Ilmiy-tadqiqot ishlari".equals(report.getSection())) {
            throw new IllegalStateException("Fayl faqat tasdiqlangan ilmiy-tadqiqot ishlari uchun yuklanadi");
        }
        if (isReportDeadlineExpired(report)) {
            throw new IllegalStateException("Hisobot yuborish muddati tugagan");
        }
        report.setAmaldaFiles(saveResearchFiles(report.getTeacher(), LocalDateTime.now(), form));
        report.setReportResponse(null);
        report.setLastActionAt(LocalDateTime.now());
        repository.save(report);
    }

    public void attachMentorshipAmaldaFiles(Long id, MentorshipWorkReportForm form) {
        TeachingWorkReport report = findById(id);
        if (!isApproved(report) || !"Ustoz-shogird ishlari".equals(report.getSection())) {
            throw new IllegalStateException("Fayl faqat tasdiqlangan ustoz-shogird ishlari uchun yuklanadi");
        }
        if (isReportDeadlineExpired(report)) {
            throw new IllegalStateException("Hisobot yuborish muddati tugagan");
        }
        report.setAmaldaFiles(saveMentorshipFiles(report.getTeacher(), LocalDateTime.now(), form));
        report.setReportResponse(null);
        report.setLastActionAt(LocalDateTime.now());
        repository.save(report);
    }

    public boolean isApproved(TeachingWorkReport report) {
        return report.getStatus() == ReportStatus.TASDIQLANDI
                || (report.getStatus() == ReportStatus.KORIB_CHIQILDI
                && report.getHeadResponse() != null
                && report.getHeadResponse().toLowerCase().contains("tasdiq"));
    }

    public boolean isReportSubmitted(TeachingWorkReport report) {
        return report.getAmaldaFiles() != null && !report.getAmaldaFiles().isBlank();
    }

    public boolean isReportReviewed(TeachingWorkReport report) {
        return report.getReportResponse() != null && !report.getReportResponse().isBlank();
    }

    public LocalDate getReportDeadline(TeachingWorkReport report) {
        if (report == null) {
            return null;
        }
        String deadlineValue = switch (report.getSection() == null ? "" : report.getSection()) {
            case "Ilmiy-uslubiy ishlar" -> report.getMethodicalMuddat();
            case "Ilmiy-tadqiqot ishlari" -> report.getResearchMuddat();
            case "Ustoz-shogird ishlari" -> report.getMentorshipMuddat();
            default -> null;
        };
        if (deadlineValue == null || deadlineValue.isBlank()) {
            return null;
        }
        return Arrays.stream(deadlineValue.split("\\Q" + ROW_DELIMITER + "\\E"))
                .map(this::extractDeadlineEndDate)
                .filter(java.util.Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    public boolean isReportDeadlineExpired(TeachingWorkReport report) {
        LocalDate deadline = getReportDeadline(report);
        return deadline != null && LocalDate.now().isAfter(deadline);
    }

    private LocalDate extractDeadlineEndDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        String[] parts = trimmed.split("\\s+-\\s+");
        String candidate = parts.length >= 2 ? parts[parts.length - 1].trim() : trimmed;
        try {
            return LocalDate.parse(candidate);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public List<SectionProgressSummary> getTeacherSectionProgress(User teacher) {
        List<TeachingWorkReport> reports = getTeacherReports(teacher);
        List<SectionProgressSummary> summaries = new ArrayList<>();
        for (String section : PROGRESS_SECTIONS) {
            TeachingWorkReport report = reports.stream()
                    .filter(item -> section.equals(item.getSection()))
                    .filter(item -> progressPercent(item) < 100 || shouldKeepVisible(item))
                    .findFirst()
                    .orElse(null);
            summaries.add(buildSectionProgress(section, report));
        }
        return summaries;
    }

    public int getTeacherAverageProgress(User teacher) {
        List<SectionProgressSummary> summaries = getTeacherSectionProgress(teacher);
        if (summaries.isEmpty()) {
            return 0;
        }
        int total = summaries.stream()
                .mapToInt(SectionProgressSummary::getProgress)
                .sum();
        return total / summaries.size();
    }

    private SectionProgressSummary buildSectionProgress(String section, TeachingWorkReport report) {
        String sectionKey = switch (section) {
            case "O'quv ishlari" -> "teaching";
            case "Ilmiy-uslubiy ishlar" -> "methodical";
            case "Ilmiy-tadqiqot ishlari" -> "research";
            case "Ustoz-shogird ishlari" -> "mentorship";
            default -> "teaching";
        };
        if (report == null) {
            return new SectionProgressSummary(sectionKey, section, 0, "Boshlanmagan", "Hali reja yuborilmagan");
        }
        return new SectionProgressSummary(
                sectionKey,
                section,
                progressPercent(report),
                progressStatus(report),
                progressNote(report)
        );
    }

    private boolean shouldKeepVisible(TeachingWorkReport report) {
        LocalDateTime lastAction = report.getLastActionAt() == null ? report.getCreatedAt() : report.getLastActionAt();
        return lastAction != null && lastAction.isAfter(LocalDateTime.now().minusDays(1));
    }

    private int progressPercent(TeachingWorkReport report) {
        if (report == null) {
            return 0;
        }
        if (isReportReviewed(report)) {
            return containsTasdiq(report.getReportResponse()) ? 100 : 75;
        }
        if (isReportSubmitted(report)) {
            return 75;
        }
        if (isApproved(report)) {
            return 50;
        }
        if (report.getHeadResponse() != null && !report.getHeadResponse().isBlank()) {
            return containsRad(report.getHeadResponse()) ? 25 : 50;
        }
        if (report.getStatus() == ReportStatus.YUBORILGAN) {
            return 25;
        }
        return 0;
    }

    private String progressStatus(TeachingWorkReport report) {
        if (report == null) {
            return "Boshlanmagan";
        }
        if (isReportReviewed(report)) {
            return containsTasdiq(report.getReportResponse()) ? "Bajarildi" : "Qayta yuborish kerak";
        }
        if (isReportSubmitted(report)) {
            return "Hisobot yuborilgan";
        }
        if (isApproved(report)) {
            return "Tasdiqlangan";
        }
        if (containsRad(report.getHeadResponse())) {
            return "Rad etilgan";
        }
        return "Yuborilgan";
    }

    private String progressNote(TeachingWorkReport report) {
        if (report == null) {
            return "Yangi ish reja hali yuborilmagan";
        }
        if (isReportReviewed(report)) {
            return containsTasdiq(report.getReportResponse())
                    ? "Hisobot tasdiqlandi"
                    : "Hisobot rad etildi. Qayta yuborish kerak.";
        }
        if (isReportSubmitted(report)) {
            return "Hisobot yuborilgan, tasdiqlanishi kutilmoqda";
        }
        if (isApproved(report)) {
            return "Reja tasdiqlandi. Endi hisobot yuborishingiz mumkin.";
        }
        if (report.getHeadResponse() != null && !report.getHeadResponse().isBlank()) {
            return containsRad(report.getHeadResponse())
                    ? "Reja rad etildi"
                    : "Reja tasdiqlandi. Endi hisobot yuborishingiz mumkin.";
        }
        return "Mudir ko'rib chiqishi kutilmoqda";
    }

    private boolean containsTasdiq(String value) {
        return value != null && value.toLowerCase().contains("tasdiq");
    }

    public void deleteReviewedReport(Long id) {
        TeachingWorkReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hisobot topilmadi"));
        if (report.getStatus() == ReportStatus.YUBORILGAN) {
            throw new IllegalStateException("Faqat ko'rib chiqilgan hisobotni o'chirish mumkin");
        }
        deleteStoredFiles(report);
        repository.delete(report);
    }

    public void deleteRejectedPlanByTeacher(Long id, User teacher) {
        TeachingWorkReport report = findTeacherOwnedReport(id, teacher);
        if (!containsRad(report.getHeadResponse())) {
            throw new IllegalStateException("Faqat mudir rad etgan rejani o'chirish mumkin");
        }
        deleteStoredFiles(report);
        repository.delete(report);
    }

    public void deleteRejectedSubmittedReportByTeacher(Long id, User teacher) {
        TeachingWorkReport report = findTeacherOwnedReport(id, teacher);
        if (!containsRad(report.getReportResponse())) {
            throw new IllegalStateException("Faqat mudir rad etgan hisobotni o'chirish mumkin");
        }
        deleteAmaldaFiles(report);
        report.setAmaldaFiles(null);
        report.setReportResponse(null);
        repository.save(report);
    }

    private TeachingWorkReport findTeacherOwnedReport(Long id, User teacher) {
        TeachingWorkReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hisobot topilmadi"));
        if (report.getTeacher() == null
                || teacher == null
                || !report.getTeacher().getUsername().equals(teacher.getUsername())) {
            throw new IllegalStateException("Bu hisobotni o'chirishga ruxsat yo'q");
        }
        return report;
    }

    private boolean containsRad(String value) {
        return value != null && value.toLowerCase().contains("rad");
    }

    public static class SectionProgressSummary {
        private final String sectionKey;
        private final String sectionTitle;
        private final int progress;
        private final String status;
        private final String note;

        public SectionProgressSummary(String sectionKey, String sectionTitle, int progress, String status, String note) {
            this.sectionKey = sectionKey;
            this.sectionTitle = sectionTitle;
            this.progress = progress;
            this.status = status;
            this.note = note;
        }

        public String getSectionKey() { return sectionKey; }
        public String getSectionTitle() { return sectionTitle; }
        public int getProgress() { return progress; }
        public String getStatus() { return status; }
        public String getNote() { return note; }
    }

    private void deleteStoredFiles(TeachingWorkReport report) {
        if (report.getReportFilePath() != null && !report.getReportFilePath().isBlank()) {
            deleteIfExists(Paths.get(report.getReportFilePath()));
        }
        deleteAmaldaFiles(report);
    }

    private void deleteAmaldaFiles(TeachingWorkReport report) {
        for (AmaldaFileItem item : getAmaldaFileItems(report)) {
            if (item.isDownloadable() && item.getPath() != null && !item.getPath().isBlank()) {
                deleteIfExists(Paths.get(item.getPath()));
            }
        }
    }

    private void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    public TeachingWorkReportForm buildEmptyForm() {
        return new TeachingWorkReportForm();
    }

    public TeachingWorkReportForm buildFormFromReport(TeachingWorkReport report) {
        TeachingWorkReportForm form = new TeachingWorkReportForm();
        form.setSemester(report.getSemester());
        form.setAcademicYear(report.getAcademicYear());
        form.setWorkName(report.getWorkName());
        form.setFaculty(report.getFaculty());
        form.setGroupName(report.getGroupName());
        form.setStudentCount(report.getStudentCount());
        form.setMaruza(report.getMaruza());
        form.setMaruzaJami(report.getMaruzaJami());
        form.setAmaliyMashgulot(report.getAmaliyMashgulot());
        form.setAmaliyMashgulotJami(report.getAmaliyMashgulotJami());
        form.setLaboratoriyaIshi(report.getLaboratoriyaIshi());
        form.setLaboratoriyaIshiJami(report.getLaboratoriyaIshiJami());
        form.setMaslahat(report.getMaslahat());
        form.setMaslahatJami(report.getMaslahatJami());
        form.setNazorat(report.getNazorat());
        form.setNazoratJami(report.getNazoratJami());
        form.setTaqrizlar(report.getTaqrizlar());
        form.setTaqrizlarJami(report.getTaqrizlarJami());
        form.setKursIshi(report.getKursIshi());
        form.setKursIshiJami(report.getKursIshiJami());
        form.setBitiruvIshi(report.getBitiruvIshi());
        form.setBitiruvIshiJami(report.getBitiruvIshiJami());
        form.setDakBmiRahbarligi(report.getDakBmiRahbarligi());
        form.setDakBmiRahbarligiJami(report.getDakBmiRahbarligiJami());
        form.setAmaliyot(report.getAmaliyot());
        form.setAmaliyotJami(report.getAmaliyotJami());
        form.setIti(report.getIti());
        form.setItiJami(report.getItiJami());
        form.setBmiGaTaqriz(report.getBmiGaTaqriz());
        form.setBmiGaTaqrizJami(report.getBmiGaTaqrizJami());
        form.setQaytaTopshirish(report.getQaytaTopshirish());
        form.setQaytaTopshirishJami(report.getQaytaTopshirishJami());
        form.setRatingBall(report.getRatingBall());
        form.setRatingBallJami(report.getRatingBallJami());
        return form;
    }

    public MethodicalWorkReportForm buildMethodicalFormFromReport(TeachingWorkReport report) {
        MethodicalWorkReportForm form = new MethodicalWorkReportForm();
        form.setBajariladiganIshlar(displayRows(report.getMethodicalBajariladiganIshlar()));
        form.setMuddat(displayRows(report.getMethodicalMuddat()));
        form.setIshlarHajmi(displayRows(report.getMethodicalIshlarHajmi()));
        form.setRejadanTashqariIshlar(displayRows(report.getMethodicalRejadanTashqariIshlar()));
        form.setRatingBall(displayRows(report.getMethodicalRatingBall()));
        return form;
    }

    public ResearchWorkReportForm buildResearchFormFromReport(TeachingWorkReport report) {
        ResearchWorkReportForm form = new ResearchWorkReportForm();
        form.setBajariladiganIshlar(displayRows(report.getResearchBajariladiganIshlar()));
        form.setMuddat(displayRows(report.getResearchMuddat()));
        form.setIjroBelgisi(displayRows(report.getResearchIjroBelgisi()));
        form.setIshHajmi(displayRows(report.getResearchIshHajmi()));
        form.setRejadanTashqariIshlar(displayRows(report.getResearchRejadanTashqariIshlar()));
        form.setRatingBall(displayRows(report.getResearchRatingBall()));
        return form;
    }

    private String displayRows(String value) {
        return value == null ? null : value.replace(ROW_DELIMITER, "\n\n");
    }

    public MentorshipWorkReportForm buildMentorshipFormFromReport(TeachingWorkReport report) {
        MentorshipWorkReportForm form = new MentorshipWorkReportForm();
        form.setBajariladiganIshlar(displayRows(report.getMentorshipBajariladiganIshlar()));
        form.setMuddat(displayRows(report.getMentorshipMuddat()));
        form.setOtkazishJoyi(displayRows(report.getMentorshipOtkazishJoyi()));
        form.setIshHajmi(displayRows(report.getMentorshipIshHajmi()));
        form.setRejadanTashqariIshlar(displayRows(report.getMentorshipRejadanTashqariIshlar()));
        form.setRatingBall(displayRows(report.getMentorshipRatingBall()));
        return form;
    }

    public TeachingWorkReport findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hisobot topilmadi"));
    }

    public TeachingWorkReport ensureReportFile(Long id) {
        TeachingWorkReport report = findById(id);
        attachReportFile(report);
        repository.save(report);
        return report;
    }

    private void tryAttachReportFile(TeachingWorkReport report) {
        try {
            attachReportFile(report);
        } catch (RuntimeException exception) {
            report.setReportFileName(null);
            report.setReportFilePath(null);
        }
    }

    private void attachReportFile(TeachingWorkReport report) {
        try {
            Path reportsDir = Paths.get("reports");
            Files.createDirectories(reportsDir);

            String safeTeacher = report.getTeacher().getUsername().replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = "oquv_ishlari_" + safeTeacher + "_" + FILE_FORMAT.format(report.getCreatedAt()) + ".docx";
            Path filePath = reportsDir.resolve(fileName);
            Path payloadPath = reportsDir.resolve(fileName.replace(".docx", ".ini"));

            Files.writeString(payloadPath, buildPayload(report));
            runDocxGenerator(payloadPath, filePath);
            report.setReportFileName(fileName);
            report.setReportFilePath(filePath.toAbsolutePath().toString());
        } catch (IOException exception) {
            throw new IllegalStateException("Hisobot faylini yaratib bo'lmadi", exception);
        }
    }

    private void runDocxGenerator(Path payloadPath, Path filePath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                pythonPath,
                "scripts/generate_oquv_report.py",
                payloadPath.toAbsolutePath().toString(),
                filePath.toAbsolutePath().toString()
        );
        processBuilder.directory(new java.io.File("."));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        String output;
        try (InputStream inputStream = process.getInputStream()) {
            output = new String(inputStream.readAllBytes());
        }
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IllegalStateException("Word fayl yaratishda xatolik: " + output);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Word fayl yaratish to'xtab qoldi", exception);
        }
    }

    private String buildPayload(TeachingWorkReport report) {
        StringBuilder builder = new StringBuilder();
        builder.append("[meta]\n");
        appendConfig(builder, "academic_year", report.getAcademicYear());
        appendConfig(builder, "semester_text", report.getSemester() == null ? "" : report.getSemester().name().equals("KUZGI") ? "Kuzgi" : "Bahorgi");
        appendConfig(builder, "work_name", report.getWorkName());
        appendConfig(builder, "faculty", report.getFaculty());
        appendConfig(builder, "group_name", report.getGroupName());
        appendConfig(builder, "student_count", value(report.getStudentCount()));
        builder.append("\n[values]\n");
        appendTripleConfig(builder, "maruza", report.getMaruza(), report.getMaruzaJami(), report.getMaruzaAmalda());
        appendTripleConfig(builder, "amaliy_mashgulot", report.getAmaliyMashgulot(), report.getAmaliyMashgulotJami(), report.getAmaliyMashgulotAmalda());
        appendTripleConfig(builder, "laboratoriya_ishi", report.getLaboratoriyaIshi(), report.getLaboratoriyaIshiJami(), report.getLaboratoriyaIshiAmalda());
        appendTripleConfig(builder, "maslahat", report.getMaslahat(), report.getMaslahatJami(), report.getMaslahatAmalda());
        appendTripleConfig(builder, "nazorat", report.getNazorat(), report.getNazoratJami(), report.getNazoratAmalda());
        appendTripleConfig(builder, "taqrizlar", report.getTaqrizlar(), report.getTaqrizlarJami(), report.getTaqrizlarAmalda());
        appendTripleConfig(builder, "kurs_ishi", report.getKursIshi(), report.getKursIshiJami(), report.getKursIshiAmalda());
        appendTripleConfig(builder, "bitiruv_ishi", report.getBitiruvIshi(), report.getBitiruvIshiJami(), report.getBitiruvIshiAmalda());
        appendTripleConfig(builder, "dak_bmi_rahbarligi", report.getDakBmiRahbarligi(), report.getDakBmiRahbarligiJami(), report.getDakBmiRahbarligiAmalda());
        appendTripleConfig(builder, "amaliyot", report.getAmaliyot(), report.getAmaliyotJami(), report.getAmaliyotAmalda());
        appendTripleConfig(builder, "iti", report.getIti(), report.getItiJami(), report.getItiAmalda());
        appendTripleConfig(builder, "bmi_ga_taqriz", report.getBmiGaTaqriz(), report.getBmiGaTaqrizJami(), report.getBmiGaTaqrizAmalda());
        appendTripleConfig(builder, "qayta_topshirish", report.getQaytaTopshirish(), report.getQaytaTopshirishJami(), report.getQaytaTopshirishAmalda());
        appendTripleConfig(builder, "rating_ball", report.getRatingBall(), report.getRatingBallJami(), report.getRatingBallAmalda());
        return builder.toString();
    }

    private void appendTripleConfig(StringBuilder builder, String key, Integer main, Integer jami, Integer amalda) {
        appendConfig(builder, key, value(main));
        appendConfig(builder, key + "_jami", value(jami));
        appendConfig(builder, key + "_amalda", value(amalda));
    }

    private void appendConfig(StringBuilder builder, String key, String value) {
        builder.append(key)
                .append("=")
                .append(value == null ? "" : value.replace("\n", " ").replace("\r", " "))
                .append("\n");
    }

    private String value(Integer number) {
        return number == null ? "-" : number.toString();
    }
}
