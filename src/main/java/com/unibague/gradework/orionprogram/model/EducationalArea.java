package com.unibague.gradework.orionprogram.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents an Educational Area entity stored in the MongoDB database.
 * <p>
 * An educational area is a component of a program and has a unique identifier and a name.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "educationalAreas")
public class EducationalArea
{
    /**
     * The unique identifier for the educational area.
     * This serves as the primary key in the MongoDB collection.
     */
    @Id
    private String educationalAreaId;

    /**
     * The name of the educational area.
     */
    private String name;
}
