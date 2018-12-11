# HBase 样例汇总

## 准备工作

注意把$HADOOP_HOME/etc/hadoop和$HBASE_HOME/conf加到classpath里，里面含有的core-site.xml, hdfs-site.xml, hbase-site.xml等文件告知程序hadoop/hbase的配置在哪里。

## 性能评估

hbase模块的pom.xml里引入了如下模块：
```xml
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-testing-util</artifactId>
            <version>${hbase.version}</version>
            <scope>test</scope>
        </dependency>
```
从而引入hbase自带的一些测试类，比如`org.apache.hadoop.hbase.PerformanceEvaluation`，用来在本地执行一些性能测试。
在IntelliJ里新建一个"Run"，main class是`org.apache.hadoop.hbase.PerformanceEvaluation`，参数是`--table="testtable" --rows=100000 sequentialWrite 10`，可以在本地把性能测试跑起来，向集群提交Map Reduce任务。
同理对`org.apache.hadoop.hbase.mapreduce.RowCounter`也适用，通过Map Reduce统计行数。