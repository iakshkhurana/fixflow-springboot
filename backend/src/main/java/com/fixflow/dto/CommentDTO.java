package com.fixflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Used both for receiving comment creation requests (only 'text' needed)
 * and for returning comment data in responses (all fields populated).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;
    private String text;       // Maps to RequestComment.commentText
    private Long userId;
    private String userName;   // Full name of commenter
    private LocalDateTime createdAt;
}
