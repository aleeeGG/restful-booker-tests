package tests;

import api.RestfulBookerApi;
import api.RetrofitClient;
import model.auth.AuthResponse;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import steps.AuthSteps;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthTests extends BaseTest {
    protected final String LOGIN = "admin";
    protected final String PASS = "password123";
    protected final String WRONG = "wrong";
    protected final String BAD = "Bad credentials";


    private final RestfulBookerApi api =
            RetrofitClient.getClient(
                    RestfulBookerApi.class,
                    config.baseUrl()
            );
    @Test
    void createToken200() throws Exception {

        AuthSteps authSteps = new AuthSteps(api);
        Response<AuthResponse> response =
                authSteps.createToken(LOGIN, PASS);

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getToken()).isNotBlank();
    }


    @Test
    void createTokenWrongPass401() throws Exception {

        AuthSteps authSteps = new AuthSteps(api);
        Response<AuthResponse> response =
                authSteps.createToken(LOGIN, WRONG);

        assertThat(response.code()).isEqualTo(200); /// возвращает 200 должно 401
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getReason()).isEqualTo(BAD);
    }

    @Test
    void createTokenWrongName401() throws Exception {

        AuthSteps authSteps = new AuthSteps(api);
        Response<AuthResponse> response =
                authSteps.createToken(WRONG, PASS);

        assertThat(response.code()).isEqualTo(200); /// возвращает 200 должно 401
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getReason()).isEqualTo(BAD);
    }
}
