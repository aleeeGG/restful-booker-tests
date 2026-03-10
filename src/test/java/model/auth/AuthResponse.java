package model.auth;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String reason;
}
