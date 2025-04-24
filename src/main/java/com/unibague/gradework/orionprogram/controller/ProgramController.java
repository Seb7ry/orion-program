package com.unibague.gradework.orionprogram.controller;

import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;
import com.unibague.gradework.orionprogram.services.IProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/service/program")
public class ProgramController {

    @Autowired
    private IProgramService programService;

    @PostMapping
    public ResponseEntity<?> createProgram(@RequestBody Program program) {
        try {
            if (program.getEducationalArea() == null) {
                program.setEducationalArea(new ArrayList<>());
            }
            Program created = programService.createProgram(program);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Incorrect data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating program.");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPrograms() {
        try {
            return ResponseEntity.ok(programService.getPrograms());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving programs.");
        }
    }

    @GetMapping("/{programId}")
    public ResponseEntity<?> getProgramById(@PathVariable String programId) {
        Optional<Program> program = programService.getProgramById(programId);
        if (program.isPresent()) {
            return ResponseEntity.ok(program.get());
        } else {
            return ResponseEntity.status(404).body("Program not found with ID: " + programId);
        }
    }

    @GetMapping("/name/{programName}")
    public ResponseEntity<?> getProgramByName(@PathVariable String programName) {
        Optional<Program> program = programService.getProgramByName(programName);
        if(program.isPresent()) {
            return ResponseEntity.ok(program.get());
        } else {
            return ResponseEntity.status(404).body("Program not found with name: " + programName);
        }
    }

    @PutMapping("/{programId}")
    public ResponseEntity<?> updateProgram(@PathVariable String programId, @RequestBody Program program) {
        try {
            Program updated = programService.updateProgram(programId, program);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Program not found to update.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating program.");
        }
    }

    @DeleteMapping("/{programId}")
    public ResponseEntity<?> deleteProgram(@PathVariable String programId) {
        try {
            programService.deleteProgram(programId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Program not found to delete.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting program.");
        }
    }

    @PostMapping("/{programId}/area")
    public ResponseEntity<?> createEducationalArea(@PathVariable String programId, @RequestBody EducationalArea area) {
        try {
            Program updated = programService.createEducationalArea(area, programId);
            return ResponseEntity.status(201).body(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating educational area.");
        }
    }

    @GetMapping("/{programId}/area")
    public ResponseEntity<?> getEducationalArea(@PathVariable String programId) {
        try {
            return ResponseEntity.ok(programService.getEducationalAreas(programId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Program not found.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving areas.");
        }
    }

    @GetMapping("/{programId}/area/{areaId}")
    public ResponseEntity<Object> getEducationalAreaById(
            @PathVariable String programId,
            @PathVariable String areaId) {

        try {
            return programService.getEducationalAreaById(programId, areaId)
                    .<ResponseEntity<Object>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Educational area not found with ID: " + areaId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Program not found with ID: " + programId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/{programId}/area/{areaId}")
    public ResponseEntity<?> updateEducationalArea(
            @PathVariable String programId,
            @PathVariable String areaId,
            @RequestBody EducationalArea area
    ) {
        try {
            EducationalArea updated = programService.updateEducationalArea(programId, areaId, area);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Educational area not found to update.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating area.");
        }
    }

    @DeleteMapping("/{programId}/area/{areaId}")
    public ResponseEntity<?> deleteEducationalArea(@PathVariable String programId, @PathVariable String areaId) {
        try {
            programService.deleteEducationalArea(programId, areaId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Educational area not found to delete.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting area.");
        }
    }
}