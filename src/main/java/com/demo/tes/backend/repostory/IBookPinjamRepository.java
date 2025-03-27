package com.demo.tes.backend.repostory;

import com.demo.tes.backend.model.Book;
import com.demo.tes.backend.model.BookPinjam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        SELECT bb FROM BookPinjam bb
        WHERE bb.isReturned = false\s
        AND bb.returnDate < CURRENT_DATE
   \s""")
    List<BookPinjam> findOverdueBooks();

    @Query("SELECT bp.book.id FROM BookPinjam bp WHERE bp.isReturned = false AND bp.book.id IN :bookIds")
    Set<Long> findBorrowedBookIds(@Param("bookIds") List<Long> bookIds);
}
