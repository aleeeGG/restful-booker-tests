package model.booking;

public class CreateUpdateRequest {
    public String firstname;
    public String lastname;
    public int totalprice;
    public boolean depositpaid;
    public BookingDates bookingdates;
    public String additionalneeds;

    public CreateUpdateRequest(String firstname,
                               String lastname,
                               int totalprice,
                               boolean depositpaid,
                               BookingDates bookingdates,
                               String additionalneeds){

        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
        this.additionalneeds = additionalneeds;
    }
}
