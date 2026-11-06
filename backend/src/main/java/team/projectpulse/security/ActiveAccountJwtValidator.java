package team.projectpulse.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import team.projectpulse.user.UserRepository;

@Component
public class ActiveAccountJwtValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INACTIVE_ACCOUNT = new OAuth2Error(
            "invalid_token", "The account is missing or deactivated.", null);

    private final UserRepository userRepository;

    public ActiveAccountJwtValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        Number userId = jwt.getClaim("userId");
        if (userId == null) {
            return OAuth2TokenValidatorResult.failure(INACTIVE_ACCOUNT);
        }
        return this.userRepository.findById(userId.intValue())
                .filter(user -> user.isEnabled())
                .map(user -> OAuth2TokenValidatorResult.success())
                .orElseGet(() -> OAuth2TokenValidatorResult.failure(INACTIVE_ACCOUNT));
    }
}
