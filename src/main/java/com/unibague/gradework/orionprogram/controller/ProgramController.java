package com.unibague.gradework.orionprogram.controller;

import com.unibague.gradework.orionprogram.exception.ProgramExceptions;
import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;
import com.unibague.gradework.orionprogram.model.UserDTO;
import com.unibague.gradework.orionprogram.services.IProgramService;
import com.unibague.gradework.orionprogram.services.IUserService;
import com.unibague.gradework.orionprogram.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Enhanced REST Controller for Program management with authentication and authorization
 * Handles HTTP requests related to academic programs and educational areas
 */
@Slf4j
@RestController
@RequestMapping("/service/program")
public class ProgramController {

    @Autowired
    private IProgramService programService;

    @Autowired
    private IUserService userService;

    /**
     * Creates a new program
     * SECURITY: Only COORDINATORS and ADMINS can create programs
     */
    @PostMapping
    public ResponseEntity<?> createProgram(@Valid @RequestBody Program program) {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Check permissions - only coordinators and admins
            if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                log.warn("UNAUTHORIZED: User {} ({}) attempted to create program",
                        currentUser.getUserId(), currentUser.getRole());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "Only coordinators and administrators can create programs"
                        ));
            }

            log.info("Creating new program: {} by user: {} ({})",
                    program.getProgramName(), currentUser.getUserId(), currentUser.getRole());

            Program created = programService.createProgram(program);

            log.info("Program created successfully with ID: {} by user: {}",
                    created.getProgramId(), currentUser.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves all programs with optional search
     * SECURITY: All authenticated users can view programs (filtered by access)
     */
    @GetMapping
    public ResponseEntity<?> getAllPrograms(@RequestParam(required = false) String search) {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            List<Program> programs;

            if (search != null && !search.trim().isEmpty()) {
                log.debug("Searching programs with term: '{}' by user: {}", search, currentUser.getUserId());
                programs = programService.getPrograms(search);
            } else {
                log.debug("Retrieving all programs for user: {} ({})",
                        currentUser.getUserId(), currentUser.getRole());
                programs = programService.getPrograms();
            }

            // Filter programs based on user access (unless admin)
            if (!currentUser.isAdmin()) {
                programs = programs.stream()
                        .filter(program -> currentUser.hasAccessToProgram(program.getProgramId()) ||
                                currentUser.isCoordinator()) // Coordinators see all
                        .toList();

                log.debug("Filtered to {} programs for user access", programs.size());
            }

            log.debug("Found {} programs for user: {}", programs.size(), currentUser.getUserId());
            return ResponseEntity.ok(programs);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves simple program statistics
     * SECURITY: Only COORDINATORS and ADMINS can view statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getProgramStatistics() {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Check permissions
            if (!currentUser.isCoordinator() && !currentUser.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "Only coordinators and administrators can view statistics"
                        ));
            }

            log.debug("Retrieving program statistics for user: {} ({})",
                    currentUser.getUserId(), currentUser.getRole());

            IProgramService.ProgramStatistics stats = programService.getProgramStatistics();

            // Add audit info
            Map<String, Object> response = Map.of(
                    "statistics", stats,
                    "requestedBy", currentUser.getUserId(),
                    "requestedAt", java.time.LocalDateTime.now()
            );

            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves a program by ID
     * SECURITY: Users can only view programs they have access to (unless admin/coordinator)
     */
    @GetMapping("/{programId}")
    public ResponseEntity<?> getProgramById(@PathVariable String programId) {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Check access to specific program (unless admin/coordinator)
            if (!currentUser.isAdmin() && !currentUser.isCoordinator() &&
                    !currentUser.hasAccessToProgram(programId)) {

                log.warn("ACCESS DENIED: User {} attempted to access program {}",
                        currentUser.getUserId(), programId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "PROGRAM_ACCESS_DENIED",
                                "message", "You don't have access to this program"
                        ));
            }

            log.debug("Retrieving program by ID: {} for user: {}", programId, currentUser.getUserId());

            Program program = programService.getProgramById(programId)
                    .orElseThrow(() -> new ProgramExceptions.ProgramNotFoundException(programId));

            return ResponseEntity.ok(program);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves a program by name
     * SECURITY: Same access control as getProgramById
     */
    @GetMapping("/name/{programName}")
    public ResponseEntity<?> getProgramByName(@PathVariable String programName) {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            log.debug("Retrieving program by name: {} for user: {}", programName, currentUser.getUserId());

            Program program = programService.getProgramByName(programName)
                    .orElseThrow(() -> new ProgramExceptions.ProgramNotFoundException("name", programName));

            // Check access to the found program
            if (!currentUser.isAdmin() && !currentUser.isCoordinator() &&
                    !currentUser.hasAccessToProgram(program.getProgramId())) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "PROGRAM_ACCESS_DENIED",
                                "message", "You don't have access to this program"
                        ));
            }

            return ResponseEntity.ok(program);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Updates an existing program
     * SECURITY: Only ADMINS and COORDINATORS of the specific program can update
     */
    @PutMapping("/{programId}")
    public ResponseEntity<?> updateProgram(@PathVariable String programId, @RequestBody Program program) {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Check if program exists first
            Program existingProgram = programService.getProgramById(programId)
                    .orElseThrow(() -> new ProgramExceptions.ProgramNotFoundException(programId));

            // Check permissions - admin or coordinator with access to this program
            if (!currentUser.isAdmin() &&
                    (!currentUser.isCoordinator() || !currentUser.hasAccessToProgram(programId))) {

                log.warn("UPDATE DENIED: User {} ({}) attempted to update program {}",
                        currentUser.getUserId(), currentUser.getRole(), programId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "You don't have permission to update this program"
                        ));
            }

            log.info("Updating program with ID: {} by user: {} ({})",
                    programId, currentUser.getUserId(), currentUser.getRole());

            Program updated = programService.updateProgram(programId, program);

            log.info("Program updated successfully: {} by user: {}", programId, currentUser.getUserId());
            return ResponseEntity.ok(updated);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Deletes a program by ID
     * SECURITY: Only ADMINS can delete programs
     */
    @DeleteMapping("/{programId}")
    public ResponseEntity<?> deleteProgram(@PathVariable String programId) {
        try {
            // Require admin privileges
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            log.warn("DELETING PROGRAM: {} by admin user: {}", programId, currentUser.getUserId());

            programService.deleteProgram(programId);

            log.warn("Program deleted successfully: {} by admin: {}", programId, currentUser.getUserId());

            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can delete programs"
                    ));
        }
    }

    /**
     * Creates a new educational area for a program
     * SECURITY: Only coordinators of the program or admins can create areas
     */
    @PostMapping("/{programId}/area")
    public ResponseEntity<?> createEducationalArea(@PathVariable String programId,
                                                   @Valid @RequestBody EducationalArea area) {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Check permissions - admin or coordinator with access to this program
            if (!currentUser.isAdmin() &&
                    (!currentUser.isCoordinator() || !currentUser.hasAccessToProgram(programId))) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "You don't have permission to create areas in this program"
                        ));
            }

            log.info("Creating educational area '{}' for program: {} by user: {} ({})",
                    area.getName(), programId, currentUser.getUserId(), currentUser.getRole());

            Program updated = programService.createEducationalArea(area, programId);

            log.info("Educational area created successfully for program: {} by user: {}",
                    programId, currentUser.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED).body(updated);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves all educational areas for a program
     * SECURITY: Users need access to the program to view its areas
     */
    @GetMapping("/{programId}/area")
    public ResponseEntity<?> getEducationalAreas(@PathVariable String programId) {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Check access to program
            if (!currentUser.isAdmin() && !currentUser.isCoordinator() &&
                    !currentUser.hasAccessToProgram(programId)) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "PROGRAM_ACCESS_DENIED",
                                "message", "You don't have access to this program's areas"
                        ));
            }

            log.debug("Retrieving educational areas for program: {} by user: {}",
                    programId, currentUser.getUserId());

            List<EducationalArea> areas = programService.getEducationalAreas(programId);

            log.debug("Found {} educational areas for program: {}", areas.size(), programId);
            return ResponseEntity.ok(areas);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves a specific educational area by ID
     * SECURITY: Same access control as getEducationalAreas
     */
    @GetMapping("/{programId}/area/{areaId}")
    public ResponseEntity<?> getEducationalAreaById(@PathVariable String programId,
                                                    @PathVariable String areaId) {
        try {
            // Require authentication and program access
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            if (!currentUser.isAdmin() && !currentUser.isCoordinator() &&
                    !currentUser.hasAccessToProgram(programId)) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "PROGRAM_ACCESS_DENIED",
                                "message", "You don't have access to this program's areas"
                        ));
            }

            log.debug("Retrieving educational area {} for program: {} by user: {}",
                    areaId, programId, currentUser.getUserId());

            EducationalArea area = programService.getEducationalAreaById(programId, areaId)
                    .orElseThrow(() -> new ProgramExceptions.EducationalAreaNotFoundException(programId, areaId));

            return ResponseEntity.ok(area);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Retrieves the leader (user) of a specific educational area
     * SECURITY: Same access control as educational areas
     */
    @GetMapping("/{programId}/area/{areaId}/leader")
    public ResponseEntity<?> getEducationalAreaLeader(@PathVariable String programId,
                                                      @PathVariable String areaId) {
        try {
            // Require authentication and program access
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            if (!currentUser.isAdmin() && !currentUser.isCoordinator() &&
                    !currentUser.hasAccessToProgram(programId)) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "PROGRAM_ACCESS_DENIED",
                                "message", "You don't have access to this program's information"
                        ));
            }

            log.debug("Retrieving leader for educational area {} in program: {} by user: {}",
                    areaId, programId, currentUser.getUserId());

            // Get the educational area
            EducationalArea area = programService.getEducationalAreaById(programId, areaId)
                    .orElseThrow(() -> new ProgramExceptions.EducationalAreaNotFoundException(programId, areaId));

            // Check if leader is assigned
            if (area.getLeaderId() == null || area.getLeaderId().isBlank()) {
                throw new ProgramExceptions.InvalidProgramDataException("No leader assigned to educational area: " + areaId);
            }

            // Get leader user data from User Service
            UserDTO leader = userService.getUserById(area.getLeaderId())
                    .orElseThrow(() -> new ProgramExceptions.InvalidProgramDataException("Leader user not found with ID: " + area.getLeaderId()));

            log.debug("Found leader '{}' for educational area: {} requested by: {}",
                    leader.getName(), areaId, currentUser.getUserId());

            return ResponseEntity.ok(leader);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Updates an educational area
     * SECURITY: Only coordinators of the program or admins can update areas
     */
    @PutMapping("/{programId}/area/{areaId}")
    public ResponseEntity<?> updateEducationalArea(@PathVariable String programId,
                                                   @PathVariable String areaId,
                                                   @RequestBody EducationalArea area) {
        try {
            // Require authentication
            UserContext.AuthenticatedUser currentUser = UserContext.requireAuthentication();

            // Check permissions
            if (!currentUser.isAdmin() &&
                    (!currentUser.isCoordinator() || !currentUser.hasAccessToProgram(programId))) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "INSUFFICIENT_PERMISSIONS",
                                "message", "You don't have permission to update areas in this program"
                        ));
            }

            log.info("Updating educational area {} for program: {} by user: {} ({})",
                    areaId, programId, currentUser.getUserId(), currentUser.getRole());

            EducationalArea updated = programService.updateEducationalArea(programId, areaId, area);

            log.info("Educational area updated successfully: {} by user: {}", areaId, currentUser.getUserId());
            return ResponseEntity.ok(updated);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "AUTHENTICATION_REQUIRED", "message", e.getMessage()));
        }
    }

    /**
     * Deletes an educational area
     * SECURITY: Only admins can delete educational areas
     */
    @DeleteMapping("/{programId}/area/{areaId}")
    public ResponseEntity<?> deleteEducationalArea(@PathVariable String programId,
                                                   @PathVariable String areaId) {
        try {
            // Require admin privileges
            UserContext.requireAdmin();
            UserContext.AuthenticatedUser currentUser = UserContext.getCurrentUser().get();

            log.warn("DELETING EDUCATIONAL AREA: {} from program {} by admin: {}",
                    areaId, programId, currentUser.getUserId());

            programService.deleteEducationalArea(programId, areaId);

            log.warn("Educational area deleted successfully: {} by admin: {}",
                    areaId, currentUser.getUserId());

            return ResponseEntity.noContent().build();

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "ADMIN_REQUIRED",
                            "message", "Only administrators can delete educational areas"
                    ));
        }
    }
}