package org.example.diplom_ishi_new.auth;

import jakarta.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.example.diplom_ishi_new.report.HeadResponseForm;
import org.example.diplom_ishi_new.report.MethodicalWorkReportForm;
import org.example.diplom_ishi_new.report.MentorshipWorkReportForm;
import org.example.diplom_ishi_new.report.ResearchWorkReportForm;
import org.example.diplom_ishi_new.report.TeachingWorkReport;
import org.example.diplom_ishi_new.report.TeachingWorkReportForm;
import org.example.diplom_ishi_new.report.TeachingWorkReportService;
import org.example.diplom_ishi_new.report.TeachingWorkReportService.MethodicalRow;
import org.example.diplom_ishi_new.report.TeachingWorkReportService.MentorshipRow;
import org.example.diplom_ishi_new.report.TeachingWorkReportService.ResearchRow;
import org.example.diplom_ishi_new.subject.TeachingSubjectForm;
import org.example.diplom_ishi_new.subject.TeachingSubjectService;
import org.example.diplom_ishi_new.template.PlanTemplateService;
import org.example.diplom_ishi_new.template.PlanTemplateView;
import org.example.diplom_ishi_new.teacher.CreatedTeacherCredentials;
import org.example.diplom_ishi_new.teacher.TeacherEditForm;
import org.example.diplom_ishi_new.teacher.TeacherForm;
import org.example.diplom_ishi_new.teacher.TeacherProfileForm;
import org.example.diplom_ishi_new.teacher.TeacherProfileUpdateResult;
import org.example.diplom_ishi_new.user.Role;
import org.example.diplom_ishi_new.user.User;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AuthController {

    private final UserService userService;
    private final TeachingWorkReportService teachingWorkReportService;
    private final TeachingSubjectService teachingSubjectService;
    private final PlanTemplateService planTemplateService;

    public AuthController(UserService userService,
                          TeachingWorkReportService teachingWorkReportService,
                          TeachingSubjectService teachingSubjectService,
                          PlanTemplateService planTemplateService) {
        this.userService = userService;
        this.teachingWorkReportService = teachingWorkReportService;
        this.teachingSubjectService = teachingSubjectService;
        this.planTemplateService = planTemplateService;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("fullName", user.getFullName() == null ? user.getUsername() : user.getFullName());

        if (user.getRole() == Role.TEACHER) {
            addTeacherCommonAttributes(model, user);
            model.addAttribute("notifications", teachingWorkReportService.getTeacherUploadNotifications(user));
            return "teacher-home";
        }

        List<TeachingWorkReport> notifications = teachingWorkReportService.getHeadNotifications();
        model.addAttribute("notifications", notifications);
        model.addAttribute("teacherCount", userService.getTeachers().size());
        model.addAttribute("monitoringCount", teachingWorkReportService.getMonitoringReports().size());
        model.addAttribute("pendingCount", notifications.size());
        return "home";
    }

    @GetMapping("/teachers")
    public String teachers(Authentication authentication,
                           @RequestParam(required = false) String query,
                           Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("fullName", user.getFullName() == null ? user.getUsername() : user.getFullName());
        model.addAttribute("teacherForm", new TeacherForm());
        model.addAttribute("teachers", userService.getTeachers(query));
        model.addAttribute("query", query == null ? "" : query);
        return "teachers";
    }

    @GetMapping("/teachers/{id}/edit")
    public String editTeacher(@PathVariable Long id, Authentication authentication, Model model) {
        User head = userService.findByUsername(authentication.getName());
        User teacher = userService.findTeacherById(id);
        model.addAttribute("username", head.getUsername());
        model.addAttribute("fullName", head.getFullName() == null ? head.getUsername() : head.getFullName());
        model.addAttribute("editTeacher", teacher);
        model.addAttribute("teacherEditForm", userService.buildTeacherEditForm(teacher));
        return "teacher-edit";
    }

    @PostMapping("/teachers/{id}/edit")
    public String updateTeacherByHead(@PathVariable Long id,
                                      @Valid @ModelAttribute("teacherEditForm") TeacherEditForm form,
                                      BindingResult bindingResult,
                                      Authentication authentication,
                                      Model model) {
        User head = userService.findByUsername(authentication.getName());
        User teacher = userService.findTeacherById(id);
        model.addAttribute("username", head.getUsername());
        model.addAttribute("fullName", head.getFullName() == null ? head.getUsername() : head.getFullName());
        model.addAttribute("editTeacher", teacher);

        if (userService.usernameExists(form.getUsername())
                && !teacher.getUsername().equals(form.getUsername().trim())) {
            bindingResult.rejectValue("username", "username.exists", "Bu login band");
        }
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            try {
                userService.validateStrongPassword(form.getPassword());
            } catch (IllegalArgumentException exception) {
                bindingResult.rejectValue("password", "password.weak", exception.getMessage());
            }
        }

        if (bindingResult.hasErrors()) {
            return "teacher-edit";
        }

        userService.updateTeacherByHead(teacher, form);
        return "redirect:/teachers?updated";
    }

    @PostMapping("/teachers/{id}/delete")
    public String deleteTeacherByHead(@PathVariable Long id) {
        userService.deleteTeacherByHead(id);
        return "redirect:/teachers?deleted";
    }

    @GetMapping("/plans")
    public String plans(Authentication authentication,
                        @RequestParam(required = false) String q,
                        Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("teachers", userService.getTeachers(q));
        model.addAttribute("q", q == null ? "" : q);
        return "plans";
    }

    @GetMapping("/plans/teachers/{id}")
    public String teacherPlansArchive(@PathVariable Long id, Authentication authentication, Model model) {
        User teacher = userService.findTeacherById(id);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("teacher", teacher);
        return "plans-teacher";
    }

    @GetMapping("/plans/teachers/{id}/sections/{sectionKey}")
    public String teacherPlansArchiveSection(@PathVariable Long id,
                                             @PathVariable String sectionKey,
                                             Authentication authentication,
                                             Model model) {
        User teacher = userService.findTeacherById(id);
        String sectionName = sectionName(sectionKey);
        if (sectionName == null) {
            return "redirect:/plans/teachers/" + id;
        }
        model.addAttribute("username", authentication.getName());
        model.addAttribute("teacher", teacher);
        model.addAttribute("sectionKey", sectionKey);
        model.addAttribute("sectionTitle", sectionTitle(sectionKey));
        model.addAttribute("reports", teachingWorkReportService.getCompletedTeacherReportsBySection(teacher, sectionName));
        return "plans-teacher-section";
    }

    @GetMapping("/plans/create")
    public String createPlan(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("subjectForm", new TeachingSubjectForm());
        model.addAttribute("subjects", teachingSubjectService.getAll());
        return "plan-create";
    }

    @GetMapping("/plan-template")
    public String planTemplate(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "plan-template";
    }

    @GetMapping("/plan-template/{sectionKey}")
    public String planTemplateSection(@PathVariable String sectionKey,
                                      Authentication authentication,
                                      Model model) {
        if (!planTemplateService.isValidKey(sectionKey)) {
            return "redirect:/plan-template";
        }
        PlanTemplateView template = planTemplateService.getTemplate(sectionKey);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("sectionKey", sectionKey);
        model.addAttribute("sectionTitle", sectionTitle(sectionKey));
        model.addAttribute("template", template);
        return "plan-template-section";
    }

    @PostMapping("/plan-template/{sectionKey}")
    public String updatePlanTemplate(@PathVariable String sectionKey,
                                     @RequestParam("file") MultipartFile file) {
        if (!planTemplateService.isValidKey(sectionKey)) {
            return "redirect:/plan-template";
        }
        planTemplateService.saveTemplate(sectionKey, file);
        return "redirect:/plan-template/" + sectionKey + "?uploaded";
    }

    @PostMapping("/plan-template/{sectionKey}/reset")
    public String resetPlanTemplate(@PathVariable String sectionKey) {
        if (!planTemplateService.isValidKey(sectionKey)) {
            return "redirect:/plan-template";
        }
        planTemplateService.resetTemplate(sectionKey);
        return "redirect:/plan-template/" + sectionKey + "?default";
    }

    @GetMapping("/plan-template/{sectionKey}/file")
    public ResponseEntity<InputStreamResource> downloadPlanTemplate(@PathVariable String sectionKey) throws Exception {
        if (!planTemplateService.isValidKey(sectionKey)) {
            return ResponseEntity.notFound().build();
        }
        Path path = planTemplateService.getTemplatePath(sectionKey);
        if (path == null || !Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName().toString().replaceFirst("^" + sectionKey + "__", "") + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/plans/subjects")
    public String createSubject(@Valid @ModelAttribute("subjectForm") TeachingSubjectForm form,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("subjects", teachingSubjectService.getAll());

        if (bindingResult.hasErrors()) {
            return "plan-create";
        }

        teachingSubjectService.create(form);
        return "redirect:/plans/create?subjectCreated";
    }

    @GetMapping("/groups-subjects")
    public String groupsAndSubjects(Model model) {
        model.addAttribute("subjects", teachingSubjectService.getAll());
        model.addAttribute("groups", teachingSubjectService.getGroups());
        return "groups-subjects";
    }

    @PostMapping("/groups-subjects/subjects")
    public String createSimpleSubject(@RequestParam String name) {
        if (name == null || name.isBlank()) {
            return "redirect:/groups-subjects";
        }
        teachingSubjectService.createSubject(name);
        return "redirect:/groups-subjects?subjectCreated";
    }

    @PostMapping("/groups-subjects/subjects/{id}/edit")
    public String updateSimpleSubject(@PathVariable Long id, @RequestParam String name) {
        if (name == null || name.isBlank()) {
            return "redirect:/groups-subjects";
        }
        teachingSubjectService.updateSubject(id, name);
        return "redirect:/groups-subjects?subjectUpdated";
    }

    @PostMapping("/groups-subjects/subjects/{id}/delete")
    public String deleteSimpleSubject(@PathVariable Long id) {
        teachingSubjectService.deleteSubject(id);
        return "redirect:/groups-subjects?subjectDeleted";
    }

    @PostMapping("/groups-subjects/groups")
    public String createGroup(@RequestParam String name) {
        if (name == null || name.isBlank()) {
            return "redirect:/groups-subjects";
        }
        teachingSubjectService.createGroup(name);
        return "redirect:/groups-subjects?groupCreated";
    }

    @PostMapping("/groups-subjects/groups/{id}/edit")
    public String updateGroup(@PathVariable Long id, @RequestParam String name) {
        if (name == null || name.isBlank()) {
            return "redirect:/groups-subjects";
        }
        teachingSubjectService.updateGroup(id, name);
        return "redirect:/groups-subjects?groupUpdated";
    }

    @PostMapping("/groups-subjects/groups/{id}/delete")
    public String deleteGroup(@PathVariable Long id) {
        teachingSubjectService.deleteGroup(id);
        return "redirect:/groups-subjects?groupDeleted";
    }

    @GetMapping("/monitoring")
    public String monitoring(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("reports", teachingWorkReportService.getMonitoringReports());
        model.addAttribute("responseForm", new HeadResponseForm());
        return "monitoring";
    }

    @GetMapping("/teacher-panel")
    public String teacherPanel(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        model.addAttribute("notifications", teachingWorkReportService.getTeacherUploadNotifications(user));
        return "teacher-home";
    }

    @GetMapping("/teacher/profile")
    public String teacherProfile(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("profileForm", userService.buildTeacherProfileForm(user));
        model.addAttribute("currentPassword", user.getVisiblePassword() == null ? "" : user.getVisiblePassword());
        addTeacherCommonAttributes(model, user);
        return "teacher-profile";
    }

    @PostMapping("/teacher/profile")
    public String updateTeacherProfile(@Valid @ModelAttribute("profileForm") TeacherProfileForm form,
                                       BindingResult bindingResult,
                                       Authentication authentication,
                                       Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);

        if (form.getUsername() != null
                && !form.getUsername().isBlank()
                && userService.usernameExists(form.getUsername())
                && !user.getUsername().equals(form.getUsername().trim())) {
            bindingResult.rejectValue("username", "username.exists", "Bu login band");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentPassword", user.getVisiblePassword() == null ? "" : user.getVisiblePassword());
            return "teacher-profile";
        }

        TeacherProfileUpdateResult result = userService.updateTeacherProfile(user, form);
        if (result.isCredentialsChanged()) {
            return "redirect:/login?profileUpdated";
        }

        model.addAttribute("successMessage", "Ma'lumotlar muvaffaqiyatli yangilandi");
        User updatedUser = userService.findByUsername(result.getUsername());
        model.addAttribute("currentPassword", updatedUser.getVisiblePassword() == null ? "" : updatedUser.getVisiblePassword());
        model.addAttribute("profileForm", userService.buildTeacherProfileForm(updatedUser));
        return "teacher-profile";
    }

    @PostMapping("/teacher/profile/password")
    public String changeTeacherPassword(@RequestParam String oldPassword,
                                        @RequestParam String newPassword,
                                        @RequestParam String confirmPassword,
                                        Authentication authentication,
                                        Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        model.addAttribute("profileForm", userService.buildTeacherProfileForm(user));
        model.addAttribute("currentPassword", user.getVisiblePassword() == null ? "" : user.getVisiblePassword());
        model.addAttribute("showPasswordDialog", true);

        if (!userService.passwordMatches(user, oldPassword)) {
            model.addAttribute("passwordError", "Eski parol noto'g'ri");
            return "teacher-profile";
        }

        if (!Objects.equals(newPassword, confirmPassword)) {
            model.addAttribute("passwordError", "Yangi parollar bir xil emas");
            return "teacher-profile";
        }

        try {
            userService.changeTeacherPassword(user, newPassword);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("passwordError", exception.getMessage());
            return "teacher-profile";
        }
        User updatedUser = userService.findByUsername(user.getUsername());
        model.addAttribute("showPasswordDialog", false);
        model.addAttribute("passwordSuccess", "Parol muvaffaqiyatli yangilandi");
        model.addAttribute("currentPassword", updatedUser.getVisiblePassword() == null ? "" : updatedUser.getVisiblePassword());
        model.addAttribute("profileForm", userService.buildTeacherProfileForm(updatedUser));
        return "teacher-profile";
    }

    @GetMapping("/teacher/plans/create")
    public String teacherPlanCreate(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        return "teacher-plan-create";
    }

    @GetMapping("/teacher/plans/teaching")
    public String teacherTeachingPlan(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        model.addAttribute("reportForm", teachingWorkReportService.buildEmptyForm());
        model.addAttribute("template", planTemplateService.getTemplate("teaching"));
        model.addAttribute("groups", teachingSubjectService.getGroups());
        return "teacher-plan-teaching";
    }

    @GetMapping("/teacher/plans/methodical")
    public String teacherMethodicalPlan(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        model.addAttribute("methodicalForm", new MethodicalWorkReportForm());
        model.addAttribute("template", planTemplateService.getTemplate("methodical"));
        return "teacher-plan-methodical";
    }

    @PostMapping("/teacher/plans/methodical")
    public String submitMethodicalWorkReport(@ModelAttribute("methodicalForm") MethodicalWorkReportForm form,
                                             BindingResult bindingResult,
                                             Authentication authentication,
                                             Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        if (!hasCompleteDateRanges(form.getMuddat())) {
            bindingResult.rejectValue("muddat", "muddat.required", "Muddat kiritish majburiy");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("template", planTemplateService.getTemplate("methodical"));
            return "teacher-plan-methodical";
        }

        teachingWorkReportService.createMethodicalReport(user, form);
        return "redirect:/teacher/monitoring?submitted";
    }

    @GetMapping("/teacher/plans/research")
    public String teacherResearchPlan(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        model.addAttribute("researchForm", new ResearchWorkReportForm());
        model.addAttribute("template", planTemplateService.getTemplate("research"));
        return "teacher-plan-research";
    }

    @PostMapping("/teacher/plans/research")
    public String submitResearchWorkReport(@ModelAttribute("researchForm") ResearchWorkReportForm form,
                                           BindingResult bindingResult,
                                           Authentication authentication,
                                           Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        if (!hasCompleteDateRanges(form.getMuddat())) {
            bindingResult.rejectValue("muddat", "muddat.required", "Muddat kiritish majburiy");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("template", planTemplateService.getTemplate("research"));
            return "teacher-plan-research";
        }

        teachingWorkReportService.createResearchReport(user, form);
        return "redirect:/teacher/monitoring/research?submitted";
    }

    @GetMapping("/teacher/plans/mentorship")
    public String teacherMentorshipPlan(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        model.addAttribute("mentorshipForm", new MentorshipWorkReportForm());
        model.addAttribute("template", planTemplateService.getTemplate("mentorship"));
        return "teacher-plan-mentorship";
    }

    @PostMapping("/teacher/plans/mentorship")
    public String submitMentorshipWorkReport(@ModelAttribute("mentorshipForm") MentorshipWorkReportForm form,
                                             BindingResult bindingResult,
                                             Authentication authentication,
                                             Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        if (!hasCompleteDateRanges(form.getMuddat())) {
            bindingResult.rejectValue("muddat", "muddat.required", "Muddat kiritish majburiy");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("template", planTemplateService.getTemplate("mentorship"));
            return "teacher-plan-mentorship";
        }

        teachingWorkReportService.createMentorshipReport(user, form);
        return "redirect:/teacher/monitoring/mentorship?submitted";
    }

    @GetMapping("/teacher/monitoring")
    public String teacherMonitoring(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        return "teacher-monitoring";
    }

    @GetMapping("/teacher/monitoring/{sectionKey}")
    public String teacherMonitoringSection(@PathVariable String sectionKey, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        String sectionName = sectionName(sectionKey);
        if (sectionName == null) {
            return "redirect:/teacher/monitoring";
        }
        addTeacherCommonAttributes(model, user);
        model.addAttribute("sectionKey", sectionKey);
        model.addAttribute("sectionTitle", sectionTitle(sectionKey));
        model.addAttribute("reports", teachingWorkReportService.getTeacherMonitoringReportsBySection(user, sectionName));
        return "teacher-monitoring-section";
    }

    @GetMapping("/teacher/my-reports")
    public String teacherMyReports(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        return "teacher-my-reports";
    }

    @GetMapping("/teacher/my-reports/{sectionKey}")
    public String teacherMyReportsSection(@PathVariable String sectionKey, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        String sectionName = sectionName(sectionKey);
        if (sectionName == null) {
            return "redirect:/teacher/my-reports";
        }
        addTeacherCommonAttributes(model, user);
        model.addAttribute("sectionKey", sectionKey);
        model.addAttribute("sectionTitle", sectionTitle(sectionKey));
        model.addAttribute("reports", teachingWorkReportService.getReviewedTeacherSubmittedReportsBySection(user, sectionName));
        return "teacher-my-reports-section";
    }

    @PostMapping("/teacher/plans/create")
    public String submitTeachingWorkReport(@Valid @ModelAttribute("reportForm") TeachingWorkReportForm form,
                                           BindingResult bindingResult,
                                           Authentication authentication,
                                           Model model) {
        User user = userService.findByUsername(authentication.getName());
        addTeacherCommonAttributes(model, user);
        model.addAttribute("groups", teachingSubjectService.getGroups());

        if (bindingResult.hasErrors()) {
            model.addAttribute("template", planTemplateService.getTemplate("teaching"));
            return "teacher-plan-teaching";
        }

        teachingWorkReportService.createReports(user, form);
        return "redirect:/teacher/monitoring?submitted";
    }

    @PostMapping("/monitoring/{id}/approve")
    public String approveReport(@PathVariable Long id, @RequestParam(required = false) String response) {
        teachingWorkReportService.approve(id, response);
        return "redirect:/monitoring?approved";
    }

    @PostMapping("/monitoring/{id}/reject")
    public String rejectReport(@PathVariable Long id, @RequestParam(required = false) String response) {
        teachingWorkReportService.reject(id, response);
        return "redirect:/monitoring?rejected";
    }

    @PostMapping("/monitoring/{id}/report/approve")
    public String approveSubmittedReport(@PathVariable Long id, @RequestParam(required = false) String response) {
        if (response == null || response.isBlank()) {
            return "redirect:/monitoring?reportCommentRequired";
        }
        teachingWorkReportService.approveSubmittedReport(id, response);
        return "redirect:/monitoring?reportApproved";
    }

    @PostMapping("/monitoring/{id}/report/reject")
    public String rejectSubmittedReport(@PathVariable Long id, @RequestParam(required = false) String response) {
        if (response == null || response.isBlank()) {
            return "redirect:/monitoring?reportCommentRequired";
        }
        teachingWorkReportService.rejectSubmittedReport(id, response);
        return "redirect:/monitoring?reportRejected";
    }

    @GetMapping("/teacher/reports/{id}/amalda")
    public String teacherTeachingAmalda(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);
        if (!report.getTeacher().getUsername().equals(user.getUsername())) {
            return "redirect:/teacher/monitoring";
        }
        addTeacherCommonAttributes(model, user);
        model.addAttribute("report", report);
        model.addAttribute("reportForm", teachingWorkReportService.buildFormFromReport(report));
        return "teacher-report-amalda";
    }

    @PostMapping("/teacher/reports/{id}/amalda")
    public String submitTeachingAmalda(@PathVariable Long id,
                                       @ModelAttribute("reportForm") TeachingWorkReportForm form,
                                       BindingResult bindingResult,
                                       Authentication authentication,
                                       Model model) {
        User user = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);
        if (!report.getTeacher().getUsername().equals(user.getUsername())) {
            return "redirect:/teacher/monitoring";
        }
        validateRequiredAmaldaFiles(report, form, bindingResult);
        if (bindingResult.hasErrors()) {
            addTeacherCommonAttributes(model, user);
            model.addAttribute("report", report);
            return "teacher-report-amalda";
        }
        teachingWorkReportService.attachTeachingAmaldaFiles(id, form);
        return "redirect:/teacher/monitoring/teaching?filesUploaded";
    }

    @GetMapping("/teacher/reports/{id}/methodical-amalda")
    public String teacherMethodicalAmalda(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);
        if (!report.getTeacher().getUsername().equals(user.getUsername())) {
            return "redirect:/teacher/monitoring";
        }
        addTeacherCommonAttributes(model, user);
        model.addAttribute("report", report);
        model.addAttribute("methodicalForm", teachingWorkReportService.buildMethodicalFormFromReport(report));
        model.addAttribute("methodicalRows", teachingWorkReportService.getMethodicalRows(report));
        model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
        model.addAttribute("deadlineExpired", teachingWorkReportService.isReportDeadlineExpired(report));
        return "teacher-report-methodical-amalda";
    }

    @PostMapping("/teacher/reports/{id}/methodical-amalda")
    public String submitMethodicalAmalda(@PathVariable Long id,
                                         @ModelAttribute("methodicalForm") MethodicalWorkReportForm form,
                                         BindingResult bindingResult,
                                         Authentication authentication,
                                         Model model) {
        User user = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);
        if (!report.getTeacher().getUsername().equals(user.getUsername())) {
            return "redirect:/teacher/monitoring";
        }
        if (teachingWorkReportService.isReportDeadlineExpired(report)) {
            addTeacherCommonAttributes(model, user);
            model.addAttribute("report", report);
            model.addAttribute("methodicalForm", teachingWorkReportService.buildMethodicalFormFromReport(report));
            model.addAttribute("methodicalRows", teachingWorkReportService.getMethodicalRows(report));
            model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
            model.addAttribute("deadlineExpired", true);
            model.addAttribute("deadlineError", "Hisobot yuborish muddati tugagan");
            return "teacher-report-methodical-amalda";
        }
        validateRequiredMethodicalFiles(report, form, bindingResult);
        if (bindingResult.hasErrors()) {
            addTeacherCommonAttributes(model, user);
            model.addAttribute("report", report);
            model.addAttribute("methodicalRows", teachingWorkReportService.getMethodicalRows(report));
            model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
            model.addAttribute("deadlineExpired", teachingWorkReportService.isReportDeadlineExpired(report));
            return "teacher-report-methodical-amalda";
        }
        teachingWorkReportService.attachMethodicalAmaldaFiles(id, form);
        return "redirect:/teacher/monitoring/methodical?filesUploaded";
    }

    @GetMapping("/teacher/reports/{id}/research-amalda")
    public String teacherResearchAmalda(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);
        if (!report.getTeacher().getUsername().equals(user.getUsername())) {
            return "redirect:/teacher/monitoring";
        }
        addTeacherCommonAttributes(model, user);
        model.addAttribute("report", report);
        model.addAttribute("researchForm", teachingWorkReportService.buildResearchFormFromReport(report));
        model.addAttribute("researchRows", teachingWorkReportService.getResearchRows(report));
        model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
        model.addAttribute("deadlineExpired", teachingWorkReportService.isReportDeadlineExpired(report));
        return "teacher-report-research-amalda";
    }

    @PostMapping("/teacher/reports/{id}/research-amalda")
    public String submitResearchAmalda(@PathVariable Long id,
                                       @ModelAttribute("researchForm") ResearchWorkReportForm form,
                                       BindingResult bindingResult,
                                       Authentication authentication,
                                       Model model) {
        User user = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);
        if (!report.getTeacher().getUsername().equals(user.getUsername())) {
            return "redirect:/teacher/monitoring";
        }
        if (teachingWorkReportService.isReportDeadlineExpired(report)) {
            addTeacherCommonAttributes(model, user);
            model.addAttribute("report", report);
            model.addAttribute("researchForm", teachingWorkReportService.buildResearchFormFromReport(report));
            model.addAttribute("researchRows", teachingWorkReportService.getResearchRows(report));
            model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
            model.addAttribute("deadlineExpired", true);
            model.addAttribute("deadlineError", "Hisobot yuborish muddati tugagan");
            return "teacher-report-research-amalda";
        }
        validateRequiredResearchFiles(report, form, bindingResult);
        if (bindingResult.hasErrors()) {
            addTeacherCommonAttributes(model, user);
            model.addAttribute("report", report);
            model.addAttribute("researchRows", teachingWorkReportService.getResearchRows(report));
            model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
            model.addAttribute("deadlineExpired", teachingWorkReportService.isReportDeadlineExpired(report));
            return "teacher-report-research-amalda";
        }
        teachingWorkReportService.attachResearchAmaldaFiles(id, form);
        return "redirect:/teacher/monitoring/research?filesUploaded";
    }

    @GetMapping("/teacher/reports/{id}/mentorship-amalda")
    public String teacherMentorshipAmalda(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);
        if (!report.getTeacher().getUsername().equals(user.getUsername())) {
            return "redirect:/teacher/monitoring";
        }
        addTeacherCommonAttributes(model, user);
        model.addAttribute("report", report);
        model.addAttribute("mentorshipForm", teachingWorkReportService.buildMentorshipFormFromReport(report));
        model.addAttribute("mentorshipRows", teachingWorkReportService.getMentorshipRows(report));
        model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
        model.addAttribute("deadlineExpired", teachingWorkReportService.isReportDeadlineExpired(report));
        return "teacher-report-mentorship-amalda";
    }

    @PostMapping("/teacher/reports/{id}/mentorship-amalda")
    public String submitMentorshipAmalda(@PathVariable Long id,
                                         @ModelAttribute("mentorshipForm") MentorshipWorkReportForm form,
                                         BindingResult bindingResult,
                                         Authentication authentication,
                                         Model model) {
        User user = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);
        if (!report.getTeacher().getUsername().equals(user.getUsername())) {
            return "redirect:/teacher/monitoring";
        }
        if (teachingWorkReportService.isReportDeadlineExpired(report)) {
            addTeacherCommonAttributes(model, user);
            model.addAttribute("report", report);
            model.addAttribute("mentorshipForm", teachingWorkReportService.buildMentorshipFormFromReport(report));
            model.addAttribute("mentorshipRows", teachingWorkReportService.getMentorshipRows(report));
            model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
            model.addAttribute("deadlineExpired", true);
            model.addAttribute("deadlineError", "Hisobot yuborish muddati tugagan");
            return "teacher-report-mentorship-amalda";
        }
        validateRequiredMentorshipFiles(report, form, bindingResult);
        if (bindingResult.hasErrors()) {
            addTeacherCommonAttributes(model, user);
            model.addAttribute("report", report);
            model.addAttribute("mentorshipRows", teachingWorkReportService.getMentorshipRows(report));
            model.addAttribute("reportDeadline", teachingWorkReportService.getReportDeadline(report));
            model.addAttribute("deadlineExpired", teachingWorkReportService.isReportDeadlineExpired(report));
            return "teacher-report-mentorship-amalda";
        }
        teachingWorkReportService.attachMentorshipAmaldaFiles(id, form);
        return "redirect:/teacher/monitoring/mentorship?filesUploaded";
    }

    @PostMapping("/plans/reports/{id}/delete")
    public String deleteReviewedReport(@PathVariable Long id,
                                       @RequestParam(required = false) Long teacherId,
                                       @RequestParam(required = false) String sectionKey,
                                       Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        if (currentUser.getRole() != Role.HEAD) {
            return "redirect:/";
        }
        teachingWorkReportService.deleteReviewedReport(id);
        if (teacherId != null && sectionKey != null && !sectionKey.isBlank()) {
            return "redirect:/plans/teachers/" + teacherId + "/sections/" + sectionKey + "?deleted";
        }
        if (teacherId != null) {
            return "redirect:/plans/teachers/" + teacherId + "?deleted";
        }
        return "redirect:/plans?deleted";
    }

    @PostMapping("/teacher/monitoring/{id}/delete-rejected-plan")
    public String deleteRejectedPlanByTeacher(@PathVariable Long id,
                                              @RequestParam(required = false) String sectionKey,
                                              Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        teachingWorkReportService.deleteRejectedPlanByTeacher(id, currentUser);
        if (sectionKey != null && !sectionKey.isBlank()) {
            return "redirect:/teacher/monitoring/" + sectionKey + "?deleted";
        }
        return "redirect:/teacher/monitoring?deleted";
    }

    @PostMapping("/teacher/my-reports/{id}/delete-rejected")
    public String deleteRejectedSubmittedReportByTeacher(@PathVariable Long id,
                                                         @RequestParam(required = false) String sectionKey,
                                                         Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        teachingWorkReportService.deleteRejectedSubmittedReportByTeacher(id, currentUser);
        if (sectionKey != null && !sectionKey.isBlank()) {
            return "redirect:/teacher/my-reports/" + sectionKey + "?deleted";
        }
        return "redirect:/teacher/my-reports?deleted";
    }

    private String sectionName(String sectionKey) {
        return switch (sectionKey) {
            case "teaching" -> "O'quv ishlari";
            case "methodical" -> "Ilmiy-uslubiy ishlar";
            case "research" -> "Ilmiy-tadqiqot ishlari";
            case "mentorship" -> "Ustoz-shogird ishlari";
            default -> null;
        };
    }

    private String sectionTitle(String sectionKey) {
        return switch (sectionKey) {
            case "teaching" -> "O'quv ishlari";
            case "methodical" -> "Ilmiy-uslubiy ishlar";
            case "research" -> "Ilmiy-tadqiqot ishlari";
            case "mentorship" -> "Ustoz-shogird ishlari";
            default -> "Hisobotlar";
        };
    }

    @GetMapping("/reports/{id}/file")
    public ResponseEntity<InputStreamResource> openReportFile(@PathVariable Long id, Authentication authentication) throws Exception {
        User currentUser = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.ensureReportFile(id);

        boolean allowed = currentUser.getRole() == Role.HEAD
                || report.getTeacher().getUsername().equals(currentUser.getUsername());

        if (!allowed) {
            return ResponseEntity.status(403).build();
        }

        Path path = Path.of(report.getReportFilePath());
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getReportFileName() + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(resource);
    }

    @GetMapping("/reports/{id}/view")
    public String viewReport(@PathVariable Long id,
                             @RequestParam(defaultValue = "true") boolean files,
                             Authentication authentication,
                             Model model) {
        User currentUser = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);

        boolean allowed = currentUser.getRole() == Role.HEAD
                || report.getTeacher().getUsername().equals(currentUser.getUsername());

        if (!allowed) {
            return "redirect:/";
        }

        model.addAttribute("report", report);
        model.addAttribute("amaldaFiles", files ? teachingWorkReportService.getAmaldaFileItems(report) : java.util.List.of());
        model.addAttribute("amaldaFileMap", files ? teachingWorkReportService.getAmaldaFileMap(report) : java.util.Map.of());
        List<MethodicalRow> methodicalRows = teachingWorkReportService.getMethodicalRows(report);
        List<ResearchRow> researchRows = teachingWorkReportService.getResearchRows(report);
        List<MentorshipRow> mentorshipRows = teachingWorkReportService.getMentorshipRows(report);
        model.addAttribute("methodicalRows", methodicalRows);
        model.addAttribute("methodicalTotals", teachingWorkReportService.getMethodicalTotals(methodicalRows));
        model.addAttribute("researchRows", researchRows);
        model.addAttribute("researchTotals", teachingWorkReportService.getResearchTotals(researchRows));
        model.addAttribute("mentorshipRows", mentorshipRows);
        model.addAttribute("mentorshipTotals", teachingWorkReportService.getMentorshipTotals(mentorshipRows));
        if ("Ilmiy-uslubiy ishlar".equals(report.getSection())) {
            return "report-view-methodical";
        }
        if ("Ilmiy-tadqiqot ishlari".equals(report.getSection())) {
            return "report-view-research";
        }
        if ("Ustoz-shogird ishlari".equals(report.getSection())) {
            return "report-view-mentorship";
        }
        return "report-view";
    }

    @GetMapping("/reports/{id}/amalda-files/{fileIndex}")
    public ResponseEntity<InputStreamResource> downloadAmaldaFile(@PathVariable Long id,
                                                                  @PathVariable int fileIndex,
                                                                  Authentication authentication) throws Exception {
        User currentUser = userService.findByUsername(authentication.getName());
        TeachingWorkReport report = teachingWorkReportService.findById(id);

        boolean allowed = currentUser.getRole() == Role.HEAD
                || report.getTeacher().getUsername().equals(currentUser.getUsername());

        if (!allowed) {
            return ResponseEntity.status(403).build();
        }

        Path path = teachingWorkReportService.getAmaldaFilePath(report, fileIndex);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult) {
        if (form.getPassword() != null
                && form.getConfirmPassword() != null
                && !Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Parollar bir xil emas");
        }

        if (form.getUsername() != null && !form.getUsername().isBlank() && userService.usernameExists(form.getUsername())) {
            bindingResult.rejectValue("username", "username.exists", "Bu login band");
        }

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        try {
            userService.register(form);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("password", "password.weak", exception.getMessage());
            return "signup";
        }
        return "redirect:/login?registered";
    }

    @PostMapping("/teachers")
    public String createTeacher(@Valid @ModelAttribute("teacherForm") TeacherForm form,
                                BindingResult bindingResult,
                                Authentication authentication,
                                Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("fullName", user.getFullName() == null ? user.getUsername() : user.getFullName());
        model.addAttribute("teachers", userService.getTeachers());

        if (bindingResult.hasErrors()) {
            model.addAttribute("showTeacherForm", true);
            return "teachers";
        }

        CreatedTeacherCredentials credentials = userService.createTeacher(form);
        model.addAttribute("createdCredentials", credentials);
        model.addAttribute("teacherForm", new TeacherForm());
        model.addAttribute("teachers", userService.getTeachers());
        return "teachers";
    }

    private void addTeacherCommonAttributes(Model model, User user) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("fullName", user.getFullName() == null ? user.getUsername() : user.getFullName());
        model.addAttribute("department", user.getDepartment());
        model.addAttribute("position", user.getPosition());
        model.addAttribute("phoneNumber", user.getPhoneNumber());
        model.addAttribute("sectionProgress", teachingWorkReportService.getTeacherSectionProgress(user));
        model.addAttribute("averageProgress", teachingWorkReportService.getTeacherAverageProgress(user));
    }

    private void validateRequiredAmaldaFiles(TeachingWorkReportForm form, BindingResult bindingResult) {
        rejectMissingFile(form.getMaruza(), form.getMaruzaAmaldaFile(), bindingResult, "maruzaAmaldaFile");
        rejectMissingFile(form.getAmaliyMashgulot(), form.getAmaliyMashgulotAmaldaFile(), bindingResult, "amaliyMashgulotAmaldaFile");
        rejectMissingFile(form.getLaboratoriyaIshi(), form.getLaboratoriyaIshiAmaldaFile(), bindingResult, "laboratoriyaIshiAmaldaFile");
        rejectMissingFile(form.getMaslahat(), form.getMaslahatAmaldaFile(), bindingResult, "maslahatAmaldaFile");
        rejectMissingFile(form.getNazorat(), form.getNazoratAmaldaFile(), bindingResult, "nazoratAmaldaFile");
        rejectMissingFile(form.getTaqrizlar(), form.getTaqrizlarAmaldaFile(), bindingResult, "taqrizlarAmaldaFile");
        rejectMissingFile(form.getKursIshi(), form.getKursIshiAmaldaFile(), bindingResult, "kursIshiAmaldaFile");
        rejectMissingFile(form.getBitiruvIshi(), form.getBitiruvIshiAmaldaFile(), bindingResult, "bitiruvIshiAmaldaFile");
        rejectMissingFile(form.getDakBmiRahbarligi(), form.getDakBmiRahbarligiAmaldaFile(), bindingResult, "dakBmiRahbarligiAmaldaFile");
        rejectMissingFile(form.getAmaliyot(), form.getAmaliyotAmaldaFile(), bindingResult, "amaliyotAmaldaFile");
        rejectMissingFile(form.getIti(), form.getItiAmaldaFile(), bindingResult, "itiAmaldaFile");
        rejectMissingFile(form.getBmiGaTaqriz(), form.getBmiGaTaqrizAmaldaFile(), bindingResult, "bmiGaTaqrizAmaldaFile");
        rejectMissingFile(form.getQaytaTopshirish(), form.getQaytaTopshirishAmaldaFile(), bindingResult, "qaytaTopshirishAmaldaFile");
        rejectMissingFile(form.getRatingBall(), form.getRatingBallAmaldaFile(), bindingResult, "ratingBallAmaldaFile");
    }

    private void validateRequiredAmaldaFiles(TeachingWorkReport report, TeachingWorkReportForm form, BindingResult bindingResult) {
        rejectMissingFile(report.getMaruza(), form.getMaruzaAmaldaFile(), bindingResult, "maruzaAmaldaFile");
        rejectMissingFile(report.getAmaliyMashgulot(), form.getAmaliyMashgulotAmaldaFile(), bindingResult, "amaliyMashgulotAmaldaFile");
        rejectMissingFile(report.getLaboratoriyaIshi(), form.getLaboratoriyaIshiAmaldaFile(), bindingResult, "laboratoriyaIshiAmaldaFile");
        rejectMissingFile(report.getMaslahat(), form.getMaslahatAmaldaFile(), bindingResult, "maslahatAmaldaFile");
        rejectMissingFile(report.getNazorat(), form.getNazoratAmaldaFile(), bindingResult, "nazoratAmaldaFile");
        rejectMissingFile(report.getTaqrizlar(), form.getTaqrizlarAmaldaFile(), bindingResult, "taqrizlarAmaldaFile");
        rejectMissingFile(report.getKursIshi(), form.getKursIshiAmaldaFile(), bindingResult, "kursIshiAmaldaFile");
        rejectMissingFile(report.getBitiruvIshi(), form.getBitiruvIshiAmaldaFile(), bindingResult, "bitiruvIshiAmaldaFile");
        rejectMissingFile(report.getDakBmiRahbarligi(), form.getDakBmiRahbarligiAmaldaFile(), bindingResult, "dakBmiRahbarligiAmaldaFile");
        rejectMissingFile(report.getAmaliyot(), form.getAmaliyotAmaldaFile(), bindingResult, "amaliyotAmaldaFile");
        rejectMissingFile(report.getIti(), form.getItiAmaldaFile(), bindingResult, "itiAmaldaFile");
        rejectMissingFile(report.getBmiGaTaqriz(), form.getBmiGaTaqrizAmaldaFile(), bindingResult, "bmiGaTaqrizAmaldaFile");
        rejectMissingFile(report.getQaytaTopshirish(), form.getQaytaTopshirishAmaldaFile(), bindingResult, "qaytaTopshirishAmaldaFile");
        rejectMissingFile(report.getRatingBall(), form.getRatingBallAmaldaFile(), bindingResult, "ratingBallAmaldaFile");
    }

    private void rejectMissingFile(Integer value, MultipartFile file, BindingResult bindingResult, String fieldName) {
        if (value != null && (file == null || file.isEmpty())) {
            bindingResult.rejectValue(fieldName, "file.required", "Qiymat kiritilgan bo'lsa, fayl yuklash majburiy");
        }
    }

    private void validateRequiredMethodicalFiles(MethodicalWorkReportForm form, BindingResult bindingResult) {
        rejectMissingFile(form.getMuddat(), form.getMuddatFile(), bindingResult, "muddatFile");
        rejectMissingFile(form.getIshlarHajmi(), form.getIshlarHajmiFile(), bindingResult, "ishlarHajmiFile");
        rejectMissingFile(form.getRejadanTashqariIshlar(), form.getRejadanTashqariIshlarFile(), bindingResult, "rejadanTashqariIshlarFile");
    }

    private void validateRequiredMethodicalFiles(TeachingWorkReport report, MethodicalWorkReportForm form, BindingResult bindingResult) {
        rejectMissingFilesForRows(report.getMethodicalBajariladiganIshlar(), form.getHisobotFiles(), null, bindingResult, "hisobotFiles");
    }

    private void validateRequiredResearchFiles(TeachingWorkReport report, ResearchWorkReportForm form, BindingResult bindingResult) {
        rejectMissingFilesForRows(report.getResearchBajariladiganIshlar(), form.getHisobotFiles(), null, bindingResult, "hisobotFiles");
    }

    private void validateRequiredMentorshipFiles(TeachingWorkReport report, MentorshipWorkReportForm form, BindingResult bindingResult) {
        rejectMissingFilesForRows(report.getMentorshipBajariladiganIshlar(), form.getHisobotFiles(), null, bindingResult, "hisobotFiles");
    }

    private void rejectMissingFilesForRows(String values, List<MultipartFile> files, MultipartFile fallbackFile, BindingResult bindingResult, String fieldName) {
        String[] rows = values == null ? new String[0] : values.split("\\Q|||ROW|||\\E", -1);
        for (int i = 0; i < rows.length; i++) {
            if (rows[i] != null && !rows[i].isBlank()) {
                MultipartFile file = files == null || i >= files.size() ? null : files.get(i);
                if ((file == null || file.isEmpty()) && (fallbackFile == null || fallbackFile.isEmpty())) {
                    bindingResult.rejectValue(fieldName, "file.required", "Har bir ish uchun alohida fayl yuklash majburiy");
                    return;
                }
            }
        }
    }

    private void rejectMissingFile(String value, MultipartFile file, BindingResult bindingResult, String fieldName) {
        if (value != null && !value.isBlank() && (file == null || file.isEmpty())) {
            bindingResult.rejectValue(fieldName, "file.required", "Qiymat kiritilgan bo'lsa, fayl yuklash majburiy");
        }
    }

    private boolean hasCompleteDateRanges(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String[] rows = value.split("\\Q|||ROW|||\\E");
        for (String row : rows) {
            if (row == null || row.isBlank()) {
                return false;
            }
            String[] parts = row.split("\\s+-\\s+");
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                return false;
            }
        }
        return true;
    }
}
