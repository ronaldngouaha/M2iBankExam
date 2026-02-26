package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Statement {
        private Account accountNumber;
        private LocalDateTime statementStartDate;
        private LocalDateTime statementEndDate;


        public Statement(Account accountNumber, LocalDateTime statementStartDate, LocalDateTime statementEndDate) {
            this.accountNumber = accountNumber;
            this.statementEndDate = statementEndDate;
            this.statementStartDate = statementStartDate;
        }

        public Account getAccountNumber() {
            return accountNumber;
        }

        public LocalDateTime getStatementStartDate() {
            return statementStartDate;
        }

        public LocalDateTime getStatementEndDate() {
            return statementEndDate;
        }
}
