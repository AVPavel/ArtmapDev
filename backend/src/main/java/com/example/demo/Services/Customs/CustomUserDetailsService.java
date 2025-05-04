package com.example.demo.Services.Customs;

import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.DBServices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.DBModels.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found iwth username "+username));

        //Conversia rolului in GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
    public UserDetails createUserFromGoogle(Payload googleUser) {
        User newUser = new User();
        newUser.setEmail(googleUser.getEmail());
        newUser.setUsername(googleUser.getEmail()); // Using email as username
        newUser.setPassword("GOOGLE_OAUTH2_USER"); // Dummy password for social login
        newUser.setRole(User.Role.USER);

        return (UserDetails) userRepository.save(newUser);
    }

}
