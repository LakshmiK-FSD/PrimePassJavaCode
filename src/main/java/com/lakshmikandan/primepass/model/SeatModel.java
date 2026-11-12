package com.lakshmikandan.primepass.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class SeatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int setid;
    String seatId;
    int number;
    int prize;
    String rowId;
    String status;
    String bookedBy;
    String bookingTime;
    LocalDateTime expireTime;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="seatrow_id")
    public SeatsrowModel seatsrow;
}
