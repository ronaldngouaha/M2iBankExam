package com.m2i;

import com.m2i.model.account.Account;
import com.m2i.model.account.AccountStatus;
import com.m2i.model.account.AccountType;
import com.m2i.model.client.*;
import com.m2i.model.transaction.Block;
import com.m2i.model.transaction.Blockchain;
import com.m2i.model.transaction.Credit;
import com.m2i.model.transaction.Debit;
import com.m2i.utils.AccountValidationServiceImpl;
import com.m2i.utils.BlockchainServiceImpl;
import com.m2i.utils.CreditServiceImpl;
import com.m2i.utils.DebitServiceImpl;

import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * Hello world!
 *
 */
public class Main 
{
    public static void main( String[] args ) throws InterruptedException {


        Address address = new Address("asd","wew",City.NEW_YORK,Country.CANADA,20);
        Customer customer= new Customer("johs","sfd","+5145467223","john@gmail.com",address);
        customer.setClientStatus(ClientStatus.ACTIVE);
        Account account = new Account(customer, AccountType.CHECKING, AccountStatus.ACTIVE);

        BlockchainServiceImpl blockchainService= new BlockchainServiceImpl(new Blockchain());
        AccountValidationServiceImpl validationService= new AccountValidationServiceImpl();

        CreditServiceImpl creditService = new CreditServiceImpl(blockchainService, validationService);
        DebitServiceImpl debitService = new DebitServiceImpl(blockchainService, validationService);

        Credit credit = new Credit(account, BigDecimal.valueOf(2000.20), "Appro compte");

        creditService.doOperation(credit);



        ExecutorService executorService= Executors.newFixedThreadPool(4);

        for(int i=1;i<3;i++){
            int finalI = i;
            executorService.submit(
                    //Soumet une tâche à exécuter par un thread du pool. La tâche est définie comme une expression lambda qui affiche un message indiquant quelle tâche est exécutée et par quel thread.
                    ()->{
                        System.out.println("Tache "+ finalI+" executée par "+Thread.currentThread().getName());
                        Credit creditPool = new Credit(account, BigDecimal.valueOf((100*finalI)), "Appro compte par pool "+Thread.currentThread().getName());

                        creditService.doOperation(creditPool);

                    }
            );

        }
        for(int i=0;i<5;i++){
            int finalI = i;
            executorService.submit(
                    //Soumet une tâche à exécuter par un thread du pool. La tâche est définie comme une expression lambda qui affiche un message indiquant quelle tâche est exécutée et par quel thread.
                    ()->{
                        System.out.println("Tache "+ finalI+" executée par "+Thread.currentThread().getName());
                        Debit debitPool = new Debit(account, BigDecimal.valueOf(200*finalI), "Debit compte par pool "+Thread.currentThread().getName());
                        debitService.doOperation(debitPool);
                    }
            );

        }

        ScheduledExecutorService service= Executors.newScheduledThreadPool(1);

     /*   service.scheduleWithFixedDelay(()->{
            System.out.println("==========================================================================");
            blockchainService.getBlockchain().getBlocks()
                    .stream().forEach(System.out::println);
        },1, 1, TimeUnit.SECONDS);*/

        service.scheduleWithFixedDelay(()->{
            System.out.println("==========================================================================");
            System.out.println("BALANCE OF "+account.getClient().getClientName()+" is "+blockchainService.computeBalance(account));
            System.out.println("Account status is look? "+account.getLock().isLocked());
            System.out.println("Blockchain status is WriteLook? "+blockchainService.getBlockchain().getLock().isWriteLocked());

        },1, 1, TimeUnit.SECONDS);

        service.schedule(executorService::shutdown,20, TimeUnit.SECONDS);
        service.schedule(service::shutdown,20, TimeUnit.SECONDS);


    }
}
