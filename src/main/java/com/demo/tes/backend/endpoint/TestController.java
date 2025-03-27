package com.demo.tes.backend.endpoint;

import com.demo.tes.backend.dto.BookDto;
import com.demo.tes.backend.dto.RequestBookDto;
import com.demo.tes.backend.dto.RequestBookPinjamDto;
import com.demo.tes.backend.dto.response.ResponseBookPinjamDto;
import com.demo.tes.backend.exception.security.ServiceException;
import com.demo.tes.backend.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final IBookService bookService;

    @GetMapping("/get-book")
    public ResponseEntity<Page<BookDto>> getDataPage(
            @Param("page") Integer page,
            @Param("page") Integer size
    ) throws ServiceException {
        return ResponseEntity.ok(bookService.getDataPage(page, size));
    }

    @GetMapping("/detail-book/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) throws ServiceException {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<Boolean> createBook(@RequestBody RequestBookDto requestBookDto) throws ServiceException {
        return ResponseEntity.ok(bookService.saveBook(requestBookDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateBook(@PathVariable Long id, @RequestBody RequestBookDto requestBookDto) throws ServiceException {
        return ResponseEntity.ok(bookService.updateBook(id, requestBookDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteBook(@PathVariable Long id) throws ServiceException {
        return ResponseEntity.ok(bookService.deleteBook(id));
    }
    @PostMapping("/book-pinjam")
    public ResponseEntity<ResponseBookPinjamDto> bookPinjam(@RequestBody RequestBookPinjamDto requestBookPinjamDto) throws ServiceException {
        return ResponseEntity.ok(bookService.bookPinjam(requestBookPinjamDto));
    }

    @PutMapping("/{pinjamId}/return")
    public ResponseEntity<String> returnBook(@PathVariable Long borrowingId) throws ServiceException {
        return ResponseEntity.ok(bookService.bookKembali(borrowingId));
    }

    @GetMapping("/send-notif")
    public ResponseEntity<String> sendNotif() throws ServiceException {
        bookService.sendNotif();
        return ResponseEntity.ok("notifikasi terkirim");
    }
}
