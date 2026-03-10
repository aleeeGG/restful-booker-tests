package model.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUpdateRequest {

    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;

}