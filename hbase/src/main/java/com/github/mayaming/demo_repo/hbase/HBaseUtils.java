package com.github.mayaming.demo_repo.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HBaseUtils {

    private static final Logger logger = LoggerFactory.getLogger(HBaseUtils.class);

    public static void createTable(HTableDescriptor tableDescriptor, boolean deleteIfExists) throws IOException {
        // Instantiating configuration class
        Configuration con = HBaseConfiguration.create();

        // Instantiating HbaseAdmin class
        HBaseAdmin admin = new HBaseAdmin(con);
        TableName tn = tableDescriptor.getTableName();

        // Execute the table through admin
        if (deleteIfExists && admin.tableExists(tn)) {
            admin.disableTable(tn);
            admin.deleteTable(tn);
            logger.info("Table {} exists, delete it first", tn.toString());
        }

        admin.createTable(tableDescriptor);
        logger.info("Table {} created", tn.toString());
    }
}
