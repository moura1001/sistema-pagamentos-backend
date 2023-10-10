package com.moura.sistemapagamentosbackend.service.user;

import com.moura.sistemapagamentosbackend.model.user.User;
import com.moura.sistemapagamentosbackend.model.user.UserDTO;
import com.moura.sistemapagamentosbackend.util.exceptions.user.UserException;
import com.moura.sistemapagamentosbackend.util.exceptions.user.UserNotFoundException;
import com.moura.sistemapagamentosbackend.util.exceptions.user.UserPersistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User findUserById(Long id) throws UserException {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    public List<User> findAllUsersIn(List<Long> idList) throws UserException {
        List<User> users = repository.findUserByIdIn(idList);
        if (users.size() < idList.size()) {
            throw new UserNotFoundException();
        }

        return users;
    }

    public List<User> getAllUsers() throws UserException {
        try {
            return repository.findAll();
        } catch (RuntimeException e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }

    public long countUsers() throws UserException {
        try {
            return repository.countById();
        } catch (RuntimeException e) {
            throw new UserNotFoundException(e.getMessage());
        }
    }

    public void saveUser(User user) throws UserException {
        this.saveAllUsers(List.of(user));
    }

    public void saveAllUsers(List<User> users) throws UserException {
        try {
            repository.saveAll(users);

        } catch (RuntimeException e) {
            if (e instanceof DataIntegrityViolationException) {
                throw new UserPersistException("CPF/CNPJ e e-mails devem ser únicos no sistema");
            }

            throw new UserPersistException(e.getMessage());
        }
    }

    public User createUser(UserDTO user) throws UserException {
        User newUser = new User(user);
        saveUser(newUser);
        return newUser;
    }
}
