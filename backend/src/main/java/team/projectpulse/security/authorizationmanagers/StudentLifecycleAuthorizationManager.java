package team.projectpulse.security.authorizationmanagers;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import team.projectpulse.student.StudentSecurityService;

import java.util.function.Supplier;

@Component
public class StudentLifecycleAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final StudentSecurityService studentSecurityService;

    public StudentLifecycleAuthorizationManager(StudentSecurityService studentSecurityService) {
        this.studentSecurityService = studentSecurityService;
    }

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication,
                                                    RequestAuthorizationContext context) {
        Authentication current = authentication.get();
        boolean lifecycleRole = current != null && current.getAuthorities().stream().anyMatch(authority ->
                authority.getAuthority().equals("ROLE_instructor") || authority.getAuthority().equals("ROLE_admin"));
        Integer studentId = PathVariables.readId(context, "studentId");
        return new AuthorizationDecision(lifecycleRole && studentId != null
                && this.studentSecurityService.canManageLifecycle(studentId));
    }
}
