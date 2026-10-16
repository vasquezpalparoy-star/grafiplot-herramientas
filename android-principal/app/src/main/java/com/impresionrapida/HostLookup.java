package com.impresionrapida;
import android.content.Context;
import android.net.wifi.WifiManager;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Resolve configured host via DNS, then an exact-name local mDNS query. */
public final class HostLookup {
 static final Map<String,Entry> cache=new ConcurrentHashMap<>();
 static final class Entry{final String ip;final long time=System.currentTimeMillis();Entry(String v){ip=v;}}
 public static String resolve(Context context,String host)throws IOException{
  Entry old=cache.get(host);if(old!=null&&System.currentTimeMillis()-old.time<60000)return old.ip;
  try{String ip=InetAddress.getByName(host).getHostAddress();cache.put(host,new Entry(ip));return ip;}catch(Exception ignored){}
  String local=host.toLowerCase(Locale.ROOT);if(!local.endsWith(".local")){if(local.contains("."))throw new IOException("DNS no pudo resolver "+host);local+=".local";}
  WifiManager.MulticastLock lock=null;
  try{
   WifiManager wifi=(WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);if(wifi!=null){lock=wifi.createMulticastLock("impresion-rapida-resolver");lock.setReferenceCounted(false);try{lock.acquire();}catch(SecurityException e){lock=null;}}
   try(DatagramSocket socket=new DatagramSocket()){
    socket.setSoTimeout(1000);byte[] query=query(local);InetAddress group=InetAddress.getByName("224.0.0.251");
    for(int attempt=0;attempt<3;attempt++){
     socket.send(new DatagramPacket(query,query.length,group,5353));long end=System.currentTimeMillis()+1200;
     while(System.currentTimeMillis()<end){byte[] data=new byte[9000];DatagramPacket p=new DatagramPacket(data,data.length);try{socket.receive(p);}catch(SocketTimeoutException e){break;}String answer=parse(Arrays.copyOf(data,p.getLength()),local);if(answer!=null){cache.put(host,new Entry(answer));return answer;}}
    }
   }
  }finally{if(lock!=null&&lock.isHeld())lock.release();}
  throw new IOException("No se pudo resolver "+host+". Verifica el nombre y el Wi-Fi de la HP.");
 }
 static byte[] query(String host)throws IOException{ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(b);d.writeShort(0x4952);d.writeShort(0);d.writeShort(1);d.writeShort(0);d.writeShort(0);d.writeShort(0);for(String part:host.split("\\.")){byte[] v=part.getBytes(StandardCharsets.US_ASCII);d.writeByte(v.length);d.write(v);}d.writeByte(0);d.writeShort(1);d.writeShort(0x8001);return b.toByteArray();}
 static int u16(byte[] b,int p)throws IOException{if(p<0||p+2>b.length)throw new EOFException();return ((b[p]&255)<<8)|(b[p+1]&255);}
 static String name(byte[] b,int[] position)throws IOException{StringBuilder out=new StringBuilder();int p=position[0],resume=-1;for(int step=0;step<128;step++){if(p>=b.length)throw new EOFException();int n=b[p++]&255;if(n==0){position[0]=resume<0?p:resume;return out.toString();}if((n&192)==192){if(p>=b.length)throw new EOFException();int pointer=((n&63)<<8)|(b[p++]&255);if(resume<0)resume=p;p=pointer;continue;}if(n>63||p+n>b.length)throw new IOException("Nombre DNS inválido");if(out.length()>0)out.append('.');out.append(new String(b,p,n,StandardCharsets.US_ASCII));p+=n;}throw new IOException("Ciclo de compresión DNS");}
 public static String parse(byte[] b,String wanted){try{if(b.length<12||(u16(b,2)&0x8000)==0)return null;int q=u16(b,4),records=u16(b,6)+u16(b,8)+u16(b,10);int[] p={12};for(int i=0;i<q;i++){name(b,p);p[0]+=4;}for(int i=0;i<records;i++){String owner=name(b,p);int type=u16(b,p[0]),clazz=u16(b,p[0]+2)&0x7fff,length=u16(b,p[0]+8);p[0]+=10;if(p[0]+length>b.length)return null;if(owner.equalsIgnoreCase(wanted)&&type==1&&clazz==1&&length==4)return InetAddress.getByAddress(Arrays.copyOfRange(b,p[0],p[0]+4)).getHostAddress();p[0]+=length;}}catch(Exception ignored){}return null;}
}
