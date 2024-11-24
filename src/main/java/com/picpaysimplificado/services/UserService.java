package com.picpaysimplificado.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.picpaysimplificado.domain.User;
import com.picpaysimplificado.domain.UserType;
import com.picpaysimplificado.repositories.UserRepository;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {

        if(sender.getUserType() == UserType.MERCHANT) {
            throw new Exception("Usuario do tipo logistica não está autorizado a realizar transação");
        }

        if(sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Saldo Insuficiente");
        }

    }

    public User findUserById(Long id) throws Exception {

        return this.userRepository.findUserById(id).orElseThrow(() -> new Exception("Usario não encontrado"));

    }

    public void saveUser(User user) {
        this.userRepository.save(user);
    }

}
