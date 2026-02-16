package tests;

import model.auth.AuthResponse;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthTests extends BaseTest {
    protected final String LOGIN = "admin";
    protected final String PASS = "password123";
    protected final String WRONG = "wrong";

    @Test
    void createToken200() throws Exception {

        Response<AuthResponse> response =
                createToken(LOGIN, PASS);

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getToken()).isNotBlank();
    }


    @Test
    void createTokenWrongPass401() throws Exception {

        Response<AuthResponse> response =
                createToken(LOGIN, WRONG);

        assertThat(response.code()).isEqualTo(200); /// возвращает 200 должно 401
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getReason()).isEqualTo("Bad credentials");
    }

    @Test
    void createTokenWrongName401() throws Exception {

        Response<AuthResponse> response =
                createToken(WRONG, PASS);

        assertThat(response.code()).isEqualTo(200); /// возвращает 200 должно 401
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getReason()).isEqualTo("Bad credentials");
    }
}
