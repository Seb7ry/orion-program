package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;
import com.unibague.gradework.orionprogram.repository.ProgramRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.unibague.gradework.orionprogram.exception.ProgramExceptions.*;

/**
 * Service implementation for Program management
 * Handles business logic for programs and educational areas
 */
@Slf4j
@Service
public class ProgramService implements IProgramService {

    @Autowired
    private ProgramRepository programRepository;

    @Override
    public Program createProgram(Program program) {
        log.info("Creating new program: {}", program.getProgramName());

        // Validations
        if (program.getProgramName() == null || program.getProgramName().isBlank()) {
            throw new InvalidProgramDataException("Program name is required");
        }

        // Check if program name already exists
        if (programRepository.findByProgramName(program.getProgramName()).isPresent()) {
            throw new DuplicateProgramException(program.getProgramName());
        }

        // Initialize educational areas if null
        if (program.getEducationalArea() == null) {
            program.setEducationalArea(new ArrayList<>());
        }

        Program saved = programRepository.save(program);
        log.info("Program created successfully with ID: {}", saved.getProgramId());
        return saved;
    }

    @Override
    public List<Program> getPrograms() {
        log.debug("Retrieving all programs");
        List<Program> programs = programRepository.findAll();
        log.debug("Found {} programs", programs.size());
        return programs;
    }

    @Override
    public Optional<Program> getProgramById(String programId) {
        log.debug("Retrieving program by ID: {}", programId);
        if (programId == null || programId.isBlank()) {
            throw new InvalidProgramDataException("Program ID cannot be null or empty");
        }
        return programRepository.findById(programId);
    }

    @Override
    public Optional<Program> getProgramByName(String name) {
        log.debug("Retrieving program by name: {}", name);
        if (name == null || name.isBlank()) {
            throw new InvalidProgramDataException("Program name cannot be null or empty");
        }
        return programRepository.findByProgramName(name);
    }

    @Override
    public Program updateProgram(String programId, Program updatedProgram) {
        log.info("Updating program with ID: {}", programId);

        Program program = getProgramOrThrow(programId);

        // Update program name if provided
        if (updatedProgram.getProgramName() != null && !updatedProgram.getProgramName().isBlank()) {
            // Check if new name conflicts with existing program
            Optional<Program> existingWithName = programRepository.findByProgramName(updatedProgram.getProgramName());
            if (existingWithName.isPresent() && !existingWithName.get().getProgramId().equals(programId)) {
                throw new DuplicateProgramException(updatedProgram.getProgramName());
            }
            program.setProgramName(updatedProgram.getProgramName());
        }

        // Update email if provided
        if (updatedProgram.getEmail() != null && !updatedProgram.getEmail().isBlank()) {
            program.setEmail(updatedProgram.getEmail());
        }

        Program saved = programRepository.save(program);
        log.info("Program updated successfully: {}", programId);
        return saved;
    }

    @Override
    public void deleteProgram(String programId) {
        log.info("Deleting program with ID: {}", programId);

        Program program = getProgramOrThrow(programId);
        programRepository.delete(program);

        log.info("Program deleted successfully: {}", programId);
    }

    @Override
    public Program createEducationalArea(EducationalArea educationalArea, String programId) {
        log.info("Creating educational area '{}' for program: {}", educationalArea.getName(), programId);

        // Validate educational area data
        if (educationalArea.getName() == null || educationalArea.getName().isBlank()) {
            throw new InvalidProgramDataException("Educational area name is required");
        }

        Program program = getProgramOrThrow(programId);

        // Initialize educational areas list if null
        if (program.getEducationalArea() == null) {
            program.setEducationalArea(new ArrayList<>());
        }

        // Check if area name already exists in this program
        boolean nameExists = program.getEducationalArea().stream()
                .anyMatch(area -> area.getName().equalsIgnoreCase(educationalArea.getName()));

        if (nameExists) {
            throw new InvalidProgramDataException("Educational area with name '" + educationalArea.getName() + "' already exists in this program");
        }

        // Generate area ID
        int areaCount = program.getEducationalArea().size() + 1;
        String areaId = String.format("%sA%02d", programId, areaCount);
        educationalArea.setEducationalAreaId(areaId);

        // Add to program
        program.getEducationalArea().add(educationalArea);

        Program saved = programRepository.save(program);
        log.info("Educational area created successfully for program: {}", programId);
        return saved;
    }

    @Override
    public List<EducationalArea> getEducationalAreas(String programId) {
        log.debug("Retrieving educational areas for program: {}", programId);

        Program program = getProgramOrThrow(programId);
        List<EducationalArea> areas = program.getEducationalArea() != null ?
                program.getEducationalArea() : new ArrayList<>();

        log.debug("Found {} educational areas for program: {}", areas.size(), programId);
        return areas;
    }

    @Override
    public Optional<EducationalArea> getEducationalAreaById(String programId, String educationalAreaId) {
        log.debug("Retrieving educational area {} for program: {}", educationalAreaId, programId);

        if (educationalAreaId == null || educationalAreaId.isBlank()) {
            throw new InvalidProgramDataException("Educational area ID cannot be null or empty");
        }

        Program program = getProgramOrThrow(programId);

        if (program.getEducationalArea() == null) {
            return Optional.empty();
        }

        return program.getEducationalArea().stream()
                .filter(area -> area.getEducationalAreaId().equals(educationalAreaId))
                .findFirst();
    }

    @Override
    public EducationalArea updateEducationalArea(String programId, String educationalAreaId, EducationalArea educationalArea) {
        log.info("Updating educational area {} for program: {}", educationalAreaId, programId);

        // Validate input data
        if (educationalArea.getName() == null || educationalArea.getName().isBlank()) {
            throw new InvalidProgramDataException("Educational area name is required");
        }

        Program program = getProgramOrThrow(programId);

        if (program.getEducationalArea() == null) {
            throw new EducationalAreaNotFoundException(programId, educationalAreaId);
        }

        // Find and update the educational area
        Optional<EducationalArea> areaToUpdate = program.getEducationalArea().stream()
                .filter(area -> area.getEducationalAreaId().equals(educationalAreaId))
                .findFirst();

        if (areaToUpdate.isEmpty()) {
            throw new EducationalAreaNotFoundException(programId, educationalAreaId);
        }

        // Check if new name conflicts with other areas in the same program
        boolean nameConflict = program.getEducationalArea().stream()
                .anyMatch(area -> !area.getEducationalAreaId().equals(educationalAreaId) &&
                        area.getName().equalsIgnoreCase(educationalArea.getName()));

        if (nameConflict) {
            throw new InvalidProgramDataException("Educational area with name '" + educationalArea.getName() + "' already exists in this program");
        }

        // Update the area
        EducationalArea area = areaToUpdate.get();
        area.setName(educationalArea.getName());

        programRepository.save(program);
        log.info("Educational area updated successfully: {}", educationalAreaId);

        return area;
    }

    @Override
    public void deleteEducationalArea(String programId, String educationalAreaId) {
        log.info("Deleting educational area {} for program: {}", educationalAreaId, programId);

        Program program = getProgramOrThrow(programId);

        if (program.getEducationalArea() == null) {
            throw new EducationalAreaNotFoundException(programId, educationalAreaId);
        }

        boolean removed = program.getEducationalArea().removeIf(
                area -> area.getEducationalAreaId().equals(educationalAreaId));

        if (!removed) {
            throw new EducationalAreaNotFoundException(programId, educationalAreaId);
        }

        programRepository.save(program);
        log.info("Educational area deleted successfully: {}", educationalAreaId);
    }

    /**
     * Helper method to get program or throw exception
     */
    private Program getProgramOrThrow(String programId) {
        if (programId == null || programId.isBlank()) {
            throw new InvalidProgramDataException("Program ID cannot be null or empty");
        }

        return programRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException(programId));
    }
}