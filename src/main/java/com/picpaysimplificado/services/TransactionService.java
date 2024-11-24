package com.picpaysimplificado.services;


import com.picpaysimplificado.domain.User;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;
import com.picpaysimplificado.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    // fazer chamadas http atraves dessa classe do spring
    @Autowired
    private RestTemplate restTemplate;

    public void createTransaction(TransactionDTO transactionDTO) throws Exception {

        User sender = this.userService.findUserById(transactionDTO.senderId());
        User receiver = this.userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender,transactionDTO.value());

        boolean authorize = this.authorizeTransaction(sender, transactionDTO.value());
        if(!authorize){
            throw  new Exception("Transação não autorizada");
        }

        Transaction transaction = new Transaction();

        transaction.setAmount(transactionDTO.value());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimeStamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);
        this.transactionRepository.save(transaction);

    }

    public boolean authorizeTransaction(User sender, BigDecimal value){

        ResponseEntity<Map> authorizationResponse =  this.restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

        if (authorizationResponse.getStatusCode() == HttpStatus.OK){

            String message = authorizationResponse.getBody().get("message").toString();
            return "Autorizado".equalsIgnoreCase(message);
        } else {
            return false;
        }

    }


}
