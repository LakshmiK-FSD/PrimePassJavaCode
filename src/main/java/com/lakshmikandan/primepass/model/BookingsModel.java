package com.lakshmikandan.primepass.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class BookingsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String userName;
    int userId;
    String seatId;
    String seatTId;
    String showDate;
    String showTime;
    String theaterName;
    String movie;
    int theaterId;
    int paidAmt;

}
