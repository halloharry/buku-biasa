package com.demo.tes.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestBookPinjamDto {
    private Long bookId;
    private String borrowerName;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
