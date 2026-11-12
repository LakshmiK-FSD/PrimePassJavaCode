package com.lakshmikandan.primepass.repository;

import com.lakshmikandan.primepass.model.BookingsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingsRepository extends JpaRepository<BookingsModel,Integer> {
    List<BookingsModel> findByUserId(int id);
    @Query(value = "select * from bookings_model where seat_id =:seatId and seattid =:seatTId ;", nativeQuery = true)
    Optional<BookingsModel> findBySeatIdNo(@Param("seatId") String seatId,@Param("seatTId")String seatTId);
}
