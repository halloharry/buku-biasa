package com.demo.tes.backend.repostory;

import com.demo.tes.backend.model.Book;
import com.demo.tes.backend.model.BookPinjam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IBookPinjamRepository extends JpaRepository<BookPinjam, Long> {
    Optional<BookPinjam> findByBookIdAndIsReturnedFalse(Long bookId);

    @Query("""
        SELECT bb FROM BookPinjam bb
        WHERE bb.isReturned = true\s
        AND bb.isNotified = false\s
        AND bb.returnDate <= CURRENT_DATE
   \s""")
    List<BookPinjam> findBooksToNotify();

    @Query("""
        SELECT bb FROM BookBorrowing bb
        WHERE bb.isReturned = false\s
        AND bb.returnDate < CURRENT_DATE
   \s""")
    List<BookPinjam> findOverdueBooks();
}
