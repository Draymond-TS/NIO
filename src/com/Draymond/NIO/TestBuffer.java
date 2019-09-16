package com.Draymond.NIO;

import org.junit.Test;

import java.nio.ByteBuffer;

/* 一、缓冲区(Buffer) ： 在 Java NIO中负责数据的存取。缓冲区的就是数组。用于存储不同数据类型的数据
 *
 * 根据数据类型不同(boolean除外)，提供了相应类型的缓冲区
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 *
 * 上述缓冲区的管理方法j几乎一致，通过allocate()获取缓冲区
 *
 * 二、缓冲区存取数据的两个核心方法
 * put() ： 存入数据到缓冲区中
 * get() ： 获取缓冲区的数据
 *
 * 三、缓冲区的四个核心方法
 * capacity : 容量，表示缓冲区中最大存储数据的容量，一旦声明不能改变
 * limit ： 界限，表示缓冲区可以操作数据的大小。(limit 后数据不能进行读写)
 * position : 位置，表示缓冲区中正在操作数据的位置
 *
 * mark ： 标志，表示记录当前postion的位置，可以通过reset()恢复到mark的位置
 *
 * 0<= mark <=position <=limit <= cappacity
 *
 * 四、直接缓冲区与非直接缓冲区：
 * 非直接缓冲区: 通过allocate() 方法分配缓冲区，将缓冲区建立在JVM的内存中
 * 直接缓冲区: 通过allocateDirect() 方法分配直接缓冲区，将缓冲区建立在物理内存中，可以提高效率
 *
 */
public class TestBuffer {

    @Test
    public void test3(){
        //分配直接缓冲区
        ByteBuffer buf=ByteBuffer.allocateDirect(1024);

        System.out.println(buf.isDirect());
    }

    @Test
    public void test2(){
        String str="efgh";

        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        byteBuffer.put(str.getBytes());

        byteBuffer.flip();

        byte[] dst=new byte[byteBuffer.limit()];//4
        byteBuffer.get(dst,0,2);
        System.out.println(new String(dst,0,2));

        //mark() 标记
        byteBuffer.mark();

        byteBuffer.get(dst, 2, 2);
        System.out.println(new String(dst, 2, 2));
        System.out.println(byteBuffer.position());

        //reset() : 恢复到 mark 的位置
        byteBuffer.reset();
        System.out.println(byteBuffer.position());


        //判断缓冲区中是否还有剩余数据
        if(byteBuffer.hasRemaining()){

            //获取缓冲区中可以操作的数量
            System.out.println(byteBuffer.remaining());
        }

    }


    @Test
    public void test1(){
        String str="abcd";

        //1.分配一个指定大小的缓冲区
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);

        System.out.println("--------------allocate-----------");
        print(byteBuffer);//0 1024 1024


        //2.利用put()存入到数据缓冲区指中
        byteBuffer.put(str.getBytes());

        System.out.println("--------------put-----------");
        print(byteBuffer);//4 1024 1024

        //3.切换读取数据模式
        byteBuffer.flip();
        System.out.println("--------------flip-----------");
        print(byteBuffer);//0 4 1024


        //4.利用get()读取缓冲区的数据
        System.out.println("--------------get-----------");
        byte[]dst=new byte[byteBuffer.limit()];
        byteBuffer.get(dst);
        System.out.println(new String(dst,0,dst.length));
        print(byteBuffer);//4 4 1024



        System.out.println("--------------rewind-----------");
        //可重复读
        byteBuffer.rewind();
        print(byteBuffer);


        System.out.println("--------------clear-----------");
        //清理缓冲区,但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
        byteBuffer.clear();
        print(byteBuffer);// 0 1024 1024

        System.out.println((char)byteBuffer.get()); //a

    }

    public static void print(ByteBuffer byteBuffer){
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
    }



}
