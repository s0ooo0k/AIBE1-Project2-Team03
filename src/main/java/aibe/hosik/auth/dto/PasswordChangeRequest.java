package aibe.hosik.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public class PasswordChangeRequest {
    @NotBlank
    @Email(message = "유효한 이메일 형식이어야 합니다")
    private String email;
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}