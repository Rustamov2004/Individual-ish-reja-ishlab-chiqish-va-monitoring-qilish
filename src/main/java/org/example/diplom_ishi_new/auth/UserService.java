package org.example.diplom_ishi_new.auth;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.example.diplom_ishi_new.report.TeachingWorkReportRepository;
import org.example.diplom_ishi_new.user.User;
import org.example.diplom_ishi_new.user.UserRepository;
import org.example.diplom_ishi_new.user.Role;
import org.example.diplom_ishi_new.teacher.CreatedTeacherCredentials;
import org.example.diplom_ishi_new.teacher.TeacherEditForm;
import org.example.diplom_ishi_new.teacher.TeacherForm;
import org.example.diplom_ishi_new.teacher.TeacherProfileForm;
import org.example.diplom_ishi_new.teacher.TeacherProfileUpdateResult;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$%&*!";
    private static final int MIN_PASSWORD_LENGTH = 10;

    private final UserRepository userRepository;
    private final TeachingWorkReportRepository teachingWorkReportRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public UserService(UserRepository userRepository,
                       TeachingWorkReportRepository teachingWorkReportRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teachingWorkReportRepository = teachingWorkReportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegistrationForm form) {
        validateStrongPassword(form.getPassword());
        User user = new User(form.getUsername().trim(), passwordEncoder.encode(form.getPassword()), form.getUsername().trim(), Role.HEAD);
        userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username.trim());
    }

    public CreatedTeacherCredentials createTeacher(TeacherForm form) {
        String username = generateUniqueUsername(form.getFullName());
        String rawPassword = generatePassword();

        User user = new User(username, passwordEncoder.encode(rawPassword), form.getFullName().trim(), Role.TEACHER);
        user.setDepartment(form.getDepartment().trim());
        user.setPosition(form.getPosition().trim());
        user.setPhoneNumber(form.getPhoneNumber().trim());
        userRepository.save(user);

        return new CreatedTeacherCredentials(user.getFullName(), username, rawPassword);
    }

    public List<User> getTeachers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.TEACHER)
                .toList();
    }

    public List<User> getTeachers(String query) {
        List<User> teachers = getTeachers();
        if (query == null || query.isBlank()) {
            return teachers;
        }
        String normalizedQuery = query.trim().toLowerCase();
        return teachers.stream()
                .filter(user -> {
                    String fullName = user.getFullName() == null ? "" : user.getFullName();
                    String username = user.getUsername() == null ? "" : user.getUsername();
                    return fullName.toLowerCase().contains(normalizedQuery)
                            || username.toLowerCase().contains(normalizedQuery);
                })
                .toList();
    }

    public TeacherProfileForm buildTeacherProfileForm(User user) {
        TeacherProfileForm form = new TeacherProfileForm();
        form.setFullName(user.getFullName());
        form.setDepartment(user.getDepartment());
        form.setPosition(user.getPosition());
        form.setPhoneNumber(user.getPhoneNumber());
        form.setUsername(user.getUsername());
        return form;
    }

    public TeacherProfileUpdateResult updateTeacherProfile(User user, TeacherProfileForm form) {
        String newUsername = form.getUsername().trim();
        boolean usernameChanged = !user.getUsername().equals(newUsername);

        user.setFullName(form.getFullName().trim());
        user.setDepartment(form.getDepartment().trim());
        user.setPosition(form.getPosition().trim());
        user.setPhoneNumber(form.getPhoneNumber().trim());
        user.setUsername(newUsername);

        userRepository.save(user);
        return new TeacherProfileUpdateResult(usernameChanged, user.getUsername());
    }

    public boolean passwordMatches(User user, String rawPassword) {
        if (rawPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void changeTeacherPassword(User user, String newPassword) {
        String rawPassword = newPassword.trim();
        validateStrongPassword(rawPassword);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setVisiblePassword(null);
        userRepository.save(user);
    }

    public TeacherEditForm buildTeacherEditForm(User user) {
        TeacherEditForm form = new TeacherEditForm();
        form.setFullName(user.getFullName());
        form.setDepartment(user.getDepartment());
        form.setPosition(user.getPosition());
        form.setPhoneNumber(user.getPhoneNumber());
        form.setUsername(user.getUsername());
        form.setPassword("");
        return form;
    }

    public void updateTeacherByHead(User user, TeacherEditForm form) {
        user.setFullName(form.getFullName().trim());
        user.setDepartment(form.getDepartment().trim());
        user.setPosition(form.getPosition().trim());
        user.setPhoneNumber(form.getPhoneNumber().trim());
        user.setUsername(form.getUsername().trim());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            String rawPassword = form.getPassword().trim();
            validateStrongPassword(rawPassword);
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        user.setVisiblePassword(null);
        userRepository.save(user);
    }

    @Transactional
    public void deleteTeacherByHead(Long id) {
        User teacher = findTeacherById(id);
        teachingWorkReportRepository.deleteByTeacher(teacher);
        userRepository.delete(teacher);
    }

    public User findTeacherById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("O'qituvchi topilmadi"));
        if (user.getRole() != Role.TEACHER) {
            throw new UsernameNotFoundException("O'qituvchi topilmadi");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi"));

        Role role = user.getRole() == null ? Role.HEAD : user.getRole();

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(role.name())
                .build();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Foydalanuvchi topilmadi"));
    }

    private String generateUniqueUsername(String fullName) {
        String base = slugify(fullName);
        String candidate = base;
        int counter = 1;

        while (userRepository.existsByUsername(candidate)) {
            candidate = base + counter;
            counter++;
        }

        return candidate;
    }

    private String slugify(String value) {
        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");

        if (normalized.length() < 8) {
            normalized = (normalized + "teacher01").substring(0, 8);
        }

        return normalized;
    }

    private String generatePassword() {
        while (true) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                int index = secureRandom.nextInt(PASSWORD_CHARS.length());
                builder.append(PASSWORD_CHARS.charAt(index));
            }
            String password = builder.toString();
            try {
                validateStrongPassword(password);
                return password;
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void validateStrongPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Parol kiritish majburiy");
        }
        String value = password.trim();
        if (value.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Parol kamida 10 ta belgidan iborat bo'lsin");
        }
        boolean hasUpper = value.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = value.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = value.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = value.chars().anyMatch(ch -> "@#$%&*!".indexOf(ch) >= 0);
        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new IllegalArgumentException("Parolda katta harf, kichik harf, raqam va maxsus belgi bo'lishi shart");
        }
    }
}
