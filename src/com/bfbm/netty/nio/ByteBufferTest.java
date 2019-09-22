package com.bfbm.netty.nio;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

public class ByteBufferTest {
    public static void main(String args[]) throws FileNotFoundException {

        allocate();

        //wrap();

        //get();

        //put();

        //flip();

        //reset();

        //rewind();

        //compact();

        //slice();
    }

    private static void compact() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        System.out.println("\r\n");
        System.out.println("--------------------------Test compact--------------------------");
        //17. compact() 把从position到limit中的内容移到0到limit-position的区域内，position和limit的取值也分别变成limit-position、capacity。如果先将positon设置到limit，再compact，那么相当于clear()
        buffer = ByteBuffer.allocate(32);
        buffer.clear();
        buffer.put("abcd".getBytes());
        System.out.println("before compact:" + buffer + new String(buffer.array()));
        buffer.flip();
        System.out.println("after flip():" + buffer + new String(buffer.array()));
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        System.out.println("after three gets:" + buffer + new String(buffer.array()));

        buffer.compact();

        System.out.println("after compact():" + buffer + new String(buffer.array()));
    }

    private static void rewind() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        System.out.println("\r\n");
        System.out.println("--------------------------Test rewind--------------------------");
        //16. rewind() 把position设为0，mark设为-1，不改变limit的值

        buffer.clear();
        buffer.position(10);
        buffer.limit(15);
        System.out.println("before rewind: " + buffer);

        buffer.rewind();

        System.out.println("before rewind: " + buffer);
    }

    private static void reset() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        System.out.println("\r\n");
        System.out.println("--------------------------Test reset--------------------------");
        //13. position()|position(5) 类似jQuery中的val(),val(10)，一个负责get，一个负责set
        //14. clear() 有点初始化的味道，但是并不影响底层byte数组的内容 position = 0;limit = capacity; mark = -1;
        //15. reset() 把position设置成mark的值，相当于之前做过一个标记，现在要退回到之前标记的地方
        buffer.clear();
        buffer.position(5);
        buffer.mark();
        buffer.position(10);
        System.out.println("before reset: " + buffer);
        buffer.reset();
        System.out.println("after  reset: " + buffer);
        buffer.clear();
        System.out.println("after  clear: " + buffer);
    }

    private static void put() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put((byte) 'a');
        buffer.put((byte) 'b');
        buffer.put((byte) 'c');
        buffer.put((byte) 'd');
        buffer.put((byte) 'e');
        buffer.put((byte) 'f');
        buffer.flip();

        byte[] dst = new byte[10];
        buffer.get(dst, 0, 2);


        System.out.println("\r\n");
        System.out.println("--------------------------Test put--------------------------");
        //10. put(int index, byte b) 绝对写，向byteBuffer底层的bytes中下标为index的位置插入byte b，不改变position
        //11. put(ByteBuffer src) 相对写，把src中可读的部分（也就是position到limit）写入此byteBuffer
        //12. put(byte[] src, int offset, int length) 从src数组中的offset到offset+length区域读取数据并使用相对写写入此byteBuffer

        ByteBuffer bb = ByteBuffer.allocate(32);
        System.out.println("before put(byte):" + bb + new String(bb.array()));
        bb.put((byte) 'b');
        bb.put((byte) 'f');
        bb.put((byte) 'b');
        bb.put((byte) 'm');
        System.out.println("after  put(byte):" + bb + new String(bb.array()));

        bb.put(4, (byte) 'i');
        System.out.println("after  put(4,'i') bb 结果：" + bb + new String(bb.array()));

        System.out.println();
        System.out.println("buffer 相关信息:" + buffer + new String(buffer.array()));

        bb.put(buffer);

        System.out.println("after  put(buffer)  bb 结果：" + bb + new String(bb.array()));
        System.out.println("buffer 相关信息:" + buffer + new String(buffer.array()));

        bb.put(dst, 0, 2);

        System.out.println("after  put(dst, 0, 2) bb 结果：" + bb + new String(bb.array()));
    }

    private static void get() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put((byte) 'a');
        buffer.put((byte) 'b');
        buffer.put((byte) 'c');
        buffer.put((byte) 'd');
        buffer.put((byte) 'e');
        buffer.put((byte) 'f');
        buffer.flip();

        System.out.println("\r\n");
        System.out.println("--------------------------Test get--------------------------");
        //7. get() 相对读，从position位置读取一个byte，并将position+1，为下次读写作准备
        //8. get(index) 绝对读，读取byteBuffer底层的bytes中下标为index的byte，不改变position
        System.out.println("before get():" + buffer + new String(buffer.array()));

        System.out.println("get结果:" + (char) buffer.get());
        System.out.println("after get():" + buffer);

        System.out.println("get(2)结果:" + (char) buffer.get(2));
        System.out.println("after get(index):" + buffer);

        System.out.println("get结果:" + (char) buffer.get());
        System.out.println("after get(index):" + buffer);


        System.out.println("\r\n");
        System.out.println("--------------------------9 Test get(index)--------------------------");
        //9. get(dst, 0, 2) 从position位置开始相对读，读length个byte，并写入dst下标从offset到offset+length的区域
        System.out.println("ByteBuffer:" + new String(buffer.array()));
        System.out.println("before get(dst, 0, 2):" + buffer);

        byte[] dst = new byte[10];
        buffer.get(dst, 0, 2);

        System.out.println("after get(dst, 0, 2):" + buffer);
        System.out.println("get(dst, 0, 2) dst结果：" + new String(dst));
    }

    private static void flip() {
        System.out.println("\r\n");
        System.out.println("--------------------------Test flip--------------------------");
        //5. put() 相对写，向position的位置写入一个byte，并将position+1，为下次读写作准备
        //6. flip() 转换为读取模式limit = position;position = 0;mark = -1;  翻转，也就是让flip之后的position到limit这块区域变成之前的0到position这块，翻转就是将一个处于存数据状态的缓冲区变为一个处于准备取数据的状态

        ByteBuffer buffer = ByteBuffer.allocate(32);

        System.out.println("ByteBuffer:" + new String(buffer.array()));
        buffer.put((byte) 'a');
        buffer.put((byte) 'b');
        buffer.put((byte) 'c');
        buffer.put((byte) 'd');
        buffer.put((byte) 'e');
        buffer.put((byte) 'f');
        System.out.println("ByteBuffer:" + new String(buffer.array()));
        System.out.println("before flip()" + buffer);
        buffer.flip();
        System.out.println("after  flip():" + buffer);
    }

    private static void wrap() {
        ByteBuffer buffer;

        System.out.println("\r\n");
        System.out.println("--------------------------Test wrap------------------------");
        //3. ByteBuffer.wrap 这个缓冲区的数据会存放在byte数组中，bytes数组或buff缓冲区任何一方中数据的改动都会影响另一方。其实ByteBuffer底层本来就有一个bytes数组负责来保存buffer缓冲区中的数据，通过allocate方法系统会帮你构造一个byte数组
        byte[] bytes = new byte[32];
        buffer = ByteBuffer.wrap(bytes);
        System.out.println(buffer);

        //4. ByteBuffer.wrap(array, offset, length) 在上一个方法的基础上可以指定偏移量和长度，这个offset也就是包装后byteBuffer的position，而length呢就是limit-position的大小，从而我们可以得到limit的位置为length+position(offset)
        buffer = ByteBuffer.wrap(bytes, 10, 10);
        System.out.println(buffer);
    }

    private static void allocate() {
        System.out.println("\r\n");
        System.out.println("--------------------------Test allocate------------------------");
        //1. ByteBuffer.allocate 从堆空间中分配一个容量大小为capacity的byte数组作为缓冲区的byte数据存储器
        //HeapByteBuffer则是分配在堆上的,会被JVM管理回收
        ByteBuffer buffer = ByteBuffer.allocate(102400);
        System.out.println("buffer = " + buffer);

        //2. ByteBuffer.allocateDirect是通过操作系统来创建内存块用作缓冲区，它与当前操作系统能够更好的耦合，因此能进一步提高I/O操作速度。
        // 但是分配直接缓冲区的系统开销很大，因此只有在缓冲区较大并长期存在，或者需要经常重用时，才使用这种缓冲区
        // DirectByteBuffer不是分配在堆上的，它不被GC直接管理
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(102400);
        System.out.println("directBuffer = " + directBuffer);

        //因为平时的read/write，都会在I/O设备与应用程序空间之间经历一个“内核缓冲区”。
        // DirectByteBuffer就好比是“内核缓冲区”上的缓存，不直接受GC管理；
        // 而Heap Buffer就仅仅是byte[]字节数组的包装形式。
        // 因此把一个Direct Buffer写入一个Channel的速度要比把一个HeapByteBuffer写入一个Channel的速度要快
    }

    private static void slice() {
        System.out.println("\r\n");
        System.out.println("--------------------------Test slice------------------------");
        ByteBuffer bb = ByteBuffer.allocate(32);
        bb.put((byte) 'b');
        bb.put((byte) 'f');
        bb.put((byte) 'b');
        bb.put((byte) 'm');

        ByteBuffer bs = bb.slice();

        System.out.println("源BB" + bb + new String(bb.array()));
        System.out.println("slice BS" + bs + new String(bs.array()));

        bs.put((byte) 'i');
        bs.put((byte) 'l');
        bs.put((byte) 'o');
        bs.put((byte) 'v');
        bs.put((byte) 'e');
        bs.put((byte) 'u');

        System.out.println("源BB" + bb + new String(bb.array()));
        System.out.println("slice BS" + bs + new String(bs.array()));
    }

}
