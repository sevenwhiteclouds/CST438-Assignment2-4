package com.cst438.dto;

/*
 * Data Transfer Object for assignment data
 */
public record AssignmentDTO(
    int id,
    String title,
    String dueDate,
    String courseId,
    int secId,
    int secNo
) {
}