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

@RestController
@RequestMapping("/service/program")
public class ProgramController {

    @Autowired
    private ProgramService programService;

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

    @GetMapping
    public ResponseEntity<List<?>> getAllPrograms() {
        try {
            return ResponseEntity.ok(programService.getPrograms());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{programId}")
    public ResponseEntity<?> getProgramById(@PathVariable String programId) {
        try {
            return new ResponseEntity<>(programService.getProgramById(programId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error when searching for the program.");
        }
    }

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


    @GetMapping("/{programId}/area")
    public ResponseEntity<List<?>> getEducationalArea(@PathVariable String programId) {
        try {
            return ResponseEntity.ok(programService.getEducationalAreas(programId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{programId}/area/{areaId}")
    public ResponseEntity<?> getEducationalAreaById(@PathVariable String programId, @PathVariable String areaId) {
        try {
            return new ResponseEntity<>(programService.getEducationalAreaById(programId, areaId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

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
