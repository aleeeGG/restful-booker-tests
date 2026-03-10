package model.booking;

import lombok.Data;

@Data
public class CreateResponse {

    private Integer bookingid;
    private BookingResponse booking;
}
