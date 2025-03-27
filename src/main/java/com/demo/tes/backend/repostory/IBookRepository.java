package com.demo.tes.backend.repostory;

import com.demo.tes.backend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookRepository extends JpaRepository<Book, Long> {
}
