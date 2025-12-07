package com.coloradodev.cronos.dto.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding a note to a client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientNoteDTO {

    @NotBlank(message = "Note content is required")
    @Size(max = 2000, message = "Note must not exceed 2000 characters")
    private String note;
}
