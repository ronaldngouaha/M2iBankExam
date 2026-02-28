package com.m2i.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiThreadedMiner {

    private final int difficulty;
    private final int threadCount;

    public MultiThreadedMiner(int difficulty, int threadCount) {
        this.difficulty = difficulty;
        this.threadCount = threadCount;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getThreadCount() {
        return threadCount;
    }

    private String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MiningResult mine(String data) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicBoolean found = new AtomicBoolean(false);
        String target = "0".repeat(difficulty);

        CompletableFuture<MiningResult> resultFuture = new CompletableFuture<>();

        for (int i = 0; i < threadCount; i++) {

            final int threadId = i;

            executor.submit(() -> {
                long nonce = threadId;

                while (!found.get()) {
                    String hash = sha256(data + nonce);

                    if (hash.startsWith(target)) {
                        found.set(true);
                        resultFuture.complete(new MiningResult(hash, nonce));
                        System.out.println(" Thread "+Thread.currentThread().getName()+" as found token "+hash);

                        return;
                    }

                    nonce += threadCount; // chaque thread saute de threadCount
                }
            });
        }

        MiningResult result = null; // attend le gagnant
        try {
            result = resultFuture.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        executor.shutdownNow(); // stoppe les autres threads

        return result;
    }

    public static class MiningResult {
        public final String hash;
        public final long nonce;

        public MiningResult(String hash, long nonce) {
            this.hash = hash;
            this.nonce = nonce;
        }

        public long getNonce() {
            return nonce;
        }

        public String getHash() {
            return hash;
        }
    }


}
