package com.demo.tes.backend.service;

import com.demo.tes.backend.dto.BookDto;
import com.demo.tes.backend.dto.RequestBookDto;
import com.demo.tes.backend.dto.RequestBookPinjamDto;
import com.demo.tes.backend.dto.response.ResponseBookPinjamDto;
import com.demo.tes.backend.exception.security.ServiceException;
import com.demo.tes.backend.model.Book;
import com.demo.tes.backend.model.BookPinjam;
import com.demo.tes.backend.repostory.IBookPinjamRepository;
import com.demo.tes.backend.repostory.IBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService {

    private final IBookRepository bookRepository;
    private final ConcurrentHashMap<String, List<BookDto>> cache = new ConcurrentHashMap<>();
    private final IBookPinjamRepository bookPinjamRepository;

    @Override
    @Cacheable(value = "BookDataPage", key = "#page + '-' + #size") // pakai redis jika ada
    public Page<BookDto> getDataPage(Integer page, Integer size) throws ServiceException {

        Pageable pageable = PageRequest.of(
                Optional.ofNullable(page).orElse(1) - 1,
                Optional.ofNullable(size).orElse(20));

        Page<Book> Books = bookRepository.findAll(pageable);
        if (page != null && page > Books.getPageable().getPageNumber()) {
            throw new ServiceException("invalid page");
        }
        List<BookDto> bookDtos = new ArrayList<>();
        Books.getContent().forEach(x -> {
            BookDto bookDto = new BookDto();
            bookDto.setId(x.getId());
            bookDto.setTitle(x.getTitle());
            bookDto.setAuthor(x.getAuthor());
            bookDto.setYearPublished(x.getYearPublished());
            boolean isBorrowed = bookPinjamRepository.findByBookIdAndIsReturnedFalse(x.getId()).isPresent();
            bookDto.setBorrowed(isBorrowed);

            bookDtos.add(bookDto);
        });

        return new PageImpl<>(bookDtos, pageable, Books.getTotalElements());
    }

    @Override
    public BookDto getBookById(Long id) throws ServiceException {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ServiceException("id tidak ditemukan"));
        return BookDto.builder().id(book.getId())
                .title(book.getTitle())
                .author(book.getTitle())
                .yearPublished(book.getYearPublished())
                .build();
    }

    @Override
    public Boolean saveBook(RequestBookDto requestBookDto) throws ServiceException {
        if (requestBookDto.getTitle() == null || requestBookDto.getTitle().trim().isEmpty()) {
            throw new ServiceException("Title cannot be empty");
        }
        if (requestBookDto.getAuthor() == null || requestBookDto.getAuthor().trim().isEmpty()) {
            throw new ServiceException("Author cannot be empty");
        }
        if (requestBookDto.getYearPublished() == null || requestBookDto.getYearPublished() < 1000) {
            throw new ServiceException("Year must be valid");
        }
        Book book = new Book();
        book.setTitle(requestBookDto.getTitle());
        book.setAuthor(requestBookDto.getAuthor());
        book.setYearPublished(requestBookDto.getYearPublished());
        bookRepository.save(book);
        return true;
    }

    @Override
    public Boolean updateBook(Long id, RequestBookDto requestBookDto) throws ServiceException {
        Book Book = bookRepository.findById(id).orElseThrow(() -> new ServiceException("Book not found with id: " + id));
        Book.setTitle(requestBookDto.getTitle());
        bookRepository.save(Book);
        return true;
    }

    @Override
    public Boolean deleteBook(Long id) throws ServiceException {
        if (!bookRepository.existsById(id)) {
            throw new ServiceException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        return true;
    }

    @Override
    public ResponseBookPinjamDto bookPinjam(RequestBookPinjamDto request) throws ServiceException {
        if (request.getBorrowerName() == null || request.getBorrowerName().trim().isEmpty()) {
            throw new ServiceException("Borrower name cannot be empty");
        }
        if (request.getBookId() == null) {
            throw new ServiceException("Book ID is required");
        }

        Optional<BookPinjam> existingBorrowing = bookPinjamRepository.findByBookIdAndIsReturnedFalse(request.getBookId());
        if (existingBorrowing.isPresent()) {
            throw new ServiceException("This book is currently borrowed and cannot be borrowed again until returned.");
        }
        Optional<Book> bookOptional = bookRepository.findById(request.getBookId());
        if (bookOptional.isEmpty()) {
            throw new ServiceException("Book not found");
        }
        Book book = bookOptional.get();

        BookPinjam bookPinjam = new BookPinjam();
        bookPinjam.setBorrowerName(request.getBorrowerName());
        bookPinjam.setBook(book);
        bookPinjam.setBorrowDate(request.getBorrowDate());
        bookPinjam.setReturnDate(request.getReturnDate());
        bookPinjam.setReturned(false);

        BookPinjam savedBorrowing = bookPinjamRepository.save(bookPinjam);

        return new ResponseBookPinjamDto(
                savedBorrowing.getId(),
                savedBorrowing.getBorrowerName(),
                book.getTitle(),
                savedBorrowing.getBorrowDate(),
                savedBorrowing.getReturnDate()
        );
    }

    @Override
    public String bookKembali(Long borrowingId) throws ServiceException {
        BookPinjam bookBorrowing = bookPinjamRepository.findById(borrowingId)
                .orElseThrow(() -> new ServiceException("Borrowing record not found"));

        if (bookBorrowing.isReturned()) {
            throw new ServiceException("This book has already been returned.");
        }

        bookBorrowing.setReturned(true);
        bookPinjamRepository.save(bookBorrowing);

        return "Book has been successfully returned!";
    }

    @Transactional
    @Override
    public void sendNotif() {
        List<BookPinjam> returnedBooks = bookPinjamRepository.findBooksToNotify();
        System.out.println("returnedBooks = " + returnedBooks);
        for (BookPinjam bb : returnedBooks) {
            System.out.println("Notifikasi: Buku '" + bb.getBook().getTitle() + "' telah dikembalikan pada " + bb.getReturnDate());
            bb.setNotified(true);
        }

        bookPinjamRepository.saveAll(returnedBooks);
    }

    @Transactional
    public void sendNotifOverdue() {
        List<BookPinjam> overdueBooks = bookPinjamRepository.findOverdueBooks();

        for (BookPinjam bb : overdueBooks) {
            System.out.println("Reminder: Buku '" + bb.getBook().getTitle() + "' belum dikembalikan. Mohon segera dikembalikan!");
        }
    }
}
