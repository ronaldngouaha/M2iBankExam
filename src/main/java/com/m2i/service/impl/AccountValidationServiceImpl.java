package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.account.AccountStatus;
import com.m2i.model.client.ClientStatus;
import com.m2i.service.AccountValidationService;

import java.math.BigDecimal;

public class AccountValidationServiceImpl implements AccountValidationService {
    @Override
    public boolean canOperate(Account account) {
        // Dummy implementation for account validation
        // In a real application, this would involve checking the account against a database or external service
        return account.getAccountStatus()== AccountStatus.ACTIVE && account.getClient().getClientStatus()== ClientStatus.ACTIVE;
    }


    @Override
    public boolean hasSufficientBalance(BigDecimal balance, BigDecimal amount) {
        // Dummy implementation for sufficient funds validation
        // In a real application, this would involve checking the account balance against the amount
        return balance.compareTo(amount)>=0; // Assume any amount less than 10,000 is valid for simplicity
    }

    @Override
    public void lockAccount(Account acc1, Account acc2, Runnable action) throws InterruptedException {

        boolean acquired = false;

        while (!acquired) {
            if (acc1.getLock().tryLock()) {
                try {
                    if (acc2.getLock().tryLock()) {
                        try {
                            action.run(); // 🔥 exécute l’opération métier
                            acquired = true;
                        } finally {
                            acc2.getLock().unlock();
                        }
                    }else{
                        System.out.println("==================================");
                        System.out.println("Current account "+acc2.getAccountNumber()+" is blocked");
                        System.out.println("==================================");
                    }
                } finally {
                    if (!acquired) {
                        acc1.getLock().unlock();
                    }
                }
            }else{

                System.out.println("==================================");
                System.out.println("Current account "+acc1.getAccountNumber()+" is blocked");
                System.out.println("==================================");
            }
            if (!acquired) Thread.sleep(1);
        }
    }

    @Override
    public void lockAccount(Account account, Runnable action) throws InterruptedException {
        boolean acquired= false;


        while (!acquired){
            if(account.getLock().tryLock()){
                try {
                    acquired=true;

                    action.run();
                }finally {
                    account.getLock().unlock();
                }
            }else{

                System.out.println("==================================");
                System.out.println("Current account "+account.getAccountNumber()+" is blocked");
                System.out.println("==================================");
            }
            if (!acquired) Thread.sleep(1);
        }
    }


}
