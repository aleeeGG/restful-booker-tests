package model.booking;

import lombok.Data;

@Data
public class PartialUpdateRequest {
    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;

    public PartialUpdateRequest() {}
}
