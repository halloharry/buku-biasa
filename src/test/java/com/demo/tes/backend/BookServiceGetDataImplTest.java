package com.demo.tes.backend;

import com.demo.tes.backend.dto.BookDto;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceGetDataImplTest {
    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private IBookRepository bookRepository;

    @Mock
    private IBookPinjamRepository bookPinjamRepository;

    @Test
    void testGetDataPage_Success() throws ServiceException {
        int page = 1;
        int size = 2;
        Pageable pageable = PageRequest.of(page - 1, size);

        Book book1 = new Book(1L, "resep bakso", "cep juna", 2020);
        Book book2 = new Book(2L, "resep seblak", "cep marinka", 2021);
        List<Book> bookList = List.of(book1, book2);
        Page<Book> bookPage = new PageImpl<>(bookList, pageable, bookList.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookPinjamRepository.findByBookIdAndIsReturnedFalse(1L)).thenReturn(Optional.of(new BookPinjam()));
        when(bookPinjamRepository.findByBookIdAndIsReturnedFalse(2L)).thenReturn(Optional.empty());

        Page<BookDto> result = bookService.getDataPage(page, size);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        assertEquals("resep bakso", result.getContent().get(0).getTitle());
        assertTrue(result.getContent().get(0).isBorrowed());

        assertEquals("resep seblak", result.getContent().get(1).getTitle());
        assertFalse(result.getContent().get(1).isBorrowed());
    }

    @Test
    void testGetDataPage_InvalidPage_ThrowsException() {
        int page = 5;
        int size = 2;
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Book> emptyPage = Page.empty(pageable);

        when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.getDataPage(page, size));
        assertEquals("invalid page", exception.getMessage());
    }

    @Test
    void testGetDataPage_NullPageAndSize_UsesDefaults() throws ServiceException {
        Pageable defaultPageable = PageRequest.of(0, 20);
        Page<Book> bookPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);

        when(bookRepository.findAll(defaultPageable)).thenReturn(bookPage);

        Page<BookDto> result = bookService.getDataPage(null, null);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void testGetBookById_Success() throws ServiceException {
        Book book = new Book(1L, "resep bakso", "cep juna", 2020);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDto result = bookService.getBookById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("resep bakso", result.getTitle());
    }

    @Test
    void testGetBookById_NotFound_ThrowsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bookService.getBookById(99L));
        assertEquals("id tidak ditemukan", exception.getMessage());
    }
}
