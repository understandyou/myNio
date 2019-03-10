package com;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class Nio {

    @Test
    public void test2() throws IOException {
        //FileChannel open = FileChannel.open("D:\\yy019195510.jpg", StandardOpenOption.APPEND);

        FileInputStream inputStream = new FileInputStream("D:\\yy019195510.jpg");
        FileOutputStream outputStream = new FileOutputStream("D:\\3.jpg");
        //输入通道
        FileChannel inputChannel = inputStream.getChannel();
        //输出通道
        FileChannel outChannel = outputStream.getChannel();
        //简化拷贝
        inputChannel.transferTo(0, inputChannel.size(), outChannel);

        resourceClose(inputStream, outputStream, inputChannel, outChannel);
    }

    @Test
    public void test1() throws IOException {//非直接缓冲区
        FileInputStream inputStream = new FileInputStream("D:\\yy019195510.jpg");
        FileOutputStream outputStream = new FileOutputStream("D:\\2.jpg");
        //输入通道
        FileChannel inputChannel = inputStream.getChannel();
        //输出通道
        FileChannel outChannel = outputStream.getChannel();

        //缓冲区,非直接缓冲区
        ByteBuffer bBuf = ByteBuffer.allocate(1024);


        //读入缓冲区
        while ((inputChannel.read(bBuf))!=-1){
            bBuf.flip();//转行为读模式
            outChannel.write(bBuf);//写到输出通道
            bBuf.clear();//清空缓冲区，实际上缓冲区是存在数据的，只是各个指针回归初始状态

        }
        resourceClose(inputStream, outputStream, inputChannel, outChannel);


    }

    private void resourceClose(FileInputStream inputStream, FileOutputStream outputStream, FileChannel inputChannel, FileChannel outChannel) throws IOException {
        outChannel.close();
        inputChannel.close();
        inputStream.close();
        outputStream.close();
    }
}
