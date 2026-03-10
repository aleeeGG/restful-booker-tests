package model.booking;

import lombok.Data;

@Data
public class BookingResponse {

    private String firstname;
    private String lastname;
    private Integer totalPrice;
    private Boolean depositPaid;
    private BookingDates bookingdates;
    private String additionalNeeds;
}
