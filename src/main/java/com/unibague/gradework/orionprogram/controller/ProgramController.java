package com.unibague.gradework.orionprogram.controller;

import com.unibague.gradework.orionprogram.exception.ProgramExceptions;
import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;
import com.unibague.gradework.orionprogram.services.IProgramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Program management
 * Handles HTTP requests related to academic programs and educational areas
 */
@Slf4j
@RestController
@RequestMapping("/service/program")
public class ProgramController {

    @Autowired
    private IProgramService programService;

    /**
     * Creates a new program
     * @param program Program data to create
     * @return ResponseEntity with created program and HTTP 201
     */
    @PostMapping
    public ResponseEntity<Program> createProgram(@Valid @RequestBody Program program) {
        log.info("Creating new program: {}", program.getProgramName());
        Program created = programService.createProgram(program);
        log.info("Program created successfully with ID: {}", created.getProgramId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves all programs
     * @return ResponseEntity with list of programs and HTTP 200
     */
    @GetMapping
    public ResponseEntity<List<Program>> getAllPrograms() {
        log.debug("Retrieving all programs");
        List<Program> programs = programService.getPrograms();
        log.debug("Found {} programs", programs.size());
        return ResponseEntity.ok(programs);
    }

    /**
     * Retrieves a program by ID
     * @param programId Program identifier
     * @return ResponseEntity with program data and HTTP 200
     * @throws ProgramExceptions.ProgramNotFoundException if program not found
     */
    @GetMapping("/{programId}")
    public ResponseEntity<Program> getProgramById(@PathVariable String programId) {
        log.debug("Retrieving program by ID: {}", programId);
        Program program = programService.getProgramById(programId)
                .orElseThrow(() -> new ProgramExceptions.ProgramNotFoundException(programId));
        return ResponseEntity.ok(program);
    }

    /**
     * Retrieves a program by name
     * @param programName Program name
     * @return ResponseEntity with program data and HTTP 200
     * @throws ProgramExceptions.ProgramNotFoundException if program not found
     */
    @GetMapping("/name/{programName}")
    public ResponseEntity<Program> getProgramByName(@PathVariable String programName) {
        log.debug("Retrieving program by name: {}", programName);
        Program program = programService.getProgramByName(programName)
                .orElseThrow(() -> new ProgramExceptions.ProgramNotFoundException("name", programName));
        return ResponseEntity.ok(program);
    }

    /**
     * Updates an existing program
     * @param programId Program identifier
     * @param program Updated program data
     * @return ResponseEntity with updated program and HTTP 200
     */
    @PutMapping("/{programId}")
    public ResponseEntity<Program> updateProgram(@PathVariable String programId, @RequestBody Program program) {
        log.info("Updating program with ID: {}", programId);
        Program updated = programService.updateProgram(programId, program);
        log.info("Program updated successfully: {}", programId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a program by ID
     * @param programId Program identifier
     * @return ResponseEntity with HTTP 204
     */
    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> deleteProgram(@PathVariable String programId) {
        log.info("Deleting program with ID: {}", programId);
        programService.deleteProgram(programId);
        log.info("Program deleted successfully: {}", programId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates a new educational area for a program
     * @param programId Program identifier
     * @param area Educational area data
     * @return ResponseEntity with updated program and HTTP 201
     */
    @PostMapping("/{programId}/area")
    public ResponseEntity<Program> createEducationalArea(@PathVariable String programId, @Valid @RequestBody EducationalArea area) {
        log.info("Creating educational area '{}' for program: {}", area.getName(), programId);
        Program updated = programService.createEducationalArea(area, programId);
        log.info("Educational area created successfully for program: {}", programId);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }

    /**
     * Retrieves all educational areas for a program
     * @param programId Program identifier
     * @return ResponseEntity with list of educational areas and HTTP 200
     */
    @GetMapping("/{programId}/area")
    public ResponseEntity<List<EducationalArea>> getEducationalAreas(@PathVariable String programId) {
        log.debug("Retrieving educational areas for program: {}", programId);
        List<EducationalArea> areas = programService.getEducationalAreas(programId);
        log.debug("Found {} educational areas for program: {}", areas.size(), programId);
        return ResponseEntity.ok(areas);
    }

    /**
     * Retrieves a specific educational area by ID
     * @param programId Program identifier
     * @param areaId Educational area identifier
     * @return ResponseEntity with educational area data and HTTP 200
     * @throws ProgramExceptions.EducationalAreaNotFoundException if area not found
     */
    @GetMapping("/{programId}/area/{areaId}")
    public ResponseEntity<EducationalArea> getEducationalAreaById(
            @PathVariable String programId,
            @PathVariable String areaId) {

        log.debug("Retrieving educational area {} for program: {}", areaId, programId);
        EducationalArea area = programService.getEducationalAreaById(programId, areaId)
                .orElseThrow(() -> new ProgramExceptions.EducationalAreaNotFoundException(programId, areaId));
        return ResponseEntity.ok(area);
    }

    /**
     * Updates an educational area
     * @param programId Program identifier
     * @param areaId Educational area identifier
     * @param area Updated educational area data
     * @return ResponseEntity with updated area and HTTP 200
     */
    @PutMapping("/{programId}/area/{areaId}")
    public ResponseEntity<EducationalArea> updateEducationalArea(
            @PathVariable String programId,
            @PathVariable String areaId,
            @RequestBody EducationalArea area) {

        log.info("Updating educational area {} for program: {}", areaId, programId);
        EducationalArea updated = programService.updateEducationalArea(programId, areaId, area);
        log.info("Educational area updated successfully: {}", areaId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes an educational area
     * @param programId Program identifier
     * @param areaId Educational area identifier
     * @return ResponseEntity with HTTP 204
     */
    @DeleteMapping("/{programId}/area/{areaId}")
    public ResponseEntity<Void> deleteEducationalArea(@PathVariable String programId, @PathVariable String areaId) {
        log.info("Deleting educational area {} for program: {}", areaId, programId);
        programService.deleteEducationalArea(programId, areaId);
        log.info("Educational area deleted successfully: {}", areaId);
        return ResponseEntity.noContent().build();
    }
}