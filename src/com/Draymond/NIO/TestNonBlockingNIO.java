package com.Draymond.NIO;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/*
* 一、使用NIO 完成网络通信的三个核心
*
* 1.通信 （Channel) ： 负责连接
*
*       java.nio.channels.Channel 接口
*           |--SelectableChannel
*               |--SocketChannel
*               |--ServerSocketChannel
*               |--DatagramChannel
*
*               |--Pipe.SinkChannel
*               |--Pipd.SourceChannel
*
*  2.缓冲区(Buffer) : 负责数据的存取
*
*  3.选择器 （Selector) : 是SelectableChannel的多路复用，用于监控SelecrtableChannel的IO状况
* */
public class TestNonBlockingNIO {

    //客户端
    @Test
    public void client() throws IOException {
        //获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        //切换非阻塞模式
        socketChannel.configureBlocking(false);

        //分配指定大小的缓冲区
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

        //发送数据
        Scanner scanner=new Scanner(System.in);

        while(scanner.hasNext()){
            String s = scanner.next();
            byteBuffer.flip();
            byteBuffer.put((new Date().toString() + "\n" + s).getBytes());
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //关闭通道
        socketChannel.close();


    }



    //服务端
    @Test
    public void server() throws IOException{
        //1. 获取通道
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();

        //2. 切换非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //3. 绑定连接
        serverSocketChannel.bind(new InetSocketAddress(9898));


        //4.获取选择器
        Selector selector=Selector.open();

        //5.将通道注册到选择器，并且指定“监听接受事件"
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //6. 轮询式的获取选择器上已经“准备就绪”的事件
        while(selector.select()>0){

            //7.获取当前选择器中所有注册的“选择键（已就绪的监听事件)"
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while(it.hasNext()){
                //获取准备”就绪”事件
                SelectionKey selectionKey = it.next();

                //9.判断具体是什么时间准备就绪
                if(selectionKey.isAcceptable()){
                    //10.若“接受就绪”.获取客户端链接就绪
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    //11.切换非阻塞模式
                    socketChannel.configureBlocking(false);

                    //12. 将该通道注册到选择器上
                    socketChannel.register(selector, SelectionKey.OP_READ);

                }else if(selectionKey.isReadable()){
                    //13. 获取当前选择器上“读就绪”状态的通道
                    SocketChannel selectableChannel = (SocketChannel) selectionKey.channel();

                    //14.读取数据
                    ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

                    int len=0;

                    while((len = ( selectableChannel).read(byteBuffer)) > 0 ){
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                }

                //15. 取消选择键 SelectionKey
                it.remove();
            }
        }
    }
}
