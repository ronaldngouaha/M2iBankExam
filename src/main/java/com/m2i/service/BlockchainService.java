package com.m2i.service;

import com.m2i.model.account.Account;
import com.m2i.model.transaction.*;
import com.m2i.utils.AccessType;

import java.util.List;

public interface BlockchainService {

    // Permet de récupérer la liste de toutes les opérations associées à un compte donné.
    RequestResponse<List<FinancialOperation>>      getOperationsForAccount(Account account);
     // Permet d'enregistrer une nouvelle opération dans la blockchain.
     RequestResponse<List<NonFinancialOperation>>      getNonFinancialOperationsForAccount(Account account);

     RequestResponse<FinancialOperation> recordOperation(FinancialOperation operation);
        // Permet de récupérer la blockchain complète.
        RequestResponse<Blockchain>   getBlockchain();
     // Permet de récupérer la liste de tous les blocs présents dans la blockchain.
      RequestResponse<List<Block>>   getAllBlocks();
        // Permet de verrouiller la blockchain pour une opération spécifique, en fonction du type d'accès requis (lecture ou écriture), afin d'assurer la cohérence des données lors de l'exécution de l'action fournie.
     void lockBlockchain(Blockchain blockchain, AccessType access , Runnable action ) throws  InterruptedException;
}
