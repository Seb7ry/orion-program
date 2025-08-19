package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;

import java.util.List;
import java.util.Optional;

public interface IProgramService {

    Program createProgram(Program program);

    List<Program> getPrograms();

    /**
     * Get programs with optional search
     */
    List<Program> getPrograms(String search);

    /**
     * Get simple program statistics
     */
    ProgramStatistics getProgramStatistics();

    Optional<Program> getProgramById(String programId);

    Optional<Program> getProgramByName(String programName);

    Program updateProgram(String programId, Program program);

    void deleteProgram(String programId);

    Program createEducationalArea(EducationalArea educationalArea, String programId);

    List<EducationalArea> getEducationalAreas(String programId);

    Optional<EducationalArea> getEducationalAreaById(String programId, String educationalAreaId);

    EducationalArea updateEducationalArea(String programId, String educationalAreaId, EducationalArea educationalArea);

    void deleteEducationalArea(String programId, String educationalAreaId);

    /**
     * Simple statistics class for program metrics
     */
    class ProgramStatistics {
        private final long totalPrograms;
        private final long programsWithAreas;
        private final long programsWithoutAreas;
        private final long totalEducationalAreas;

        public ProgramStatistics(long totalPrograms, long programsWithAreas,
                                 long programsWithoutAreas, long totalEducationalAreas) {
            this.totalPrograms = totalPrograms;
            this.programsWithAreas = programsWithAreas;
            this.programsWithoutAreas = programsWithoutAreas;
            this.totalEducationalAreas = totalEducationalAreas;
        }

        // Getters
        public long getTotalPrograms() { return totalPrograms; }
        public long getProgramsWithAreas() { return programsWithAreas; }
        public long getProgramsWithoutAreas() { return programsWithoutAreas; }
        public long getTotalEducationalAreas() { return totalEducationalAreas; }
    }
}