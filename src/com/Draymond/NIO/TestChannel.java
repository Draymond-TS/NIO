package com.Draymond.NIO;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

/*
* 一、通道(Channel) : 用于源节点与目标的链接。在Java NIO 中负责缓冲区数据的传输。Channel 本身不存储数据，因此需要配合缓冲区进行传输。
*
* 二、通道的主要实现类
*   java.nio.channels.Channel 接口
*     |--FileChannel
 *    |--SocketChannel
 *    |--ServerSocketChannel
 *    |--DatagramChannel
 *
 *三、获取通道
 * 1、java针对支持通道的类提供了getChannel() 方法
 *  本地 IO :
 *  FileInputStream/FileOutputStream
 *  RandomAccessFile
 *
 *  网络IO
 *  Socket
 *  ServerSocket
 *  DatagramSocket
 *
 *  2.在JDK 1.7 中的 NIO.2 针对各个通道提供了静态方法 open()
 *  3.在JDL 1.7 中的NIO.2 的File 工具类的 newByteChannel()
 *
 *  四、通道之间的数据传输
 *  transferForm()
 *  transferTo()
 *
 *  五、分散(Scatter)与聚合(Gather)
 *  分散读取 (Scatter Reads) : 将报道中的数据分散到多个缓冲区中
 *  聚集写入 (Gathering Writes) : 将多个缓冲区数据聚集到通道中
 *
 *
 * 六、字符集：Charset
 * 编码：字符串 -> 字节数组
 * 解码：字节数组  -> 字符串
 * */
public class TestChannel {


    @Test
    public void test6() throws CharacterCodingException {
        Charset cs1=Charset.forName("GBK");

        //获得编码器
        CharsetEncoder ce = cs1.newEncoder();

        //获取解码器
        CharsetDecoder cd=cs1.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("泰勒斯威夫特！");
        cBuf.flip();

        //编码
        ByteBuffer bBuf = ce.encode(cBuf);

        for (int i = 0; i < cBuf.limit()+1; i++) {
            System.out.println(bBuf.get());
        }


        //解码
        bBuf.flip();
        CharBuffer cBuf2 = cd.decode(bBuf);
        System.out.println(cBuf2.toString());

        System.out.println("------------------------------------------------------");

        Charset cs2 = Charset.forName("GBK");
        bBuf.flip();
        CharBuffer cBuf3 = cs2.decode(bBuf);
        System.out.println(cBuf3.toString());


    }

    @Test
    public void test5(){
        Map<String, Charset> map = Charset.availableCharsets();

        Set<Map.Entry<String, Charset>> set = map.entrySet();

        for (Map.Entry<String, Charset> entry : set) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }


    //分散和聚集
    @Test
    public void test4() throws IOException{
        RandomAccessFile randomAccessFile=new RandomAccessFile("1.txt","rw");

        //获取通道
        FileChannel fileChannel = randomAccessFile.getChannel();

        ByteBuffer byteBuffer1=ByteBuffer.allocate(100);
        ByteBuffer byteBuffer2=ByteBuffer.allocate(1024);

        //3. 分散读取
        ByteBuffer[] bufs = {byteBuffer1, byteBuffer2};
        fileChannel.read(bufs);

        for(ByteBuffer byteBuffer:bufs){
            byteBuffer.flip();
        }

        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println("-----------------");
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));


        //4. 聚集写入
        RandomAccessFile randomAccessFile1=new RandomAccessFile("2.txt","rw");
        FileChannel fileChannel1 = randomAccessFile1.getChannel();

        fileChannel1.write(bufs);

    }

    @Test
    public void test3() throws IOException {

        FileChannel inChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("3.png"),StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);

        outChannel.transferFrom(inChannel,0,inChannel.size());
        //inChannel.transferTo(0,inChannel.size(),outChannel);

         inChannel.close();
         outChannel.close();
    }

    //通道之间的数据传输(直接缓冲区)
    @Test
    public void test2() throws IOException {
        long start=System.currentTimeMillis();

        //使用直接缓冲区完成文件的复制(内存映射文件)

        FileChannel inChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("3.png"),StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);

        //内容映射
        MappedByteBuffer inmap = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outmapp=outChannel.map(FileChannel.MapMode.READ_WRITE,0,inChannel.size());

        byte[] dst=new byte[1048576];
        inmap.get(dst);
        outmapp.put(dst);

        inChannel.close();
        outChannel.close();

        long end = System.currentTimeMillis();
        System.out.println("耗费时间为：" + (end - start));

    }


    //1.利用通道完成文件的复制
    @Test
    public  void test1(){
        long start=System.currentTimeMillis();

        FileInputStream fis=null;
        FileOutputStream fos=null;
        //获取通道
        FileChannel inChannel=null;
        FileChannel outChannel=null;
        try {
            fis = new FileInputStream("E:\\视频\\[Kamigami] Byousoku 5 Centimeter [BD x264 1920x1080 AC3(5. 1ch,jp,en,rus,ita,ger) Sub(BIG5,GB,en,jp,ara,ger,kor,por,tha,spa,ita,ind)].mkv");
            fos = new FileOutputStream("E:\\视频\\FiveCm.mkv");

            inChannel = fis.getChannel();
            outChannel = fos.getChannel();


            //分配指定大小的缓冲区
            ByteBuffer byteBuffer=ByteBuffer.allocateDirect(1024);  //115393
            //ByteBuffer byteBuffer=ByteBuffer.allocate(1024);      //209494

            while(inChannel.read(byteBuffer)!=-1){
                byteBuffer.flip();
                //将缓冲区中的数据写入通道中
                outChannel.write(byteBuffer);
                //清空缓冲区
                byteBuffer.clear();

            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            isNotNull(outChannel);
            isNotNull(inChannel);
            isNotNull(fos);
            isNotNull(fis);
        }

        long end = System.currentTimeMillis();
        System.out.println("耗费时间为：" + (end - start));

    }

    public void isNotNull(Closeable closeable){
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
