package com.lakshmikandan.primepass.controller;

import com.lakshmikandan.primepass.model.*;
import com.lakshmikandan.primepass.service.PrimepassService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Data
@RequestMapping("/users")
public class PrimepassController {
    @Autowired
    private PrimepassService primepass;
   @GetMapping("/movies")
    public List<PrimepassModel> movies(){
        return primepass.getData();
    }
    @GetMapping("/movies/{id}")
    public PrimepassModel idMovie(@PathVariable int id){
        return primepass.idMovie(id);
    }
    @GetMapping("/theater/{theatid}")
    public TheatersModel theatersModel(@PathVariable("theatid") int theatid){
       return primepass.theater(theatid);
    }
    @PostMapping("/movies")
    public String adder(@RequestBody PrimepassModel prm){
       return primepass.adder(prm);
    }
    @GetMapping("/theatdate")
    public List<String> findDates(){
       return primepass.findDates();
    }
    @GetMapping("/dates/{date}")
    public DateModel dateModel(@PathVariable int date){
       return primepass.dateModel(date);
    }
    @GetMapping("/time/{timeId}")
    public ShowsModel timeModel(@PathVariable("timeId") int tmId){
       return primepass.timeModel(tmId);
    }
    @GetMapping("/viewclass/{clsId}")
    public ViewclsModel viewcls(@PathVariable int clsId){
       return primepass.viewcls(clsId);
    }
    @GetMapping("/search")
    public List<PrimepassModel> search(@RequestParam("srh") String search){

       return primepass.search(search);
    }
    @PutMapping("/booking")
    public ResponseEntity<?> booking(@RequestBody Map<String,List<String>> book){
       return primepass.booking(book);
    }

    @GetMapping("/seats")
    public List<SeatModel> prizeSeats(@RequestParam List<String> seats){
       return  primepass.prizeSeats(seats);
    }

    @GetMapping("/passes")
    public List<BookingsModel> myPasses(@RequestHeader("Authorization") String bearer){
       return primepass.myPasses(bearer);
    }

    @GetMapping("/bookings")
    public List<BookingsModel> bookings(@RequestHeader("Authorization") String bearer){
        return primepass.bookings(bearer);
    }

    @PostMapping("/booker")
    public ResponseEntity<String> booker(@RequestBody BookingsModel booker){
     return primepass.booker(booker);
   }
    @PostMapping("/seatlock")
    public ResponseEntity<String> seatLock(@RequestParam List<String> lockfr,@RequestHeader("Authorization") String bearer){
        return primepass.seatLock(lockfr,bearer);
    }

    @GetMapping("/movename/{mName}")
    public PrimepassModel moveName(@PathVariable String mName){
       return primepass.moveName(mName);
    }
}
