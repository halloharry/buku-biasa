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
public class BookDto  implements Serializable {
    @Serial
    private static final long serialVersionUID = -55567670720L;

    private Long id;
    private String title;
    private String author;
    private int yearPublished;
    private boolean isBorrowed;
}
