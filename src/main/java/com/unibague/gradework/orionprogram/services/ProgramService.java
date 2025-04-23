package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;
import com.unibague.gradework.orionprogram.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProgramService implements IProgramService {

    @Autowired
    private ProgramRepository programRepository;

    @Override
    public Program createProgram(Program program) {
        if (program.getProgramName() == null || program.getProgramName().isBlank()) {
            throw new IllegalArgumentException("El nombre del programa es obligatorio.");
        }
        if (program.getEmail() == null || program.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo del programa es obligatorio.");
        }

        return programRepository.save(program);
    }

    @Override
    public List<Program> getPrograms() {
        return programRepository.findAll();
    }

    @Override
    public Optional<Program> getProgramById(String programId) {
        return programRepository.findById(programId);
    }

    @Override
    public Optional<Program> getProgramByName(String name) {
        return programRepository.findByProgramName(name);
    }

    @Override
    public Program updateProgram(String programId, Program updatedProgram) {
        Program program = getProgramOrThrow(programId);

        if (updatedProgram.getProgramName() != null && !updatedProgram.getProgramName().isBlank()) {
            program.setProgramName(updatedProgram.getProgramName());
        }

        if (updatedProgram.getEmail() != null && !updatedProgram.getEmail().isBlank()) {
            program.setEmail(updatedProgram.getEmail());
        }

        return programRepository.save(program);
    }

    @Override
    public void deleteProgram(String programId) {
        getProgramOrThrow(programId);
        programRepository.deleteById(programId);
    }

    @Override
    public Program createEducationalArea(EducationalArea educationalArea, String programId) {
        if (educationalArea.getName() == null || educationalArea.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del área educativa es obligatorio.");
        }

        Program program = getProgramOrThrow(programId);

        if (program.getEducationalArea() == null) {
            program.setEducationalArea(new ArrayList<>());
        }

        int areaCount = program.getEducationalArea().size() + 1;
        String areaId = String.format("%sA%02d", programId, areaCount);

        educationalArea.setEducationalAreaId(areaId);
        program.getEducationalArea().add(educationalArea);
        return programRepository.save(program);
    }

    @Override
    public List<EducationalArea> getEducationalAreas(String programId) {
        return getProgramOrThrow(programId).getEducationalArea();
    }

    @Override
    public Optional<EducationalArea> getEducationalAreaById(String programId, String educationalAreaId) {
        Program program = getProgramOrThrow(programId);
        return program.getEducationalArea().stream()
                .filter(area -> area.getEducationalAreaId().equals(educationalAreaId))
                .findFirst();
    }

    @Override
    public EducationalArea updateEducationalArea(String programId, String educationalAreaId, EducationalArea educationalArea) {
        if (educationalArea.getName() == null || educationalArea.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre del área educativa es obligatorio.");
        }

        Program program = getProgramOrThrow(programId);

        boolean updated = program.getEducationalArea().stream()
                .filter(area -> area.getEducationalAreaId().equals(educationalAreaId))
                .peek(area -> area.setName(educationalArea.getName()))
                .findFirst()
                .isPresent();

        if (!updated) {
            throw new IllegalArgumentException("No se encontró el área educativa con ID: " + educationalAreaId);
        }

        programRepository.save(program);
        return educationalArea;
    }

    @Override
    public void deleteEducationalArea(String programId, String educationalAreaId) {
        Program program = getProgramOrThrow(programId);

        boolean removed = program.getEducationalArea().removeIf(
                area -> area.getEducationalAreaId().equals(educationalAreaId));

        if (!removed) {
            throw new IllegalArgumentException("No se encontró el área educativa con ID: " + educationalAreaId);
        }

        programRepository.save(program);
    }

    private Program getProgramOrThrow(String programId) {
        return programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Programa no encontrado con ID: " + programId));
    }
}