package com.Draymond.NIO;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TestBlockingNIO2 {
    //客户端
    @Test
    public void client() throws IOException {
        SocketChannel socketChannel=SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));

        FileChannel inChannel=FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);

        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

        while(inChannel.read(byteBuffer)!= -1){
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();

        }
        //杀掉线程，避免受到阻塞不能发送消息
        socketChannel.shutdownOutput();

        inChannel.close();
        socketChannel.close();

    }

    //服务端
    @Test
    public void server() throws IOException{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        
        FileChannel outChannel= FileChannel.open(Paths.get("2.png"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        serverSocketChannel.bind(new InetSocketAddress(9898));

        ByteBuffer buf = ByteBuffer.allocate(1024);

        SocketChannel socketChannel1 = serverSocketChannel.accept();

        while(socketChannel1.read(buf)!=-1){
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        //发送反馈给客户端
        buf.put("服务端接收数据成功".getBytes());
        buf.flip();
        socketChannel1.write(buf);

        socketChannel1.close();
        outChannel.close();
        serverSocketChannel.close();
    }

}
