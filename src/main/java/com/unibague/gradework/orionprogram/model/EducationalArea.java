package com.unibague.gradework.orionprogram.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "educationalAreas")
public class EducationalArea {

    @Id
    private String educationalAreaId;

    @Indexed
    private String name;

    @Indexed
    private String leaderId;
}
