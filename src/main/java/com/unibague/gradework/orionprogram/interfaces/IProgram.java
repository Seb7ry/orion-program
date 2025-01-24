package com.unibague.gradework.orionprogram.interfaces;

import com.unibague.gradework.orionprogram.model.Program;

import java.util.List;
import java.util.Optional;

/**
 * Interface for managing programs and their associated educational areas.
 *
 * This interface defines the operations for handling the relationship between programs
 * and educational areas. Each program can have multiple educational areas, and each
 * educational area belongs to one program.
 */
public interface IProgram {

    /**
     * Creates a new program.
     *
     * @param program The Program object to be created.
     * @return The created Program object.
     */
    Program createProgram(Program program);

    /**
     * Retrieves a list of all programs.
     *
     * @return A list of all programs.
     */
    List<Program> getPrograms();

    /**
     * Retrieves a program by its unique ID.
     *
     * @param id The ID of the program to retrieve.
     * @return An Optional containing the Program object if found, or empty if not.
     */
    Optional<Program> getProgramById(String id);

    /**
     * Updates an existing program by its ID.
     *
     * @param id The ID of the program to update.
     * @param program The updated Program object.
     * @return The updated Program object.
     */
    Program updateProgram(String id, Program program);

    /**
     * Deletes a program by its unique ID.
     *
     * @param id The ID of the program to delete.
     */
    void deleteProgram(String id);
}
