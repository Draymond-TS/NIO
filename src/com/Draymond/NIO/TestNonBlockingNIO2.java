package com.Draymond.NIO;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class TestNonBlockingNIO2 {
    @Test
    public void send() throws IOException {
        DatagramChannel dc=DatagramChannel.open();

        dc.configureBlocking(false);

        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

        Scanner scanner=new Scanner(System.in);

        while(scanner.hasNext()){
            String str = scanner.next();
            byteBuffer.put((new Date().toString() + ":\n" + str).getBytes());
            byteBuffer.flip();
            dc.send(byteBuffer,new InetSocketAddress("127.0.0.1",9898));
            byteBuffer.clear();
        }
        dc.close();
    }


    @Test
    public void receive() throws IOException{
        DatagramChannel dc = DatagramChannel.open();

        dc.configureBlocking(false);

        dc.bind(new InetSocketAddress(9898));

        Selector selector=Selector.open();

        dc.register(selector, SelectionKey.OP_READ);


        while(selector.select()>0){
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()){
                SelectionKey sk = it.next();

                if(sk.isReadable()){
                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    dc.receive(buf);
                    buf.flip();
                    System.out.println(new String(buf.array(), 0, buf.limit()));
                    buf.clear();

                }
            }
            it.remove();
        }
    }
}
