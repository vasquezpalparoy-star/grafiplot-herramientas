package com.impresionrapida;
import java.io.*;
import java.util.*;
public class ProtocolTest {
 public static void main(String[] args)throws Exception{
  for(int n=1;n<=100;n++){
   Set<Integer> seen=new HashSet<>();int sheets=(n+3)/4;
   for(int i=0;i<sheets;i++)for(boolean back:new boolean[]{false,true})for(int p:Documents.booklet(n,i,back))if(p>=0&&!seen.add(p))throw new AssertionError("Duplicate page");
   if(seen.size()!=n)throw new AssertionError("Missing page");
  }
  if(!Arrays.equals(Documents.booklet(8,0,false),new int[]{7,0}))throw new AssertionError();
  if(!Arrays.equals(Documents.booklet(8,0,true),new int[]{1,6}))throw new AssertionError();
  for(int pages=1;pages<=100;pages++)for(int mode=0;mode<3;mode++)for(int copies=1;copies<=5;copies++){
   int per=mode==0?1:mode==1?2:4;int expected=(int)Math.ceil(pages/(double)per)*copies;
   if(PrintRules.sheets(pages,mode,copies)!=expected)throw new AssertionError("Sheet count");
   boolean allowed=true;try{PrintRules.enforceLimit(pages,mode,copies);}catch(IllegalArgumentException e){allowed=false;}
   if(allowed!=(expected<=5))throw new AssertionError("5-sheet limit");
  }
  if(!PrintRules.sides(2,true).equals("two-sided-short-edge")||!PrintRules.sides(1,true).equals("two-sided-short-edge")||!PrintRules.sides(1,false).equals("two-sided-long-edge"))throw new AssertionError("Orientation");
  for(int state=3;state<=9;state++)if(PrintRules.terminal(state)!=(state>=7))throw new AssertionError("Terminal state");
  for(String host:new String[]{"HPE802D0","HPE802D0.local","192.168.1.121"})PrintRules.host(host);
  for(String invalid:new String[]{"http://HPE802D0","a/b","a..b","999.1.1.1","-hp","hp-"})try{PrintRules.host(invalid);throw new AssertionError("Invalid host accepted");}catch(IllegalArgumentException expected){}
  byte[] landscape=RasterCodec.header(1753,1240,150,false,true,true,2,1,1,true);
  java.nio.ByteBuffer header=java.nio.ByteBuffer.wrap(landscape);
  if(header.getInt(352)!=842||header.getInt(356)!=595||header.getInt(368)!=1)throw new AssertionError("Landscape short-edge header");
  byte[] request=Ipp.jobRequest("ipp://HPE802D0:631/ipp/print",8,41);if(request[3]!=8)throw new AssertionError("Cancel opcode");
  ByteArrayOutputStream dns=new ByteArrayOutputStream();DataOutputStream nd=new DataOutputStream(dns);
  nd.writeShort(0x4952);nd.writeShort(0x8400);nd.writeShort(1);nd.writeShort(1);nd.writeShort(0);nd.writeShort(0);
  for(String part:new String[]{"HPE802D0","local"}){nd.writeByte(part.length());nd.writeBytes(part);}nd.writeByte(0);nd.writeShort(1);nd.writeShort(1);
  nd.writeShort(0xc00c);nd.writeShort(1);nd.writeShort(0x8001);nd.writeInt(120);nd.writeShort(4);nd.write(new byte[]{(byte)192,(byte)168,1,121});
  if(!"192.168.1.121".equals(HostLookup.parse(dns.toByteArray(),"hpe802d0.local")))throw new AssertionError("Compressed mDNS");
  if(HostLookup.parse(dns.toByteArray(),"other.local")!=null)throw new AssertionError("mDNS owner mismatch");
  if(HostLookup.parse(new byte[]{1,2,3},"hpe802d0.local")!=null)throw new AssertionError("Truncated mDNS");
  ByteArrayOutputStream wire=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(wire);d.writeShort(0x101);d.writeShort(0);d.writeInt(1);d.writeByte(4);
  Ipp.text(d,0x49,"document-format-supported","application/pdf");Ipp.text(d,0x49,"","image/pwg-raster");Ipp.attr(d,0x21,"job-id",Ipp.number(123));d.writeByte(3);
  Ipp.Reply r=Ipp.parse(new ByteArrayInputStream(wire.toByteArray()));if(!r.has("document-format-supported","image/pwg-raster")||r.integer("job-id",0)!=123)throw new AssertionError("IPP parser");
  try{Ipp.parse(new ByteArrayInputStream(new byte[]{1,1}));throw new AssertionError("Truncation accepted");}catch(EOFException expected){}
  try(OutputStream out=new FileOutputStream(args[0])){
   out.write(new byte[]{0x52,0x61,0x53,0x32});
   out.write(RasterCodec.header(2,2,150,false,false,false,1,1,1));
   RasterCodec.row(out,new byte[]{0,127},1);RasterCodec.row(out,new byte[]{(byte)255,64},1);
  }
  System.out.println("PASS: booklet, 1500 sheet-limit cases, orientation, terminal states, host validation, exact-name compressed mDNS, IPP codec, PWG fixture.");
 }
}
