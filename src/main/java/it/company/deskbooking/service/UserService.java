package it.company.deskbooking.service;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.exception.ResourceNotFoundException;
import it.company.deskbooking.model.User;
import it.company.deskbooking.model.Department;
import it.company.deskbooking.repository.UserRepository;
import it.company.deskbooking.repository.DepartmentRepository;
import it.company.deskbooking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Registrazione nuovo utente
     */
    @Transactional
    public LoginResponse register(CreateUserRequest request) {
        log.info("Registrazione nuovo utente: {}", request.getEmail());

        // Verifica se l'email esiste già
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email già registrata: " + request.getEmail());
        }

        // Verifica se l'employeeId esiste già
        if (request.getEmployeeId() != null && userRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID già registrato: " + request.getEmployeeId());
        }

        // Carica il dipartimento se specificato
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dipartimento non trovato"));
        }

        // Crea l'utente
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .employeeId(request.getEmployeeId())
                .password(passwordEncoder.encode(request.getPassword()))
                .department(department)
                .role(request.getRole() != null ? request.getRole() : User.UserRole.USER)
                .workType(request.getWorkType() != null ? request.getWorkType() : User.WorkType.STANDARD)
                .phoneNumber(request.getPhoneNumber())
                .active(true)
                .emailVerified(false) // Senza invio email, ma manteniamo il campo
                .build();

        user = userRepository.save(user);
        log.info("Utente registrato con successo: {} (ID: {})", user.getEmail(), user.getId());

        // Genera il token JWT
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        String token = jwtUtil.generateToken(extraClaims, userDetails);

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Login utente
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Tentativo di login per: {}", request.getEmail());

        // Autentica l'utente
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Carica l'utente
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        // Aggiorna last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Genera il token JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        String token = jwtUtil.generateToken(extraClaims, userDetails);

        log.info("Login effettuato con successo per: {}", user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Ottiene tutti gli utenti
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Ottiene utente per ID
     */
    @Transactional(readOnly = true)
    public UserDetailDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + id));
        return convertToDetailDTO(user);
    }

    /**
     * Ottiene utente per email
     */
    @Transactional(readOnly = true)
    public UserDetailDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con email: " + email));
        return convertToDetailDTO(user);
    }

    /**
     * Aggiorna utente
     */
    @Transactional
    public UserDetailDTO updateUser(Long id, UpdateUserRequest request) {
        log.info("Aggiornamento utente ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        // Aggiorna i campi
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dipartimento non trovato"));
            user.setDepartment(department);
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getWorkType() != null) {
            user.setWorkType(request.getWorkType());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        user = userRepository.save(user);
        log.info("Utente aggiornato: {}", user.getEmail());

        return convertToDetailDTO(user);
    }

    /**
     * Cambia password
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Cambio password per utente ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        // Verifica la vecchia password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password attuale non corretta");
        }

        // Imposta la nuova password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password cambiata con successo per: {}", user.getEmail());
    }

    /**
     * Elimina utente
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Eliminazione utente ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        userRepository.delete(user);
        log.info("Utente eliminato: {}", user.getEmail());
    }

    // ========== Metodi di conversione ==========

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .employeeId(user.getEmployeeId())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .role(user.getRole().name())
                .workType(user.getWorkType().name())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .active(user.getActive())
                .emailVerified(user.getEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private UserDetailDTO convertToDetailDTO(User user) {
        return UserDetailDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .employeeId(user.getEmployeeId())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .role(user.getRole().name())
                .workType(user.getWorkType().name())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .active(user.getActive())
                .emailVerified(user.getEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .totalBookings(user.getBookings() != null ? user.getBookings().size() : 0)
                .activeBookings(user.getBookings() != null ? 
                    (int) user.getBookings().stream()
                        .filter(b -> b.getStatus().name().equals("ACTIVE") || b.getStatus().name().equals("CHECKED_IN"))
                        .count() : 0)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
