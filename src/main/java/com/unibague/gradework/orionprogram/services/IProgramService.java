package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;

import java.util.List;
import java.util.Optional;

public interface IProgramService {

    Program createProgram(Program program);

    List<Program> getPrograms();

    Optional<Program> getProgramById(String programId);

    Optional<Program> getProgramByName(String programName);

    Program updateProgram(String programId, Program program);

    void deleteProgram(String programId);

    Program createEducationalArea(EducationalArea educationalArea, String programId);

    List<EducationalArea> getEducationalAreas(String programId);

    Optional<EducationalArea> getEducationalAreaById(String programId, String educationalAreaId);

    EducationalArea updateEducationalArea(String programId, String educationalAreaId, EducationalArea educationalArea);

    void deleteEducationalArea(String programId, String educationalAreaId);
}
