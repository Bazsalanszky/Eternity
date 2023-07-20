package eu.toldi.infinityforlemmy.dto;

public class AccountLoginDTO {
    private String username_or_email;
    private String password;
    private String totp_2fa_token;

    public AccountLoginDTO(String username_or_email, String password, String totp_2fa_token) {
        this.username_or_email = username_or_email;
        this.password = password;
        this.totp_2fa_token = totp_2fa_token;
    }

    public String getUsername_or_email() {
        return username_or_email;
    }

    public void setUsername_or_email(String username_or_email) {
        this.username_or_email = username_or_email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTotp_2fa_token() {
        return totp_2fa_token;
    }

    public void setTotp_2fa_token(String totp_2fa_token) {
        this.totp_2fa_token = totp_2fa_token;
    }
}
