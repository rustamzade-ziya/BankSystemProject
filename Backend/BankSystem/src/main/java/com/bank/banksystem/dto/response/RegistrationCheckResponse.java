package com.bank.banksystem.dto.response;

public class RegistrationCheckResponse {

    private boolean isUnique;
    private String conflictField;

    public RegistrationCheckResponse(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public RegistrationCheckResponse(boolean isUnique, String conflictField) {
        this.isUnique = isUnique;
        this.conflictField = conflictField;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public String getConflictField() {
        return conflictField;
    }
}

