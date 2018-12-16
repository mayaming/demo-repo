package com.github.mayaming.demo_repo.hbase.comments;

import com.github.mayaming.demo_repo.hbase.HBaseUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommentsTest {
    private static final int NUM_WRITERS = 8;

    public static void main(String[] args) throws IOException {
        TableName tn = TableName.valueOf("comments-test");
        // Instantiating table descriptor class
        HTableDescriptor tableDescriptor = new HTableDescriptor(tn);

        // Adding column families to table descriptor
        tableDescriptor.addFamily(new HColumnDescriptor("info"));

        HBaseUtils.createTable(tableDescriptor, true);

        CommentGenerator commentGenerator = new CommentGenerator();
        Executors.newSingleThreadExecutor().submit(commentGenerator);
        ExecutorService writerPool = Executors.newFixedThreadPool(NUM_WRITERS);
        for (int i = 0; i < NUM_WRITERS; ++i) {
            writerPool.submit(new CommentWriter(commentGenerator, tn));
        }
    }
}
