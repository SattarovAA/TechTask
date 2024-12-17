package ru.effective.tms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleRequest {
    /**
     * Simple sting message from request.
     */
    private String message;
}
