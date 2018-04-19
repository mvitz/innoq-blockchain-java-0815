package com.innoq.blockchain0815;

import java.util.List;
import java.util.function.Predicate;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.sha256;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

public final class Block {

    public static final Block GENESIS =
        new Block(1, 0, 1917336, asList(new Transaction("b3c973e2-db05-4eb5-9668-3e81c7389a6d", 0, "I am Heribert Innoq")), "0");

    private final int index;
    private final long timestamp;
    private final long proof;
    private final List<Transaction> transactions;
    private final String previousBlockHash;

    public Block(int index, long timestamp, long proof, List<Transaction> transactions, String previousBlockHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.proof = proof;
        this.transactions = unmodifiableList(transactions);
        this.previousBlockHash = previousBlockHash;
    }

    int getIndex() {
        return index;
    }

    long getTimestamp() {
        return timestamp;
    }

    long getProof() {
        return proof;
    }

    List<Transaction> getTransactions() {
        return transactions;
    }

    String getPreviousBlockHash() {
        return previousBlockHash;
    }

    // TODO: optimize JSON representation
    String toJson() {
        return
            "{" +
                "\"index\":" + index + "," +
                "\"timestamp\":" + timestamp + "," +
                "\"proof\":" + proof + "," +
                "\"transactions\":" + transactions.stream().map(Transaction::toJson).collect(joining(",", "[", "]")) + "," +
                "\"previousBlockHash\":\"" + previousBlockHash + "\"" +
            "}";
    }

    private String hash;

    // TODO comparison without string maybe longer
    String hash() {
        if (hash == null) {
            hash = sha256().hashString(toJson(), UTF_8).toString();
        }

        return hash;
    }

    boolean isValid(Predicate<String> validator) {
        return validator.test(hash());
    }

    boolean isInvalid(Predicate<String> validator) {
        return isValid(validator.negate());
    }
}
