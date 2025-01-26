package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.interfaces.IProgram;
import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;
import com.unibague.gradework.orionprogram.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing Programs and their associated Educational Areas.
 *
 * This class provides the business logic for CRUD operations on Programs and Educational Areas.
 * It interacts with the {@link ProgramRepository} for database operations.
 */
@Service
public class ProgramService implements IProgram
{
    @Autowired
    private ProgramRepository programRepository;

    /**
     * Creates a new program.
     *
     * @param program The program to be created.
     * @return The created program.
     */
    @Override
    public Program createProgram(Program program) {
        return programRepository.save(program);
    }

    /**
     * Retrieves all programs.
     *
     * @return A list of all programs.
     */
    @Override
    public List<Program> getPrograms() {
        return programRepository.findAll();
    }

    /**
     * Retrieves a program by its ID.
     *
     * @param programId The ID of the program to retrieve.
     * @return An {@link Optional} containing the program if found.
     */
    @Override
    public Optional<Program> getProgramById(String programId) {
        return programRepository.findById(programId);
    }

    /**
     * Updates an existing program.
     *
     * @param programId      The ID of the program to update.
     * @param updatedProgram The updated program data.
     * @return The updated program.
     * @throws IllegalArgumentException If the program does not exist.
     */
    @Override
    public Program updateProgram(String programId, Program updatedProgram) {
        Optional<Program> existingProgram = programRepository.findById(programId);

        if (existingProgram.isPresent()) {
            Program program = existingProgram.get();
            program.setProgramName(updatedProgram.getProgramName());
            program.setEmail(updatedProgram.getEmail());
            program.setEducationalArea(updatedProgram.getEducationalArea());
            return programRepository.save(program);
        } else {
            throw new IllegalArgumentException("Program not found");
        }
    }

    /**
     * Deletes a program by its ID.
     *
     * @param programId The ID of the program to delete.
     */
    @Override
    public void deleteProgram(String programId) {
        programRepository.deleteById(programId);
    }

    /**
     * Creates an educational area and associates it with a specific program.
     *
     * @param educationalArea The educational area to create.
     * @param programId       The ID of the program to associate with the educational area.
     * @return The updated program containing the new educational area.
     * @throws IllegalArgumentException If the program does not exist.
     */
    @Override
    public Program createEducationalArea(EducationalArea educationalArea, String programId) {
        Optional<Program> existingProgram = programRepository.findById(programId);
        if (existingProgram.isPresent()) {
            Program program = existingProgram.get();
            if (program.getEducationalArea() == null) {
                program.setEducationalArea(new ArrayList<>());
            }
            educationalArea.setEducationalAreaId(UUID.randomUUID().toString());
            program.getEducationalArea().add(educationalArea);
            return programRepository.save(program);
        } else {
            throw new IllegalArgumentException("Program not found to create with ID: " + programId);
        }
    }

    /**
     * Retrieves all educational areas associated with a specific program.
     *
     * @param programId The ID of the program.
     * @return A list of educational areas.
     * @throws IllegalArgumentException If the program does not exist.
     */
    @Override
    public List<EducationalArea> getEducationalAreas(String programId) {
        Optional<Program> existingProgram = programRepository.findById(programId);

        if(existingProgram.isPresent()) {
            return existingProgram.get().getEducationalArea();
        } else {
            throw new IllegalArgumentException("Program not found to list with ID: " + programId);
        }
    }

    /**
     * Retrieves a specific educational area by its ID within a program.
     *
     * @param programId          The ID of the program.
     * @param educationalAreaId  The ID of the educational area.
     * @return An {@link Optional} containing the educational area if found.
     * @throws IllegalArgumentException If the program does not exist.
     */
    @Override
    public Optional<EducationalArea> getEducationalAreaById(String programId, String educationalAreaId) {
        Optional<Program> existingProgram = programRepository.findById(programId);

        if(existingProgram.isPresent()) {
            return existingProgram.get().getEducationalArea().stream()
                    .filter(area -> area.getEducationalAreaId().equals(educationalAreaId))
                    .findFirst();
        } else {
            throw new IllegalArgumentException("Program not found to get by with ID: " + programId);
        }
    }

    /**
     * Updates an educational area within a program.
     *
     * @param programId          The ID of the program.
     * @param educationalAreaId  The ID of the educational area to update.
     * @param educationalArea    The updated educational area data.
     * @return The updated educational area.
     * @throws IllegalArgumentException If the program does not exist.
     */
    @Override
    public EducationalArea updateEducationalArea(String programId, String educationalAreaId, EducationalArea educationalArea) {
        Optional<Program> existingProgram = programRepository.findById(programId);

        if(existingProgram.isPresent()) {
            Program program = existingProgram.get();
            program.getEducationalArea().stream()
                    .filter(area -> area.getEducationalAreaId().equals(educationalAreaId))
                    .findFirst()
                    .ifPresent(area -> {area.setName(educationalArea.getName());
                    });
            programRepository.save(program);
            return educationalArea;
        } else {
            throw new IllegalArgumentException("Program not found to update with ID: " + programId);
        }
    }

    /**
     * Deletes an educational area within a program.
     *
     * @param programId          The ID of the program.
     * @param educationalAreaId  The ID of the educational area to delete.
     * @throws IllegalArgumentException If the program or educational area does not exist.
     */
    @Override
    public void deleteEducationalArea(String programId, String educationalAreaId) {
        Optional<Program> existingProgram = programRepository.findById(programId);

        if(existingProgram.isPresent()){
            Program program = existingProgram.get();

            boolean removed = program.getEducationalArea().removeIf(
                    area -> area.getEducationalAreaId().equals(educationalAreaId));

            if (removed) {
                programRepository.save(program);
            } else {
                throw new IllegalArgumentException("Program not found to delete with ID: " + programId);
            }
        }
    }
}
