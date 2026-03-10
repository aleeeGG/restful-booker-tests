package data;

import model.booking.BookingDates;
import model.booking.CreateUpdateRequest;

public class BookingData {

    public CreateUpdateRequest defaultBooking() {

        BookingDates dates = BookingDates.builder()
                .checkin("2024-05-10")
                .checkout("2024-09-01")
                .build();

        return CreateUpdateRequest.builder()
                .firstname("Vlad")
                .lastname("Tretyakov")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(dates)
                .additionalneeds("Breakfast")
                .build();
    }

    public CreateUpdateRequest updateBooking() {

        BookingDates dates = BookingDates.builder()
                .checkin("2024-06-28")
                .checkout("2024-07-11")
                .build();

        return CreateUpdateRequest.builder()
                .firstname("Aleg")
                .lastname("Smirnov")
                .totalprice(222)
                .depositpaid(false)
                .bookingdates(dates)
                .additionalneeds("Breakfast")
                .build();
    }
}