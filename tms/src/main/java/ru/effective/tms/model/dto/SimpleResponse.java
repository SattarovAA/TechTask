package ru.effective.tms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleResponse {
    /**
     * Simple sting message for response.
     */
    private String message;
}
