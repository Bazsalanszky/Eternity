package eu.toldi.infinityforlemmy.dto;

public class AuthDTO {

    private String auth;

    public AuthDTO(String auth) {
        this.auth = auth;
    }

    public String getAuth() {
        return auth;
    }
}
