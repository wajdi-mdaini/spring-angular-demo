package com.demo.springbootdemo.model;

import com.demo.springbootdemo.entity.HolidayStatus;
import com.demo.springbootdemo.entity.HolidayType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookHolidayDTO {
    private Long from;
    private Long to;
    private HolidayType type;
    private HolidayStatus status;
    private int countedDays;
}
