package com.unibague.gradework.orionprogram.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "programs")
public class Program {

    @Id
    private String programId;

    @Indexed(unique = true)
    private String programName;

    @Indexed(unique = true)
    private String email;

    private List<EducationalArea> educationalArea;
}
