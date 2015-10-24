# redis-push

通过redis s/c 通信协议与redis server进行通信


## redis-push 服务框架设计图
 <img src="redis-push服务框架.png"/>
 
 
##初步单机测试结果

####测试环境：  

  主机：mac pro  

  处理器：2.2 GHz Intel Core i7  

  内存:16 GB 1600 MHz DDR3  


  redis服务和redis-push服务，应用都在单机上，拉取110万数据分发到各个应用总耗时30秒左右，每条数据10个字节  
  



##说明
 项目废弃掉，当做demo玩玩吧！^_^
 
##问题
   该项目最大的问题就是数据的丢失很严重！由于redis-push服务与redis服务拉取数的时候，以每秒五万的速度拉取，然后数据有缓冲，一旦服务出现宕机或者client断链的问题，数据就会部分丢失。  
 通过此次项目也发现这种的不适合对redis坐中间分发的服务，数据丢失是一大问题！