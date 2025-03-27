package com.demo.tes.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestBookDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -5551830720L;

    private String title;
    private String author;
    private Integer yearPublished;
    
}
