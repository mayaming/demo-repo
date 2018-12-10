## Consul的准备

1. 下载consul，地址在[Download Consul](https://www.consul.io/downloads.html)；
2. 解压下载的zip文件，在当前目录下会有consul的可执行文件（consul很简单，就一个文件，golang带来的好处）；
3. 运行`build.sh`来构建consul镜像，构建完成后运行`docker images`应该会看到名为`my-consul`的镜像；
4. 到docker目录下运行`docker-compose up`，启动3 docker节点组成的伪集群；
5. 启动集群成功后，到`http://localhost:8501`里查看consul UI。

## 启动程序

启动服务端时，注意在JVM args里添加-Dspring.profiles.active=svc；客户端时添加-Dspring.profiles.active=client。这样才能让程序找到正确的配置文件：bootstrap-svc.yml和bootstrap-client.yml。