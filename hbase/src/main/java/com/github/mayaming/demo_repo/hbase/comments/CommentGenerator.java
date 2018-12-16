package com.github.mayaming.demo_repo.hbase.comments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommentGenerator implements Runnable {
    private static final int NAMES_SIZE = 100;
    private static final int TOTAL_COMMENT_NUM = 10000000;
    private static final int GENERATE_THRESHOLD = 1000000;
    private static final int GENERATE_PER_BATCH = 100000;
    private static final Logger logger = LoggerFactory.getLogger(CommentGenerator.class);

    @Override
    public void run() {
        Thread.currentThread().setName("comments-producer");
        ArrayList<String> candNames = Utils.generateNames(NAMES_SIZE);
        long alreadyCommentNum = 0;
        while (alreadyCommentNum < TOTAL_COMMENT_NUM) {
            while (commentQueue.size() > GENERATE_THRESHOLD) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Utils.generateComments(GENERATE_PER_BATCH, commentQueue, candNames);
            logger.info("generated a batch of {} comments, queue size = {}", GENERATE_PER_BATCH, commentQueue.size());
            alreadyCommentNum += GENERATE_PER_BATCH;
        }
        logger.info("Producer ended with {} entries generated", alreadyCommentNum);
        producerEnded.set(true);
    }

    private Queue<Comment> commentQueue = new LinkedBlockingDeque<>();
    private AtomicBoolean producerEnded = new AtomicBoolean(false);

    public ArrayList<String> getCandNames() {
        return candNames;
    }

    private ArrayList<String> candNames;

    public Queue<Comment> getCommentQueue() {
        return commentQueue;
    }

    public AtomicBoolean getProducerEnded() {
        return producerEnded;
    }
}
