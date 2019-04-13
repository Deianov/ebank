package com.softuni.ebank.services;

import com.softuni.ebank.bindingModels.UserBindingModel;
import com.softuni.ebank.entities.Role;
import com.softuni.ebank.entities.User;
import com.softuni.ebank.repositories.RoleRepository;
import com.softuni.ebank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = this.userRepository.findByUsername(username);

        if (userDetails == null) {
            throw new UsernameNotFoundException("Invalid user!");
        }

        return userDetails;
    }

    public boolean registerUser(UserBindingModel userBindingModel) {
        //Gets user from the database with given username
        User user = this.userRepository.findByUsername(userBindingModel.getUsername());

        //Returns false if we already have user with given username or password
        //and confirmPassword do not match
        if (user != null) {
            return false;
        } else if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())) {
            return false;
        }

        Role role = this.roleRepository.findByAuthority("USER");
        if (role == null) {
            return false;
        }

        // Creates new user with given username, email and password
        user = new User();
        user.setUsername(userBindingModel.getUsername());
        user.setEmail(userBindingModel.getEmail());

        //Encrypt password
        user.setPassword(this.bCryptPasswordEncoder.encode(userBindingModel.getPassword()));
        user.getAuthorities().add(role);

        //Save user in the database
        this.userRepository.save(user);

        return true;
    }

}

