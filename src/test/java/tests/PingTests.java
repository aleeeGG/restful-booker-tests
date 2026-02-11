package tests;

import model.auth.AuthRequest;
import model.auth.AuthResponse;
import model.booking.GetBookingIDsResponse;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PingTests extends BaseTest{
    @Test
    void Ping200() throws Exception {

        Response<ResponseBody> response =
                api.ping().execute();

        assertThat(response.code()).isEqualTo(201);
    }
}
