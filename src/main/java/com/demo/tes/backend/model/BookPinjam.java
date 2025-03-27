package com.demo.tes.backend.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book_borrowing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookPinjam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String borrowerName;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDate borrowDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private boolean isReturned = false;

    private boolean isNotified = false;

    public BookPinjam(Long id, String borrowerName, Book book, LocalDate borrowDate, LocalDate returnDate, boolean isReturned) {
        this.id = id;
        this.borrowerName = borrowerName;
        this.book = book;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }
}