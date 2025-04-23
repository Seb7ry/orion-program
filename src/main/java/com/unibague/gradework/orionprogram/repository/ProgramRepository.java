package com.unibague.gradework.orionprogram.repository;

import com.unibague.gradework.orionprogram.model.Program;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramRepository extends MongoRepository<Program, String> {
    Optional<Program> findByProgramName(String programName);
}
