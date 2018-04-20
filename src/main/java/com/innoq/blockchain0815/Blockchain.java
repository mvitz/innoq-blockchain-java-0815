package com.innoq.blockchain0815;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.innoq.blockchain0815.Block.GENESIS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

public final class Blockchain {

    private final List<Block> blocks = new ArrayList<>();
    private final Miner miner;

    public Blockchain(Miner miner) {
        this.miner = miner;
        blocks.add(GENESIS);
    }

    public int getCurrentBlockHeight() {
        return blocks.size();
    }

    public MiningResult mine() throws Exception {
        final Block last = blocks.get(getCurrentBlockHeight() - 1);

        Stopwatch sw = Stopwatch.createStarted();
        final Block next = generateSuccessorFor(last);
        final String message = "Mined a new block in " + sw.stop() + ".";

        blocks.add(next);
        return new MiningResult(message, next);
    }

    private Block generateSuccessorFor(Block block) throws Exception {
        final int index = block.index + 1;
        final long timestamp = System.currentTimeMillis();
        final String previousHash =
            new BlockHasher(new BlockSerializer(block)).hash().toString();

        return miner.mine(index, timestamp, emptyList(), previousHash);
    }

    public String toJson() {
        return
            "{" +
                "\"blocks\":" + toJson(blocks) + "," +
                "\"blockHeight\":" + getCurrentBlockHeight() +
            "}";
    }

    private static String toJson(List<Block> blocks) {
        return blocks.stream()
            .map(BlockSerializer::new)
            .map(BlockSerializer::serialize)
            .map(bytes -> new String(bytes, UTF_8))
            .collect(joining(",", "[", "]"));
    }

    public static final class MiningResult {

        private final String message;
        private final Block block;

        public MiningResult(String message, Block block) {
            this.message = message;
            this.block = block;
        }

        public String toJson() {
            return
                "{" +
                    "\"message\":\"" + message + "\"," +
                    "\"block\":" + new String(new BlockSerializer(block).serialize(), UTF_8) +
                "}";
        }
    }
}
