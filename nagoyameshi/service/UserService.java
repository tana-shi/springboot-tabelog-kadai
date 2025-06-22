package com.example.nagoyameshi.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(SignupForm signupForm) {
        User user = new User();
        Role role = roleRepository.findByName("ROLE_FREE_MEMBER");

        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());

        if (!signupForm.getBirthday().isEmpty()) {
            user.setBirthday(LocalDate.parse(signupForm.getBirthday(), DateTimeFormatter.ofPattern("yyyyMMdd")));
        } else {
            user.setBirthday(null);
        }

        if (!signupForm.getOccupation().isEmpty()) {
            user.setOccupation(signupForm.getOccupation());
        } else {
            user.setOccupation(null);
        }

        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false);

        return userRepository.save(user);
    }

    @Transactional
    public void updateUser(UserEditForm userEditForm, User user) {
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());

        if (!userEditForm.getBirthday().isEmpty()) {
            user.setBirthday(LocalDate.parse(userEditForm.getBirthday(), DateTimeFormatter.ofPattern("yyyyMMdd")));
        } else {
            user.setBirthday(null);
        }

        if (!userEditForm.getOccupation().isEmpty()) {
            user.setOccupation(userEditForm.getOccupation());
        } else {
            user.setOccupation(null);
        }

        user.setEmail(userEditForm.getEmail());
        userRepository.save(user);
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    public boolean isEmailChanged(UserEditForm userEditForm, User user) {
        return !userEditForm.getEmail().equals(user.getEmail());
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> findUsersByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable) {
        return userRepository.findByNameLikeOrFuriganaLike("%" + nameKeyword + "%", "%" + furiganaKeyword + "%", pageable);
    }

    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    }

    public long countUsersByRole_Name(String roleName) {
        return userRepository.countByRole_Name(roleName);
    }

    @Transactional
    public void saveStripeCustomerId(User user, String stripeCustomerId) {
        user.setStripeCustomerId(stripeCustomerId);
        userRepository.save(user);
    }

    @Transactional
    public void updateRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName);
        user.setRole(role);
        userRepository.save(user);
    }

    public void refreshAuthenticationByRole(String newRole) {
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(newRole));
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(currentAuthentication.getPrincipal(), currentAuthentication.getCredentials(), simpleGrantedAuthorities);
       
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

    // ✅ 有料会員の人数を取得
    public long countPaidMembers() {
        return userRepository.countByRole_Name("ROLE_PAID_MEMBER");
    }

    // ✅ 無料会員の人数を取得（全体 - 有料）
    public long countFreeMembers() {
        return userRepository.count() - countPaidMembers();
    }
}
