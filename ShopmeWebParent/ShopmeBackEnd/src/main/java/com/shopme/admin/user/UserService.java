package com.shopme.admin.user;

import com.shopme.commen.entity.Role;
import com.shopme.commen.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
//@ComponentScan
@Transactional
public class UserService {

    public static final int USER_PER_PAGE = 4;

    final private UserRepository userRepository;
    final private RoleRepository roleRepository;
    final private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);

        if (keyword != null) {
            return userRepository.findAll(keyword, pageable);
        }

        return userRepository.findAll(pageable);
    }

    public List<Role> listAllRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public User saveUser(User user) {
        boolean isUpdatingUser = (user.getId() != null);
        if (isUpdatingUser) {
            String oldPassword = userRepository.findById(user.getId()).get().getPassword();
            if (user.getPassword().isEmpty()) {
                user.setPassword(oldPassword);
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    public void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public boolean isEmailUnique(Integer id, String email) {
        // this method is not right
        // it does 2 jobs and is error pron on front side.
        User userByEmail = userRepository.getUserByEmail(email);
        if (userByEmail == null)
            return true;
        boolean isCreatingUser = (id == null);
        if (!isCreatingUser) {
            return Objects.equals(userByEmail.getId(), id);
        } else {
            return false;
        }

    }

    public User getUserById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new UserNotFoundException("Could not find any user with this ID: " + id);

        }
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0)
            throw new UserNotFoundException("Could not find any user with this ID: " + id);

        userRepository.deleteById(id);
    }

    public void updateUserEnableStatus(Integer id, boolean state) {
        userRepository.updateEnabledStatus(id, state);
    }
}
