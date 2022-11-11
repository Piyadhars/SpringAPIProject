package com.api.springapiproject.Services;

import com.api.springapiproject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtService implements UserDetailsService {

    @Autowired
    UserRepository jwtUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<com.api.springapiproject.Model.User> jwtUser = jwtUserRepository.findById(email);
        if (jwtUser == null) {
            throw new UsernameNotFoundException("email Not found" + email);
        }
        return new org.springframework.security.core.userdetails.User(jwtUser.get().getEmail(),jwtUser.get().getPassword(), new ArrayList<>());
    }
}