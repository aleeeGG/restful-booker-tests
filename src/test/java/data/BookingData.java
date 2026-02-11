package data;

import model.booking.BookingDates;
import model.booking.CreateUpdateRequest;

public class BookingData {

    public static CreateUpdateRequest defaultBooking() {

        BookingDates dates =
                new BookingDates(
                        "2024-05-10",
                        "2024-09-01"
                );

        return new CreateUpdateRequest(
                "Vlad",
                "Tretyakov",
                111,
                true,
                dates,
                "Breakfast"
        );
    }

    public static CreateUpdateRequest updateBooking() {

        BookingDates dates =
                new BookingDates(
                        "2024-06-28",
                        "2024-07-11"
                );

        return new CreateUpdateRequest(
                "Aleg",
                "Smirnov",
                222,
                false,
                dates,
                "Breakfast"
        );
    }
}
