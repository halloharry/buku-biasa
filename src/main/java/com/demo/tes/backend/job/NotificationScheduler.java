package com.demo.tes.backend.job;

import com.demo.tes.backend.exception.security.ServiceException;
import com.demo.tes.backend.service.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationScheduler {

    private final IBookService bookService;

    @Scheduled(cron = "0 0 0 * * ?") // run every midnight
    public void checkAndSendNotifications() throws ServiceException {
        bookService.sendNotif(); // Notifikasi buku yang sudah dikembalikan
        bookService.sendNotifOverdue();   // Reminder buku yang belum dikembalikan
    }
}
