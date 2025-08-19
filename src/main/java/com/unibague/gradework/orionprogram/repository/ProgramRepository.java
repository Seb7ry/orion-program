package com.unibague.gradework.orionprogram.repository;

import com.unibague.gradework.orionprogram.model.Program;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends MongoRepository<Program, String> {

    /**
     * Find program by name (existing method)
     */
    Optional<Program> findByProgramName(String programName);

    /**
     * Search programs by name containing text (case insensitive)
     */
    @Query("{'programName': {$regex: ?0, $options: 'i'}}")
    List<Program> findByProgramNameContainingIgnoreCase(String name);

    /**
     * Find programs by email domain
     */
    @Query("{'email': {$regex: ?0, $options: 'i'}}")
    List<Program> findByEmailContainingIgnoreCase(String emailDomain);

    /**
     * Count programs with educational areas
     */
    @Query(value = "{'educationalArea': {$exists: true, $not: {$size: 0}}}", count = true)
    long countProgramsWithEducationalAreas();

    /**
     * Count programs without educational areas
     */
    @Query(value = "{'$or': [{'educationalArea': {$exists: false}}, {'educationalArea': {$size: 0}}]}", count = true)
    long countProgramsWithoutEducationalAreas();

    /**
     * Find all programs sorted by name (optimized)
     */
    @Query(value = "{}", sort = "{'programName': 1}")
    List<Program> findAllSortedByName();
}