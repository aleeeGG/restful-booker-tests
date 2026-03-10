package tests;

import api.RestfulBookerApi;
import api.RetrofitClient;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class PingTests extends BaseTest{

    private final RestfulBookerApi api =
            RetrofitClient.getClient(
                    RestfulBookerApi.class,
                    config.baseUrl()
            );

    @Test
    void Ping200() throws Exception {

        Response<ResponseBody> response =
                api.ping().execute();

        assertThat(response.code()).isEqualTo(201);
    }
}
