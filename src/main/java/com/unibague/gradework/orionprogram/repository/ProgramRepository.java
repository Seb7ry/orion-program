package com.unibague.gradework.orionprogram.repository;

import com.unibague.gradework.orionprogram.model.Program;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Program entities in the MongoDB database.
 *
 * Extends the {@link MongoRepository} to provide CRUD operations for the {@link Program} entity.
 * Spring Data automatically implements this interface and generates the necessary queries.
 */
@Repository
public interface ProgramRepository extends MongoRepository<Program, String>
{
}
