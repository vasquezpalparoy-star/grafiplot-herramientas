package com.impresionrapida;
import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
/** PWG Raster v2, network-endian header and pixel PackBits compression. */
public final class RasterCodec {
 public static byte[] header(int width,int height,int dpi,boolean rgb,boolean duplex,boolean tumble,int pages,int tx,int ty){
  return header(width,height,dpi,rgb,duplex,tumble,pages,tx,ty,false);
 }
 public static byte[] header(int width,int height,int dpi,boolean rgb,boolean duplex,boolean tumble,int pages,int tx,int ty,boolean landscape){
  byte[] data=new byte[1796];ByteBuffer b=ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
  b.putInt(272,duplex?1:0);b.putInt(276,dpi);b.putInt(280,dpi);b.putInt(292,landscape?842:595);b.putInt(296,landscape?595:842);
  b.putInt(340,1);b.putInt(352,landscape?842:595);b.putInt(356,landscape?595:842);b.putInt(368,tumble?1:0);
  b.putInt(372,width);b.putInt(376,height);b.putInt(384,8);b.putInt(388,rgb?24:8);b.putInt(392,width*(rgb?3:1));
  b.putInt(400,rgb?19:18);b.putInt(420,rgb?3:1);b.putInt(452,pages);b.putInt(456,tx);b.putInt(460,ty);
  b.putInt(472,width);b.putInt(476,height);b.putInt(480,0xffffff);
  byte[] name="iso_a4_210x297mm".getBytes(StandardCharsets.US_ASCII);System.arraycopy(name,0,data,1732,name.length);return data;
 }
 public static void row(OutputStream out,byte[] row,int components)throws IOException{
  out.write(0);int pixels=row.length/components;
  for(int i=0;i<pixels;){int count=Math.min(128,pixels-i);out.write(count==1?0:257-count);out.write(row,i*components,count*components);i+=count;}
 }
}
