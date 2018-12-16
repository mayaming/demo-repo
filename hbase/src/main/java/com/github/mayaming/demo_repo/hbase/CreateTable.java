package com.github.mayaming.demo_repo.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;

public class CreateTable {

    public static void main(String[] args) throws IOException {

        TableName tn = TableName.valueOf("test");
        // Instantiating table descriptor class
        HTableDescriptor tableDescriptor = new HTableDescriptor(tn);

        // Adding column families to table descriptor
        tableDescriptor.addFamily(new HColumnDescriptor("personal"));
        tableDescriptor.addFamily(new HColumnDescriptor("professional"));

        HBaseUtils.createTable(tableDescriptor, true);
    }
}