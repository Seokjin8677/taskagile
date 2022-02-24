package com.taskagile.web.payload;

import org.assertj.core.api.Assertions;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class RegistrationPayloadTests {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 빈 페이로드가 오면 실패하여야 한다.
     */
    @Test
    public void validate_blankPayload_shouldFail() {
        RegistrationPayload payload = new RegistrationPayload();
        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertThat(violations.size()).isEqualTo(3);
    }

    /**
     * 100글자 보다 큰 이메일 주소는 실패하여야 한다.
     *
     * 책에선 org.apache.commons.lang3.RandomStringUtils를 사용하였지만
     * 해당 라이브러리를 찾을 수 없어 assertj의 RandomString 클래스를 사용
     */
    @Test
    public void validate_blankPayloadWithEmailAddressLongerThan100_shouldFail() {
        // 로컬 파트는 64글자 까지 허용
        // http://www.rfc-editor.org/errata_search.php?rfc=3696&eid=1690
        int maxLocalPartLength = 64;
        String localPart = RandomString.make(maxLocalPartLength);
        int usedLength = maxLocalPartLength + "@".length() + ".com".length();
        String domain = RandomString.make(101 - usedLength);

        RegistrationPayload payload = new RegistrationPayload();
        payload.setEmailAddress(localPart + "@" + domain + ".com");
        payload.setUsername("MyUsername");
        payload.setPassword("MyPassword");

        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);
        assertThat(violations.size()).isEqualTo(1);
    }

    /**
     * 2글자 보다 적은 회원 이름은 실패하여야 한다.
     */
    @Test
    public void validate_payloadWithUsernameShorterThan2_shouldFail() {
        // given
        RegistrationPayload payload = new RegistrationPayload();
        String usernameTooShort = RandomString.make(1);
        payload.setUsername(usernameTooShort);
        payload.setEmailAddress("sunny@taskagile.com");
        payload.setPassword("MyPassword");
        // when
        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }

    /**
     * 50글자 보다 큰 회원 이름은 실패하여야 한다.
     */
    @Test
    public void validate_payloadWithUsernameLongerThan50_shouldFail() {
        // given
        RegistrationPayload payload = new RegistrationPayload();
        String usernameTooLong = RandomString.make(51);
        payload.setUsername(usernameTooLong);
        payload.setEmailAddress("sunny@taskagile.com");
        payload.setPassword("MyPassword");
        // when
        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }

    /**
     * 6글자보다 작은 비밀번호는 실패해야한다.
     */
    @Test
    public void validate_payloadWithPasswordShorterThan6_shouldFail() {
        // given
        RegistrationPayload payload = new RegistrationPayload();
        String passwordTooShort = RandomString.make(5);
        payload.setPassword(passwordTooShort);
        payload.setUsername("MyUsername");
        payload.setEmailAddress("sunny@taskagile.com");

        // when
        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }

    /**
     * 30글자가 넘는 비밀번호는 실패해야한다.
     */
    @Test
    public void validate_payloadWithPasswordLongerThan30_shouldFail() {
        // given
        RegistrationPayload payload = new RegistrationPayload();
        String passwordTooLong = RandomString.make(31);
        payload.setPassword(passwordTooLong);
        payload.setUsername("MyUsername");
        payload.setEmailAddress("sunny@taskagile.com");

        // when
        Set<ConstraintViolation<RegistrationPayload>> violations = validator.validate(payload);

        // then
        assertThat(violations.size()).isEqualTo(1);
    }

}
