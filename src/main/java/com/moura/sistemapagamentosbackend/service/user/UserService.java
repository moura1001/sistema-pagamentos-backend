package com.moura.sistemapagamentosbackend.service.user;

import com.moura.sistemapagamentosbackend.model.user.User;
import com.moura.sistemapagamentosbackend.util.exceptions.user.UserException;
import com.moura.sistemapagamentosbackend.util.exceptions.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new UserNotFoundException("usuário inválido encontrado na lista");
        }

        return users;
    }

    public void saveAllUsers(List<User> users) {
        repository.saveAll(users);
    }
}
