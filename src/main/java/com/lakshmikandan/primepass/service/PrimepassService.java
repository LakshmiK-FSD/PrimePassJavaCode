package com.lakshmikandan.primepass.service;

import com.lakshmikandan.primepass.model.*;
import com.lakshmikandan.primepass.repository.*;
import com.lakshmikandan.primepass.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Data
public class PrimepassService {
    @Autowired
    private PrimepassRepository primepassRepo;
    @Autowired
    private TheatersRepository theatersRepo;
    @Autowired
    private SeatRepository seatRepo;
    @Autowired
    private DateRepository datRepo;
    @Autowired
    private ShowsRepository showsRepo;
    @Autowired
    private ViewclsRepository viewclsRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BookingsRepository bookingsRepo;
    @Autowired
    private UsersRepository usersRepo;


    public List<PrimepassModel> getData() {
        List<PrimepassModel> primeModel = primepassRepo.findAll();
        return primeModel;
    }

    public TheatersModel theater(int theatid) {
        return theatersRepo.findById(theatid).orElse(null);
    }

    public PrimepassModel idMovie(int id) {
        return primepassRepo.findById(id).orElse(null);
    }

    public String adder(PrimepassModel prm) {
        primepassRepo.save(prm);
        return "added";
    }

    public List<String> findDates() {
        return datRepo.findByDistinctDates();
    }

    public DateModel dateModel(int date) {
        return datRepo.findById(date).orElse(null);
    }

    public ShowsModel timeModel(int tmId) {
        return showsRepo.findById(tmId).orElse(null);
    }

    public ViewclsModel viewcls(int clsId) {
        return viewclsRepo.findById(clsId).orElse(null);
    }

    public List<PrimepassModel> search(String search) {
        return primepassRepo.findBySearch(search);
    }

    @Transactional
    public ResponseEntity<?> booking(Map<String, List<String>> bkModel) {
        List<String> book = bkModel.get("seats");
        List<String> booker = bkModel.get("booker");
        String bookedBy = booker.get(0);
        for (String seats : book) {
            int seatId = Integer.parseInt(seats);
            System.out.println(seatId);
            SeatModel seatModel = seatRepo.findById(seatId).orElse(null);
            LocalDateTime now = LocalDateTime.now();

            if (seatModel != null && bookedBy != null) {
                if (seatModel.getBookedBy() != null) {
                    if (seatModel.getBookedBy().equals(bookedBy)) {
                        seatModel.setBookingTime(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        System.out.println("booked by seat id ===================================================>>>>");
                        seatModel.setStatus("BOOKED");
                        seatModel.setBookedBy(bookedBy);
                        seatRepo.save(seatModel);
                    }
//                if (seatModel.getExpireTime().isAfter(now)) {
//                    System.out.println("booked by time locked -------------------------------------------->>>>>>>>>>>>>>>>>");
//                    seatModel.setBookingTime(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//                    seatModel.setStatus("BOOKED");
//                    seatModel.setBookedBy(bookedBy);
//                    seatRepo.save(seatModel);
//                }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("value", "ok"));
    }

    public List<SeatModel> prizeSeats(List<String> seats) {
        List<SeatModel> seatsFn = new ArrayList<>();
        for (String e : seats) {
            int id = Integer.parseInt(e);
            SeatModel finded = seatRepo.findById(id).get();
            seatsFn.add(finded);
        }
        return seatsFn;
    }

    public List<BookingsModel> myPasses(String bearer) {
        String token = bearer.substring(7);
        String email = jwtUtil.unWrap(token);
        Optional<UsersModel> option = usersRepo.findByEmail(email);
        UsersModel user = option.get();
        int id = user.getId();
        List<BookingsModel> bookingsModels = bookingsRepo.findByUserId(id);
        return bookingsModels;
    }

    public List<BookingsModel> bookings(String bearer) {
        String token = bearer.substring(7);
        String email = jwtUtil.unWrap(token);
        Optional<UsersModel> option = usersRepo.findByEmail(email);
        UsersModel user = option.get();
        int id = user.getId();
        return bookingsRepo.findByUserId(id);
    }

    public synchronized ResponseEntity<String> booker(BookingsModel booker) {

        System.out.println(booker.getSeatId() + booker.getSeatTId());
        Optional<BookingsModel> check = bookingsRepo.findBySeatIdNo(booker.getSeatId(), booker.getSeatTId());
        if (check.isEmpty()) {
            bookingsRepo.save(booker);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Booking Successfull");
    }

    @Transactional
    public ResponseEntity<String> seatLock(List<String> lockfr, String bearer) {
        if(lockfr == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Sorry already locked");
        }
        String token = bearer.substring(7);
        String email = jwtUtil.unWrap(token);
        Optional<UsersModel> option = usersRepo.findByEmail(email);
        UsersModel user = option.get();
        int id = user.getId();
        for (String seat : lockfr) {
            int seatId = Integer.parseInt(seat);
            SeatModel fnSeat = seatRepo.findById(seatId).get();
            LocalDateTime now = LocalDateTime.now();
            if (fnSeat.getStatus().equals("AVAILABLE")) {
                fnSeat.setExpireTime(now.plusMinutes(3));
                fnSeat.setBookedBy(String.valueOf(id));
                fnSeat.setStatus("LOCKED");
                seatRepo.save(fnSeat);
            }
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Locked");
    }

    @Async("taskExecutor")
    @Scheduled(fixedRate = 100000)
    public void checkLockRelease() {
        List<SeatModel> locked = seatRepo.findByStatus("LOCKED");
        if(locked != null){
        for (SeatModel seat : locked) {
            LocalDateTime time = LocalDateTime.now();
            if (time.isAfter(seat.getExpireTime())) {
                seat.setStatus("AVAILABLE");
                seat.setBookedBy(null);
                seat.setExpireTime(null);
                seatRepo.save(seat);
              }
           }
        }
    }

    public PrimepassModel moveName(String mName) {
        return primepassRepo.findByMovename(mName);
    }
}