package com.Draymond.NIO;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class TestPipe {
    @Test
    public void test1() throws IOException {
        //1.获取管道
        Pipe pipe=Pipe.open();

        //2.将缓冲区的数据写入管道
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

        Pipe.SinkChannel sinkChannel=pipe.sink();
        byteBuffer.put("通过单向管道发送数据".getBytes());
        byteBuffer.flip();
        sinkChannel.write(byteBuffer);

        //3.读取缓冲区中的数据
        Pipe.SourceChannel sourceChannel=pipe.source();
        byteBuffer.flip();
        int len = sourceChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array(), 0, len));

        sourceChannel.close();
        sinkChannel.close();


    }
}
