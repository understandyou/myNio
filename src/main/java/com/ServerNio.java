package com;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class ServerNio {

    @Test//测试客户端，发送信息
    public void client() throws IOException {
        //socketChanel也可以用已经连接的socket中获得
        SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8833));
        //设置非阻塞模式
        clientChannel.configureBlocking(false);
        //非直接缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        Scanner scanner = new Scanner(System.in);


        String message = scanner.next();
        buf.put(("客户端信息:"+message).getBytes());
        buf.flip();//切换读模式
        //输出缓冲区
        clientChannel.write(buf);
        buf.clear();


        clientChannel.close();

    }

    //使用socketNio来代替原来的socket流
    //@Test
    public void server() throws IOException {
        //获得serverchannel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        //开启非阻塞模式
        serverChannel.configureBlocking(false);
        //绑定连接
        serverChannel.bind(new InetSocketAddress(8833));

        //创建选择器
        Selector selector = Selector.open();
        //添加选择器,并且指定监听的事件为“接收事件”
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        //轮询选择器上已经准备就绪的事件
        while (selector.select()>0)
        {
            //获得此选择器已经选择的键集合，然后在获取其迭代器
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            //迭代所有选择的选择器
            while (keyIterator.hasNext()){
                SelectionKey next = keyIterator.next();
                //判断是什么事件已经就绪
                if (next.isAcceptable()){
                    //连接就绪获得  连接
                    SocketChannel clicketChannel = serverChannel.accept();
                    //切换非阻塞模式
                    clicketChannel.configureBlocking(false);
                    //将此通道注册到选择器
                    clicketChannel.register(selector, SelectionKey.OP_READ);
                }else if(next.isReadable()){//判断是否为“读就绪”
                    //获得当前“读就绪的通道”
                    SocketChannel iChannel = (SocketChannel) next.channel();
                    //创建缓冲区
                    ByteBuffer bBuf = ByteBuffer.allocate(1024);
                    int len =0;
                    while ((len = iChannel.read(bBuf))>0){
                        //切换为读模式，
                        bBuf.flip();

                        //读取到的内容
                        System.out.println(new String(bBuf.array(),0,len));
                        //清空缓冲区
                        bBuf.clear();
                    }
                }
                //取消选择键selectionKey
                keyIterator.remove();
            }

        }


    }
}
