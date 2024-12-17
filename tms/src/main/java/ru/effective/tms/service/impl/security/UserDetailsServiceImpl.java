package ru.effective.tms.service.impl.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.effective.tms.model.entity.security.AppUserDetails;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.repository.UserRepository;

/**
 * Service for working with {@link AppUserDetails}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * {@link User} Repository to search user by username.
     */
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found. Username is " + username)
                );
        log.info("load user with {} username.", user.getUsername());
        return new AppUserDetails(user);
    }
}
