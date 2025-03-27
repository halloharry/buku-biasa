package com.demo.tes.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBookPinjamDto {
    private Long id;
    private String borrowerName;
    private String bookTitle;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
