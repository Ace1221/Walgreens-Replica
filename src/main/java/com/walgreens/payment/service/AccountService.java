package com.walgreens.payment.service;

import com.walgreens.payment.repository.AccountRepository;
import com.walgreens.payment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public void createAccount(UUID user_id){
        UUID account_id = UUID.randomUUID();

        accountRepository.create_account(account_id, user_id);
    }
}
