package com.demo.tes.backend.service;

import com.demo.tes.backend.dto.BookDto;
import com.demo.tes.backend.dto.RequestBookDto;
import com.demo.tes.backend.dto.RequestBookPinjamDto;
import com.demo.tes.backend.dto.response.ResponseBookPinjamDto;
import com.demo.tes.backend.exception.security.ServiceException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBookService {

    Page<BookDto> getDataPage(Integer page, Integer size) throws ServiceException;
    BookDto getBookById(Long id) throws ServiceException;
    Boolean saveBook(RequestBookDto requestBookDto) throws ServiceException;
    Boolean updateBook(Long id, RequestBookDto requestBookDto) throws ServiceException;
    Boolean deleteBook(Long id) throws ServiceException;
    ResponseBookPinjamDto bookPinjam(RequestBookPinjamDto requestBookPinjamDto) throws ServiceException;
    String bookKembali(Long id) throws ServiceException;
    void sendNotif() throws ServiceException;
    void sendNotifOverdue() throws ServiceException;
}
