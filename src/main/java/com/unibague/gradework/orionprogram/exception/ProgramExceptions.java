package com.unibague.gradework.orionprogram.exception;

public class ProgramExceptions {

    public static class ProgramNotFoundException extends RuntimeException {
        public ProgramNotFoundException(String programId) {
            super("Program not found with ID: " + programId);
        }

        public ProgramNotFoundException(String field, String value) {
            super("Program not found with " + field + ": " + value);
        }
    }

    public static class EducationalAreaNotFoundException extends RuntimeException {
        public EducationalAreaNotFoundException(String areaId) {
            super("Educational area not found with ID: " + areaId);
        }

        public EducationalAreaNotFoundException(String programId, String areaId) {
            super("Educational area with ID " + areaId + " not found in program " + programId);
        }
    }

    /**
     * Exception thrown when trying to create a duplicate program
     */
    public static class DuplicateProgramException extends RuntimeException {
        public DuplicateProgramException(String programName) {
            super("Program with name '" + programName + "' already exists");
        }
    }

    /**
     * Exception thrown for invalid program data
     */
    public static class InvalidProgramDataException extends RuntimeException {
        public InvalidProgramDataException(String message) {
            super(message);
        }
    }
}
