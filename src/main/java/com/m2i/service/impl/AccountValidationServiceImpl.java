package com.m2i.service.impl;

import com.m2i.model.account.Account;
import com.m2i.model.account.AccountStatus;
import com.m2i.model.client.ClientStatus;
import com.m2i.model.transaction.RequestResponse;
import com.m2i.model.transaction.ResponseStatusCode;
import com.m2i.service.AccountValidationService;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AccountValidationServiceImpl implements AccountValidationService {
    @Override
    public RequestResponse<Boolean> canOperate(Account account) {
        if(account==null) return new RequestResponse<>(ResponseStatusCode.ACCOUNT_NOT_FOUND, "Account cannot be null", false);
        // Dummy implementation for account validation
        // In a real application, this would involve checking the account against a database or external service
        return new RequestResponse<>(ResponseStatusCode.ACCEPTED, "Account is not active or client is not active", account.getAccountStatus() == AccountStatus.ACTIVE && account.getClient().getClientStatus() == ClientStatus.ACTIVE);
    }


    @Override
    public RequestResponse<Boolean>  hasSufficientBalance(BigDecimal balance, BigDecimal amount) {
        // Dummy implementation for sufficient funds validation
        // In a real application, this would involve checking the account balance against the amount
        return new RequestResponse<>(ResponseStatusCode.ACCEPTED, "Checking Balance Completed", balance.compareTo(amount) >= 0); // Assume any amount less than 10,000 is valid for simplicity
    }

    @Override
    public void lockAccount(Account acc1, Account acc2, Runnable action) throws InterruptedException {
        // Ordre stable pour éviter deadlocks
        Account first = acc1.getAccountId() < acc2.getAccountId() ? acc1 : acc2;
        Account second = acc1.getAccountId() < acc2.getAccountId() ? acc2 : acc1;

        long timeoutMs = 50000; // Timeout global pour l'opération
        long startTime = System.currentTimeMillis();
        long sleepTime = 1; // Backoff initial
        boolean acquired = false;

        while (!acquired) {
            long elapsed = System.currentTimeMillis() - startTime;
            long remainingTime = timeoutMs - elapsed;
            if (remainingTime <= 0) {
                throw new RuntimeException("Impossible de verrouiller les comptes "
                        + acc1.getAccountNumber() + " et " + acc2.getAccountNumber()
                        + " après " + timeoutMs + "ms");
            }

            // Essayer de verrouiller le premier compte
            if (first.getLock().tryLock(remainingTime, TimeUnit.MILLISECONDS)) {
                try {
                    elapsed = System.currentTimeMillis() - startTime;
                    remainingTime = timeoutMs - elapsed;
                    if (remainingTime <= 0) throw new RuntimeException("Timeout sur second compte");

                    // Essayer de verrouiller le second compte
                    if (second.getLock().tryLock(remainingTime, TimeUnit.MILLISECONDS)) {
                        try {
                            action.run(); // Exécuter l'opération métier
                            acquired = true;
                        } finally {
                            second.getLock().unlock();
                        }
                    }
                } finally {
                    if (!acquired) first.getLock().unlock();
                }
            }

            if (!acquired) {
                // Backoff exponentiel + randomisation pour éviter que tous les threads retentent simultanément
                long jitter = ThreadLocalRandom.current().nextLong(0, 5); // 0-5ms aléatoire
                Thread.sleep(Math.min(50, sleepTime) + jitter);
                sleepTime = Math.min(50, sleepTime * 2);
            }
        }
    }
    @Override
    public void lockAccount(Account account, Runnable action) throws InterruptedException {
        long timeoutMs = 5000; // temps max pour essayer de verrouiller
        long startTime = System.currentTimeMillis();
        long sleepTime = 1; // backoff initial en ms
        boolean acquired = false;

        while (!acquired) {
            if (account.getLock().tryLock()) {
                try {
                    acquired = true;
                    action.run();
                } finally {
                    account.getLock().unlock();
                }
            } else {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= timeoutMs) {
                    throw new RuntimeException("Impossible de verrouiller le compte "
                            + account.getAccountNumber()
                            + " après " + timeoutMs + "ms");
                }
                // Affiche le log seulement toutes les 50ms pour limiter le spam
                if (elapsed % 50 < sleepTime) {
                    System.out.println("Compte " + account.getAccountNumber() + " est bloqué, retry...");
                }

                Thread.sleep(sleepTime);
                // Exponentiel jusqu'à 50ms max pour limiter la charge CPU
                sleepTime = Math.min(50, sleepTime * 2);
            }
        }
    }

}
