package com.Draymond.NIO;

/* 一、使用 NIO 完成网络通信
 *
 *  1.通道 （Channel） ：负责连接
 *
 *  java.nio.channels.Channel 接口：
 *       |--SelectableChannel
 *       |--ServerChaennel
 *       |--ServerSocketChannel
 *       |--DatagramChannel
 *
 *
 *  2.缓冲区 (Buffer) : 负责数据的存取
 *
 *  3.选择器(Selector) : 是SelectableChannel 的多路复用，用于监控SelectableChannel的IO状况
 *
 */

import org.junit.Test;
import sun.java2d.pipe.BufferedBufImgOps;

import javax.xml.stream.events.StartDocument;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestBlockingNIO {

    //客户端
    @Test
    public void client() throws IOException {
        //获取通道
        SocketChannel socketChannel= SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));

        FileChannel inFileChannel=FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);

        //2.分配指定大小的缓冲区
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

        //3. 读取本地文件，并发送到服务端
        while(inFileChannel.read(byteBuffer)!=-1){
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }


        //4.关闭通道
        inFileChannel.close();
        socketChannel.close();
    }

    //服务端
    @Test
    public void server() throws IOException {
        //获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        FileChannel fileChannel=FileChannel.open(Paths.get("2.png"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        //绑定连接
        serverSocketChannel.bind(new InetSocketAddress(9898));

        //获取客户端的连接的通道
        SocketChannel socketChannel = serverSocketChannel.accept();

        //分配只当大小的缓冲区
        ByteBuffer byteBuffer= ByteBuffer.allocate(1024);

        //5. 接收客户端的数据，并保存到本地
        while(socketChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //6. 关闭通道
        socketChannel.close();
        fileChannel.close();
        serverSocketChannel.close();
    }
}
