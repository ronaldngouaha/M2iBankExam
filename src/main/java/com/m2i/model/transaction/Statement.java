package com.m2i.model.transaction;

import com.m2i.model.account.Account;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Statement {
        private Account account;
        private LocalDateTime statementStartDate;
        private LocalDateTime statementEndDate;
        private Integer maxEntry;


        public Statement(Account account, LocalDateTime statementStartDate, LocalDateTime statementEndDate, Integer maxEntry) {
            this.account = account;
            this.statementEndDate = statementEndDate;
            this.statementStartDate = statementStartDate;
            this.maxEntry= maxEntry;
        }

        public Account getAccount() {
            return account;
        }

        public LocalDateTime getStatementStartDate() {
            return statementStartDate;
        }

        public LocalDateTime getStatementEndDate() {
            return statementEndDate;
        }

    public Integer getMaxEntry() {
        return maxEntry;
    }
}
