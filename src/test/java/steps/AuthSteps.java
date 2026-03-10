package steps;

import api.RestfulBookerApi;
import api.RetrofitClient;
import model.auth.AuthRequest;
import model.auth.AuthResponse;
import retrofit2.Response;

public class AuthSteps {

    private final RestfulBookerApi api;

    public AuthSteps(RestfulBookerApi api) {
        this.api = api;
    }
    public Response<AuthResponse> createToken(String login, String password) throws Exception {

        AuthRequest body = new AuthRequest(login, password);

        return api.createToken(body).execute();
    }
}