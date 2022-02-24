package com.taskagile.web.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RegistrationPayload {

    @Size(min = 2, max = 50, message = "유저 이름은 최소 2글자에서 50글자 사이어야 합니다.")
    @NotNull
    private String username;

    @Email(message = "올바른 이메일 주소가 아닙니다.")
    @Size(max = 100, message = "이메일 주소는 최대 100글자 입니다.")
    @NotNull
    private String emailAddress;

    @Size(min = 6, max = 30, message = "비밀번호는 최소 6글자에서 30글자 사이어야 합니다.")
    @NotNull
    private String password;

}
