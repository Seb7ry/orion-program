package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.interfaces.IProgram;
import com.unibague.gradework.orionprogram.model.EducationalArea;
import com.unibague.gradework.orionprogram.model.Program;
import com.unibague.gradework.orionprogram.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProgramService implements IProgram
{
    @Autowired
    private ProgramRepository programRepository;

    @Override
    public Program createProgram(Program program) {
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

    @Override
    public void deleteProgram(String programId) {
        programRepository.deleteById(programId);
    }

    @Override
    public Program createEducationalArea(EducationalArea educationalArea, String programId) {
        Optional<Program> existingProgram = programRepository.findById(programId);

        if(existingProgram.isPresent()) {
            Program program = existingProgram.get();
            educationalArea.setEducationalAreaId(UUID.randomUUID().toString());
            program.getEducationalArea().add(educationalArea);
            return programRepository.save(program);
        } else {
            throw new IllegalArgumentException("Program not found to create with ID: " + programId);
        }
    }

    @Override
    public List<EducationalArea> getEducationalAreas(String programId) {
        Optional<Program> existingProgram = programRepository.findById(programId);

        if(existingProgram.isPresent()) {
            return existingProgram.get().getEducationalArea();
        } else {
            throw new IllegalArgumentException("Program not found to list with ID: " + programId);
        }
    }

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
