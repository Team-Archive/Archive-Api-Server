package site.archive.dto.v1.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.archive.common.ArchiveStringUtils;
import site.archive.domain.user.BaseUser;
import site.archive.domain.user.PasswordUser;
import site.archive.domain.user.UserRole;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
public class PasswordRegisterCommandV1 extends BasicRegisterCommandV1 {

    @NotEmpty(message = "필수 입력 항목입니다.")
    @Pattern(regexp = "(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9@$!%*#?&]{8,20}$",
             message = "비밀번호는 영문/숫자 를 꼭 포함하여 8~20자리로 입력해 주세요.")
    private String password;

    public PasswordRegisterCommandV1(String userMail, String password) {
        super(userMail);
        this.password = password;
    }

    @Override
    public BaseUser toUserEntity() {
        return new PasswordUser(getEmail(),
                                UserRole.GENERAL,
                                getPassword(),
                                ArchiveStringUtils.extractIdFromMail(getEmail()));
    }

}
