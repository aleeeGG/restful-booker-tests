package tests;

import api.ReqresApi;
import api.RetrofitClient;

import model.auth.AuthRequest;
import model.auth.AuthResponse;
import model.booking.BookingResponse;
import model.booking.CreateUpdateRequest;
import model.booking.CreateResponse;

import model.booking.GetBookingIDsResponse;
import org.junit.jupiter.api.BeforeEach;
import retrofit2.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseTest {

    protected ReqresApi api;

    @BeforeEach
    void setUp() {
        api = RetrofitClient.api();
    }

    protected BookingResponse getBookingById(int id) throws Exception {

        Response<BookingResponse> response =
                api.getBooking("application/json", id)
                        .execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        return response.body();
    }


    protected CreateResponse create(CreateUpdateRequest body) throws Exception {

        Response<CreateResponse> response =
                api.createBooking(
                        "application/json",
                        "application/json",
                        body
                ).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        return response.body();
    }


    protected Response<AuthResponse> createToken(String login, String password) throws Exception {

        AuthRequest body = new AuthRequest(login, password);

        return api.createToken(body).execute();
    }

    protected Response <List<GetBookingIDsResponse>> getBookingListIds(String firstname,
                                                       String lastname,
                                                       String checkin,
                                                       String checkout) throws Exception {


        return api.getBookingIDs(firstname, lastname, checkin, checkout).execute();
    }
}