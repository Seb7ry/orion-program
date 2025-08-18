package com.unibague.gradework.orionprogram.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "educationalAreas")
public class EducationalArea {

    @Id
    private String educationalAreaId;

    @NotBlank(message = "Educational area name is required")
    @Size(min = 2, max = 80, message = "Educational area name must be between 2 and 80 characters")
    @Indexed
    private String name;

    @Indexed
    private String leaderId;

    private String image;
}