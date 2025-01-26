package com.unibague.gradework.orionprogram.controller;

import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;
import com.unibague.gradework.orionprogram.services.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST Controller for managing programs and their associated educational areas.
 *
 * Provides endpoints for creating, retrieving, updating, and deleting programs and educational areas.
 */
@RestController
@RequestMapping("/service/program")
public class ProgramController {

    @Autowired
    private ProgramService programService;

    /**
     * Creates a new program.
     *
     * If the list of educational areas is null, it initializes it as an empty list.
     *
     * @param program The program object to be created.
     * @return A {@link ResponseEntity} containing the created program or an error message.
     */
    @PostMapping
    public ResponseEntity<?> createProgram(@RequestBody Program program) {
        try {
            if (program.getEducationalArea() == null) {
                program.setEducationalArea(new ArrayList<>());
            }
            return new ResponseEntity<>(programService.createProgram(program), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Incorrect data administered.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error has occurred while creating the program.");
        }
    }

    /**
     * Retrieves all programs.
     *
     * @return A {@link ResponseEntity} containing the list of all programs or an error message.
     */
    @GetMapping
    public ResponseEntity<List<?>> getAllPrograms() {
        try {
            return ResponseEntity.ok(programService.getPrograms());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Retrieves a program by its ID.
     *
     * @param programId The ID of the program to retrieve.
     * @return A {@link ResponseEntity} containing the program or an error message if not found.
     */
    @GetMapping("/{programId}")
    public ResponseEntity<?> getProgramById(@PathVariable String programId) {
        try {
            return new ResponseEntity<>(programService.getProgramById(programId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error when searching for the program.");
        }
    }

    /**
     * Updates an existing program.
     *
     * @param programId The ID of the program to update.
     * @param program   The updated program object.
     * @return A {@link ResponseEntity} containing the updated program or an error message.
     */
    @PutMapping("/{programId}")
    public ResponseEntity<?> updateProgram(@PathVariable String programId, @RequestBody Program program) {
        try {
            return new ResponseEntity<>(programService.updateProgram(programId, program), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Program to update not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unspecified error occurred: " + e.getMessage());
        }
    }

    /**
     * Deletes a program by its ID.
     *
     * @param programId The ID of the program to delete.
     * @return A {@link ResponseEntity} with no content or an error message if not found.
     */
    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> deleteProgram(@PathVariable String programId) {
        try{
            programService.deleteProgram(programId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Creates a new educational area and associates it with a program.
     *
     * @param programId The ID of the program to associate the educational area with.
     * @param area      The educational area object to be created.
     * @return A {@link ResponseEntity} containing the updated program with the new educational area or an error message.
     */
    @PostMapping("/{programId}/area")
    public ResponseEntity<?> createEducationalArea(@PathVariable String programId, @RequestBody EducationalArea area) {
        try {
            Program createdArea = programService.createEducationalArea(area, programId);
            return new ResponseEntity<>(createdArea, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves all educational areas for a specific program.
     *
     * @param programId The ID of the program.
     * @return A {@link ResponseEntity} containing the list of educational areas or an error message if the program is not found.
     */
    @GetMapping("/{programId}/area")
    public ResponseEntity<List<?>> getEducationalArea(@PathVariable String programId) {
        try {
            return ResponseEntity.ok(programService.getEducationalAreas(programId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Retrieves a specific educational area by its ID within a program.
     *
     * @param programId The ID of the program.
     * @param areaId    The ID of the educational area.
     * @return A {@link ResponseEntity} containing the educational area or an error message.
     */
    @GetMapping("/{programId}/area/{areaId}")
    public ResponseEntity<?> getEducationalAreaById(@PathVariable String programId, @PathVariable String areaId) {
        try {
            return new ResponseEntity<>(programService.getEducationalAreaById(programId, areaId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Updates an educational area within a program.
     *
     * @param programId The ID of the program.
     * @param areaId    The ID of the educational area to update.
     * @param area      The updated educational area object.
     * @return A {@link ResponseEntity} containing the updated educational area or an error message.
     */
    @PutMapping("/{programId}/area/{areaId}")
    public ResponseEntity<?> updateEducationalArea(@PathVariable String programId, @PathVariable String areaId, @RequestBody EducationalArea area) {
        try{
            return new ResponseEntity<>(programService.updateEducationalArea(programId, areaId, area), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Program to update not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unspecified error occurred: " + e.getMessage());
        }
    }

    /**
     * Deletes an educational area by its ID within a program.
     *
     * @param programId The ID of the program.
     * @param areaId    The ID of the educational area to delete.
     * @return A {@link ResponseEntity} with no content or an error message if not found.
     */
    @DeleteMapping("/{programId}/area/{areaId}")
    public ResponseEntity<Void> deleteEducationalArea(@PathVariable String programId, @PathVariable String areaId) {
        try {
            programService.deleteEducationalArea(programId, areaId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
