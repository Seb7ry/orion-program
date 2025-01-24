package com.unibague.gradework.orionprogram.interfaces;

import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;

import java.util.List;
import java.util.Optional;

public interface IEducationalArea
{
    /**
     * Creates a new educational area and associates it with a specific program.
     *
     * @param educationalArea The EducationalArea object to create.
     * @param program The Program to which this educational area belongs.
     * @return The created EducationalArea object.
     */
    EducationalArea createEducationalArea(EducationalArea educationalArea, Program program);

    /**
     * Retrieves a list of all educational areas for a specific program.
     *
     * @param programId The ID of the program whose educational areas are to be retrieved.
     * @return A list of all educational areas associated with the specified program.
     */
    List<EducationalArea> getEducationalAreas(String programId);

    /**
     * Retrieves a specific educational area by its unique ID within a given program.
     *
     * @param programId The ID of the program to which the educational area belongs.
     * @param id The ID of the educational area to retrieve.
     * @return An Optional containing the EducationalArea object if found, or empty if not.
     */
    Optional<EducationalArea> getEducationalAreaById(String programId, String id);

    /**
     * Updates an existing educational area within a specific program.
     *
     * @param programId The ID of the program to which the educational area belongs.
     * @param id The ID of the educational area to update.
     * @param educationalArea The updated EducationalArea object.
     * @return The updated EducationalArea object.
     */
    EducationalArea updateEducationalArea(String programId, String id, EducationalArea educationalArea);

    /**
     * Deletes an educational area by its unique ID within a specific program.
     *
     * @param programId The ID of the program to which the educational area belongs.
     * @param id The ID of the educational area to delete.
     */
    void deleteEducationalArea(String programId, String id);
}
