package com.unibague.gradework.orionprogram.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "programs")
public class Program {

    @Id
    private String programId;

    @NotBlank(message = "Program name is required")
    @Size(min = 2, max = 100, message = "Program name must be between 2 and 100 characters")
    @Indexed(unique = true)
    private String programName;

    @Email(message = "Email format is invalid")
    @Indexed(unique = true)
    private String email;

    private String image;

    private List<EducationalArea> educationalArea;
}