package steps;

import api.RestfulBookerApi;
import api.RetrofitClient;
import model.booking.BookingResponse;
import model.booking.CreateResponse;
import model.booking.CreateUpdateRequest;
import model.booking.GetBookingIDsResponse;
import retrofit2.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingSteps {
    protected final String CONTENT_TYPE = "application/json";
    protected final String ACCEPT = "application/json";

    private final RestfulBookerApi api;

    public BookingSteps(RestfulBookerApi api) {
        this.api = api;
    }

    public BookingResponse getBookingById(int id) throws Exception {

        Response<BookingResponse> response =
                api.getBooking(ACCEPT, id)
                        .execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        return response.body();
    }

    public CreateResponse create(CreateUpdateRequest body) throws Exception {

        Response<CreateResponse> response =
                api.createBooking(
                        CONTENT_TYPE,
                        ACCEPT,
                        body
                ).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        return response.body();
    }

    public Response <List<GetBookingIDsResponse>> getBookingListIds(String firstname,
                                                                       String lastname,
                                                                       String checkin,
                                                                       String checkout) throws Exception {


        return api.getBookingIDs(firstname, lastname, checkin, checkout).execute();
    }
}