package com.unibague.gradework.orionprogram.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a Program entity stored in the MongoDB database.
 * <p>
 * A program consists of a unique identifier, name, email, and a list of associated educational areas.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "programs")
public class Program
{
    /**
     * The unique identifier for the program.
     * This serves as the primary key in the MongoDB collection.
     */
    @Id
    private String programId;

    /**
     * The name of the program.
     */
    private String programName;

    /**
     * The email associated with the program.
     */
    private String email;

    /**
     * The list of educational areas associated with the program.
     * Each program can have multiple educational areas.
     */
    private List<EducationalArea> educationalArea;
}
