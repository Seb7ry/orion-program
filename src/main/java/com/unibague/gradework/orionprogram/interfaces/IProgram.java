package com.unibague.gradework.orionprogram.interfaces;

import com.unibague.gradework.orionprogram.model.EducationalArea;
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
     * @param programId The ID of the program to retrieve.
     * @return An Optional containing the Program object if found, or empty if not.
     */
    Optional<Program> getProgramById(String programId);

    /**
     * Updates an existing program by its ID.
     *
     * @param programId The ID of the program to update.
     * @param program The updated Program object.
     * @return The updated Program object.
     */
    Program updateProgram(String programId, Program program);

    /**
     * Deletes a program by its unique ID.
     *
     * @param programId The ID of the program to delete.
     */
    void deleteProgram(String programId);

    /**
     * Creates a new educational area and associates it with a specific program.
     *
     * @param educationalArea The EducationalArea object to create.
     * @param programId The Program ID to which this educational area belongs.
     * @return The created EducationalArea object.
     */
    Program createEducationalArea(EducationalArea educationalArea, String programId);

    /**
     * Retrieves a list of all educational areas for a specific program.
     *
     * @param programId The ID of the program whose educational areas are to be retrieved.
     * @return A list of all educational areas associated with the specified program.'
     */
    List<EducationalArea> getEducationalAreas(String programId);

    /**
     * Retrieves a specific educational area by its unique ID within a given program.
     *
     * @param programId The ID of the program to which the educational area belongs.
     * @param educationalAreaId The ID of the educational area to retrieve.
     * @return An Optional containing the EducationalArea object if found, or empty if not.
     */
    Optional<EducationalArea> getEducationalAreaById(String programId, String educationalAreaId);

    /**
     * Updates an existing educational area within a specific program.
     *
     * @param programId The ID of the program to which the educational area belongs.
     * @param educationalAreaId The ID of the educational area to update.
     * @param educationalArea The updated EducationalArea object.
     * @return The updated EducationalArea object.
     */
    EducationalArea updateEducationalArea(String programId, String educationalAreaId, EducationalArea educationalArea);

    /**
     * Deletes an educational area by its unique ID within a specific program.
     *
     * @param programId The ID of the program to which the educational area belongs.
     * @param educationalAreaId The ID of the educational area to delete.
     */
    void deleteEducationalArea(String programId, String educationalAreaId);
}
