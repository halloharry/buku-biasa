package com.demo.tes.backend;

import com.demo.tes.backend.dto.BookDto;
import com.demo.tes.backend.dto.RequestBookDto;
import com.demo.tes.backend.dto.RequestBookPinjamDto;
import com.demo.tes.backend.dto.response.ResponseBookPinjamDto;
import com.demo.tes.backend.exception.security.ServiceException;
import com.demo.tes.backend.model.Book;
import com.demo.tes.backend.model.BookPinjam;
import com.demo.tes.backend.repostory.IBookPinjamRepository;
import com.demo.tes.backend.repostory.IBookRepository;
import com.demo.tes.backend.service.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceCreateUpdateTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private IBookRepository bookRepository;

    @Mock
    private IBookPinjamRepository bookPinjamRepository;

    @Test
    void testSaveBook_Success() throws ServiceException {
        // Given
        RequestBookDto request = new RequestBookDto("Java Basics", "Surya", 2022);
        Book book = new Book(null, request.getTitle(), request.getAuthor(), request.getYearPublished());

        // When
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Boolean result = bookService.saveBook(request);

        // Then
        assertTrue(result);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testSaveBook_EmptyTitle_ThrowsException() {
        RequestBookDto request = new RequestBookDto("", "Surya", 2022);

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.saveBook(request));
        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void testSaveBook_EmptyAuthor_ThrowsException() {
        RequestBookDto request = new RequestBookDto("Java Basics", "", 2022);

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.saveBook(request));
        assertEquals("Author cannot be empty", exception.getMessage());
    }

    @Test
    void testSaveBook_InvalidYear_ThrowsException() {
        RequestBookDto request = new RequestBookDto("Java Basics", "Surya", 999);

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.saveBook(request));
        assertEquals("Year must be valid", exception.getMessage());
    }

    @Test
    void testUpdateBook_Success() throws ServiceException {
        // Given
        Long bookId = 1L;
        RequestBookDto request = new RequestBookDto("Advanced Java", "Surya", 2023);
        Book existingBook = new Book(bookId, "Old Title", "Old Author", 2020);

        // When
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        // Act
        Boolean result = bookService.updateBook(bookId, request);

        // Then
        assertTrue(result);
        assertEquals("Advanced Java", existingBook.getTitle());
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void testUpdateBook_NotFound_ThrowsException() {
        Long bookId = 99L;
        RequestBookDto request = new RequestBookDto("Advanced Java", "Surya", 2023);

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.updateBook(bookId, request));
        assertEquals("Book not found with id: " + bookId, exception.getMessage());
    }

    @Test
    void testDeleteBook_Success() throws ServiceException {
        // Given
        Long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(bookId);

        // Act
        Boolean result = bookService.deleteBook(bookId);

        // Then
        assertTrue(result);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void testDeleteBook_NotFound_ThrowsException() {
        Long bookId = 99L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.deleteBook(bookId));
        assertEquals("Book not found with id: " + bookId, exception.getMessage());
    }

    @Test
    void testBookPinjam_Success() throws ServiceException {
        // Given
        RequestBookPinjamDto request = new RequestBookPinjamDto(1L, "joko", LocalDate.now(), LocalDate.now().plusDays(7));
        Book book = new Book(1L, "jalan jalan berenang", "Surya", 2022);
        BookPinjam bookPinjam = new BookPinjam(1L, "joko", book, LocalDate.now(), LocalDate.now().plusDays(7), false);

        when(bookPinjamRepository.findByBookIdAndIsReturnedFalse(1L)).thenReturn(Optional.empty());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookPinjamRepository.save(any(BookPinjam.class))).thenReturn(bookPinjam);

        // Act
        ResponseBookPinjamDto response = bookService.bookPinjam(request);

        // Then
        assertNotNull(response);
        assertEquals("joko", response.getBorrowerName());
        assertEquals("jalan jalan berenang", response.getBookTitle());
        verify(bookPinjamRepository, times(1)).save(any(BookPinjam.class));
    }

    @Test
    void testBookPinjam_BookAlreadyBorrowed_ThrowsException() {
        RequestBookPinjamDto request = new RequestBookPinjamDto( 1L, "joko", LocalDate.now(), LocalDate.now().plusDays(7));
        when(bookPinjamRepository.findByBookIdAndIsReturnedFalse(1L)).thenReturn(Optional.of(new BookPinjam()));

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.bookPinjam(request));
        assertEquals("This book is currently borrowed and cannot be borrowed again until returned.", exception.getMessage());
    }

    @Test
    void testBookPinjam_BookNotFound_ThrowsException() {
        RequestBookPinjamDto request = new RequestBookPinjamDto( 99L, "joko", LocalDate.now(), LocalDate.now().plusDays(7));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.bookPinjam(request));
        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void testBookKembali_Success() throws ServiceException {
        // Given
        Book book = new Book(1L, "jalan jalan berenang", "Surya", 2022);
        BookPinjam bookPinjam = new BookPinjam(1L, "joko", book, LocalDate.now(), LocalDate.now().plusDays(7), false);

        when(bookPinjamRepository.findById(1L)).thenReturn(Optional.of(bookPinjam));

        // Act
        String result = bookService.bookKembali(1L);

        // Then
        assertEquals("Book has been successfully returned!", result);
        assertTrue(bookPinjam.isReturned());
        verify(bookPinjamRepository, times(1)).save(bookPinjam);
    }

    @Test
    void testBookKembali_AlreadyReturned_ThrowsException() {
        Book book = new Book(1L, "jalan jalan berenang", "Surya", 2022);
        BookPinjam bookPinjam = new BookPinjam(1L, "joko", book, LocalDate.now(), LocalDate.now().plusDays(7), true);

        when(bookPinjamRepository.findById(1L)).thenReturn(Optional.of(bookPinjam));

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.bookKembali(1L));
        assertEquals("This book has already been returned.", exception.getMessage());
    }

    @Test
    void testBookKembali_NotFound_ThrowsException() {
        when(bookPinjamRepository.findById(99L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.bookKembali(99L));
        assertEquals("Borrowing record not found", exception.getMessage());
    }

    @Test
    void testSendNotif_Success() {
        // Given
        Book book = new Book(1L, "jalan jalan berenang", "Surya", 2022);
        BookPinjam bookPinjam = new BookPinjam(1L, "joko", book, LocalDate.now(), LocalDate.now().plusDays(7), true);
        List<BookPinjam> returnedBooks = List.of(bookPinjam);

        when(bookPinjamRepository.findBooksToNotify()).thenReturn(returnedBooks);

        // Act
        bookService.sendNotif();

        // Then
        assertTrue(bookPinjam.isNotified());
        verify(bookPinjamRepository, times(1)).saveAll(returnedBooks);
    }

    @Test
    void testSendNotifOverdue_Success() {
        // Given
        Book book = new Book(1L, "jalan jalan berenang", "Surya", 2022);
        BookPinjam overdueBook = new BookPinjam(1L, "joko", book, LocalDate.now().minusDays(10), LocalDate.now().minusDays(3), false);
        List<BookPinjam> overdueBooks = List.of(overdueBook);

        when(bookPinjamRepository.findOverdueBooks()).thenReturn(overdueBooks);

        // Act
        bookService.sendNotifOverdue();

        // Then
        verify(bookPinjamRepository, times(1)).findOverdueBooks();
    }
}
