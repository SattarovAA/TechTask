package ru.effective.tms.model.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * List Response DTO for working with entity comment.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentListResponse {
    List<CommentResponse> comments = new ArrayList<>();
}
