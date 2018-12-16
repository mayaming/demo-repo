package com.github.mayaming.demo_repo.hbase.comments;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CommentWriter implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CommentWriter.class);
    private static final int WRITE_BATCH_SIZE = 10000;

    private CommentGenerator commentGenerator;
    private HTable hTable;
    private Map<String, Long> commentCntByName = new ConcurrentHashMap<>();
    private static final AtomicInteger seq = new AtomicInteger(1);

    public CommentWriter(CommentGenerator commentGenerator, TableName tableName) throws IOException {
        this.commentGenerator = commentGenerator;
        Configuration conn = HBaseConfiguration.create();
        hTable = new HTable(conn, tableName);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("comments-consumer-" + seq.getAndIncrement());

        AtomicBoolean producerEnded = commentGenerator.getProducerEnded();
        Queue<Comment> commentQueue = commentGenerator.getCommentQueue();

        byte[] colFamilyBytes = null;
        byte[] commentColBytes = null;
        List<Put> puts = new ArrayList<>(WRITE_BATCH_SIZE + 1);

        try {
            colFamilyBytes = "info".getBytes("UTF-8");
            commentColBytes = "comment".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        while (!producerEnded.get() || commentQueue.size() > 0) {
            Comment comment = commentQueue.poll();
            if (comment != null) {
                String name = comment.getName();
                if (!commentCntByName.containsKey(name)) {
                    commentCntByName.put(name, 0L);
                }
                Long seqL = commentCntByName.get(name);
                String seq = StringUtils.leftPad(seqL.toString(), 10, '0');
                commentCntByName.put(name, seqL + 1);
                String key = name + "-" + seq;
                try {
                    Put put = new Put(key.getBytes("UTF-8"));
                    put.addColumn(colFamilyBytes, commentColBytes, comment.getComment().getBytes("UTF-8"));
                    puts.add(put);
                    if (puts.size() == WRITE_BATCH_SIZE) {
                        hTable.batch(puts);
                        puts.clear();
                        logger.info("put {} entries into hbase", WRITE_BATCH_SIZE);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
