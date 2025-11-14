package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.entity.*;
import com.demo.springbootdemo.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

@Controller
public class HolidayController {

    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private NotificationController notificationController;

    public Holiday getHolidayById(long id){
        return holidayRepository.findById(id);
    }

    public Holiday saveHoliday(Holiday holiday){
        return holidayRepository.save(holiday);
    }

    public void deleteHoliday(Holiday holiday){
         holidayRepository.delete(holiday);
    }

    public List<Holiday> getHolidaysListByUser(User user){
        return holidayRepository.findByUserOrderByAtDesc(user);
    }

    public List<Holiday> getHolidaysByHolidayStatus(HolidayStatus holidayStatus){
        return  holidayRepository.findByStatus(holidayStatus);
    }

    public List<Holiday> getHolidaysByHolidayStatusAndUser(HolidayStatus holidayStatus, User user){
        return  holidayRepository.findByStatusAndUser(holidayStatus,user);
    }

    public void HolidayRequestNotifyManager(User user) {
        User userTo;
        if(user.getTeam() == null || user.getRole() == Role.ADMIN) userTo = user.getCompany().getCompanyCreator();
        else userTo = user.getTeam().getManager();
        Notification notification = new Notification();
        notification.setTo(userTo);
        notification.setFrom(user);
        notification.setAt(new Date().getTime());
        notification.setTitleLabel("book_holiday_notification_title");
        notification.setMessageLabel("book_holiday_notification_message");
        webSocketService.sendNotification(notificationController.saveNotification(notification));
    }

    public void holidayRequestStatusChange(User userTo,User userFrom, HolidayStatus holidayStatus) {
        Notification notification = new Notification();
        notification.setTo(userTo);
        notification.setFrom(userFrom);
        notification.setAt(new Date().getTime());
        notification.setTitleLabel(
                holidayStatus.equals(HolidayStatus.APPROVED) ? "book_holiday_approved_notification_title" :
                        holidayStatus.equals(HolidayStatus.REJECTED) ? "book_holiday_rejected_notification_title" : "book_holiday_canceled_notification_title"
        );
        notification.setMessageLabel(
                holidayStatus.equals(HolidayStatus.APPROVED) ? "book_holiday_approved_notification_message" :
                        holidayStatus.equals(HolidayStatus.REJECTED) ? "book_holiday_rejected_notification_message" : "book_holiday_canceled_notification_message"
        );
        webSocketService.sendNotification(notificationController.saveNotification(notification));
    }
}
