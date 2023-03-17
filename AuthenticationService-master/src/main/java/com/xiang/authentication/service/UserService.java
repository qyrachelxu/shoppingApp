package com.xiang.authentication.service;

import com.xiang.authentication.dao.RoleDao;
import com.xiang.authentication.dao.UserDao;
import com.xiang.authentication.dao.UserRoleDao;
import com.xiang.authentication.domain.entity.Role;
import com.xiang.authentication.domain.entity.User;
import com.xiang.authentication.domain.entity.UserRole;
import com.xiang.authentication.domain.request.RegistrationRequest;
import com.xiang.authentication.exception.ConstraintViolationException;
import com.xiang.authentication.exception.EmailExistedException;
import com.xiang.authentication.exception.UsernameExistedException;
import com.xiang.authentication.exception.ZeroOrManyException;
import com.xiang.authentication.security.AuthUserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    private UserDao userDao;
    private UserRoleDao userRoleDao;
    private RoleDao roleDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    @Autowired
    public void setUserRoleDao(UserRoleDao userRoleDao) {this.userRoleDao = userRoleDao;}
    @Autowired
    public void setRoleDao(RoleDao roleDao) {this.roleDao = roleDao;}

    public User getCurrentUser() throws ZeroOrManyException {return userDao.getCurrentUser();}

    public String getCurrentUserEmail() {return userDao.getCurrentUserEmail();}

    public void createNewUser(RegistrationRequest request)
            throws EmailExistedException, UsernameExistedException, ConstraintViolationException, ZeroOrManyException {
        // check duplicate username
        List<User> usersWithSameInfo = userDao.getUserByUsername(request.getUsername());
        if (usersWithSameInfo != null && usersWithSameInfo.size() > 0) {
            throw new UsernameExistedException("Username existed");
        }

        // check duplicate email
        usersWithSameInfo = userDao.getUserByEmail(request.getEmail());
        if (usersWithSameInfo != null && usersWithSameInfo.size() > 0) {
            throw new EmailExistedException("Email existed");
        }

        // check null
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmail();
        String authorizedEmail = getCurrentUserEmail();
        if (username == null || username.equals("") || password == null || password.equals("") || email == null || email.equals("")) {
            throw new ConstraintViolationException("Username, password, and email must be NOT NULL");
        }

        //check email format
        final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (!matcher.matches()) throw new ConstraintViolationException("Email must follow the format like name@domain, for example 'sample@sample.com'");
        //check email not the same as provided by hr
        if (!email.equals(authorizedEmail)) {
            throw new ConstraintViolationException("Email must be the same as the one which receives registration link from hr");
        }
        // create new user
        User user = userDao.createNewUser(request.getId(), request.getUsername(), request.getPassword(), request.getEmail());
        Role role = roleDao.getRoleById(2);
        userRoleDao.createNewUserRoleForNewEmployee(user, role);
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userDao.loadUserByUsername(username);
        if (!userOptional.isPresent()){
            throw new UsernameNotFoundException("Username does not exist");
        }

        User user = userOptional.get();
        return user;
    }

    public List<UserRole> getUserRoleByUser(User user) {
        List<UserRole> userRoles = userRoleDao.getAllUserRoleByUser(user);
        return userRoles;
    }


    public Role getRoleById(int roleId) {
        return roleDao.getRoleById(roleId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userDao.loadUserByUsername(username);
        if (!userOptional.isPresent()){
            throw new UsernameNotFoundException("Username does not exist");
        }
        User user = userOptional.get(); // database user
        return AuthUserDetail.builder() // spring security's userDetail
                .username(user.getUsername())
                .userId(user.getId())
                .password(user.getPassword())
                .email(user.getEmail())
                .authorities(getAuthoritiesFromUser(user))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    private List<GrantedAuthority> getAuthoritiesFromUser(User user) {
        List<GrantedAuthority> userAuthorities = new ArrayList<>();
        List<UserRole> userRoles = getUserRoleByUser(user);
        for (UserRole ur: userRoles) {
            Role role = getRoleById(ur.getRoleId().getId());
            userAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return userAuthorities;
    }

    public void updatePassword(String password) throws ZeroOrManyException {
        User user = getCurrentUser();
        userDao.updatePassword(user, password);
    }
}
