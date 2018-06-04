# 关于Handler执行顺序的问题

Netty中的所有handler都实现自ChannelHandler接口。按照输入输出分为ChannelInboundHandler、ChannelOutboundHandler两大类。

# ChannelInboundHandler
InboundHandler对从客户端发往服务器的报文进行处理,一般用来执行解码、读取客户端数据、进行业务处理等.

# ChannelOutBoundHandler
OutboundHandler对从服务器发往客户端的报文进行处理，一般用来进行编码、发送报文到客户端。


可以在pipeline中注册多个handler，但是需要注意的是：
1. ChannelInboundHandler按照注册的先后顺序执行
2. ChannelOutboundHandler按照注册的先后顺序逆序执行

比如：
```java
ch.pipeline.addLast(new InboundHandler1());
ch.pipeline.addLast(new InboundHandler2());
ch.pipeline.addLast(new OutboundHandler1());
ch.pipeline.addLast(new OutboundHandler2());
```
pipeline中注册了两个InboundHandler和两个OutboundHandler。

对于InboundHandler来说，先执行InboundHandler1，然后执行InboundHandler2。

对于OutboundHandler来说，先执行OutboundHandler2，然后执行OutboundHandler1。


比如现在client向server发送一个msg，那么server通过InboundHandler对client发送的msg进行解析，解析的顺序是先InboundHandler1，然后InboundHandler2。server解析完消息后向client发送消息，这时就是通过OutboundHandler向client发送消息，经过的顺序是OutboundHandler2，OutboundHandler1。


但是上面pipeline中添加handler还是有错误的，正确的做法是：
```java
ch.pipeline.addLast(new OutboundHandler1());
ch.pipeline.addLast(new OutboundHandler2());
ch.pipeline.addLast(new InboundHandler1());
ch.pipeline.addLast(new InboundHandler2());
```
或者
```java
ch.pipeline.addLast(new InboundHandler1());
ch.pipeline.addLast(new OutboundHandler1());
ch.pipeline.addLast(new OutboundHandler2());
ch.pipeline.addLast(new InboundHandler2());
```
总之，ChannelOutboundHandler 在注册的时候需要放在最后一个ChannelInboundHandler之前，否则将无法传递到ChannelOutboundHandler。


ChannelInboundHandler之间的传递，通过调用 ctx.fireChannelRead(msg) 实现；调用ctx.write(msg) 将传递到ChannelOutboundHandler。


程序运行，server端输出结果为：
```java
InboundHandler1.channelRead; 
InboundHandler2.channelRead; 
client msg: Are you ok?
OutboundHandler2.write
OutboundHandler1.write
InboundHandler1.channelReadComplete; 
InboundHandler1.channelReadComplete;
```

为什么会这样？

在InboundHandler执行完成需要调用OutboundHandler的时候，比如在InboundHandler调用ctx.writeAndFlush()方法，Netty是直接从该InboundHandler返回逆序的查找该InboundHandler之前的OutboundHandler，并非从Pipeline的最后一项Handler开始查找。所以Outbound一定要在最后一个InboundHandler之前。


# 注意
上面的分析还是有问题的。因为我们在Inbound中执行write方法的时候是采用的ctx.write，如果我们采用的是ctx.channel.write方法，ctx.channel()中的对应方法会从开始执行所有的Handler，即便ChannelOutboundHandler放在最后也会被执行。