package web2.project.fms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import web2.project.fms.model.Account;
import web2.project.fms.service.AccountService;

import java.util.UUID;

@Component
public class SecurityUtils {

    private static AccountService accountService;

    @Autowired
    public SecurityUtils(AccountService accountService) {
        SecurityUtils.accountService = accountService;
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    public static void checkOwnershipOrAdmin(UUID accountId) {
        if (!isAdmin()) {
            String currentUsername = getCurrentUsername();
            Account account = accountService.getAccountById(accountId);
            if (!account.getUsername().equals(currentUsername)) {
                throw new SecurityException("Access denied. You are not the owner of this account.");
            }
        }
    }

    public static UUID getAuthenticatedAccountId() {
        String currentUsername = getCurrentUsername();
        Account account = accountService.getAccountByUsername(currentUsername);
        if (account != null) {
            return account.getId();
        }
        throw new SecurityException("Authenticated account not found.");
    }
}
