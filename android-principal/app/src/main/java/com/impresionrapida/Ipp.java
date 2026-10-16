package com.impresionrapida;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Minimal IPP/1.1 codec. No system print service or external server. */
public final class Ipp {
 public interface Resolver{String resolve(String host)throws IOException;}
 public static volatile Resolver resolver;
 public static final class Reply {
  public int status;
  public final Map<String,List<byte[]>> values=new LinkedHashMap<>();
  public List<String> strings(String key) {
   List<String> out=new ArrayList<>();
   for(byte[] v:values.getOrDefault(key, Collections.emptyList()))out.add(new String(v,StandardCharsets.UTF_8));
   return out;
  }
  public boolean has(String key,String value){return strings(key).contains(value);}
  public int integer(String key,int fallback){List<byte[]> a=values.get(key); if(a==null||a.isEmpty()||a.get(0).length!=4)return fallback; byte[] b=a.get(0);return ((b[0]&255)<<24)|((b[1]&255)<<16)|((b[2]&255)<<8)|(b[3]&255);}
 }
 public final String host, path;
 public final int port;
 public volatile AtomicBoolean stop;
 public String jobName="Impresion Rapida";
 public Ipp(String host,String path){this(host,path,631);}
 public Ipp(String host,String path,int port){this.host=host;this.path=path;this.port=port;}
 public String uri(){return "ipp://"+host+":"+port+path;}
 static void attr(DataOutputStream d,int tag,String name,byte[] v)throws IOException{byte[] n=name.getBytes(StandardCharsets.UTF_8);d.writeByte(tag);d.writeShort(n.length);d.write(n);d.writeShort(v.length);d.write(v);}
 static void text(DataOutputStream d,int tag,String n,String v)throws IOException{attr(d,tag,n,v.getBytes(StandardCharsets.UTF_8));}
 static byte[] number(int n){return new byte[]{(byte)(n>>>24),(byte)(n>>>16),(byte)(n>>>8),(byte)n};}
 ByteArrayOutputStream begin(int operation)throws IOException{
  ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(b);
  d.writeShort(0x0101);d.writeShort(operation);d.writeInt(1);d.writeByte(1);
  text(d,0x47,"attributes-charset","utf-8");text(d,0x48,"attributes-natural-language","es");text(d,0x45,"printer-uri",uri());return b;
 }
 public static byte[] jobRequest(String uri,int op,int id)throws IOException{
  if(id<=0)throw new IOException("La impresora no devolvió un identificador de trabajo válido.");
  ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream d=new DataOutputStream(b);
  d.writeShort(0x101);d.writeShort(op);d.writeInt(1);d.writeByte(1);
  text(d,0x47,"attributes-charset","utf-8");text(d,0x48,"attributes-natural-language","es");text(d,0x45,"printer-uri",uri);
  attr(d,0x21,"job-id",number(id));text(d,0x42,"requesting-user-name","Cliente");d.writeByte(3);return b.toByteArray();
 }
 public Reply job(int id)throws IOException{return exchange(jobRequest(uri(),9,id),null);}
 public Reply cancel(int id)throws IOException{return exchange(jobRequest(uri(),8,id),null);}
 public Reply createJob(int copies,String sides,String color,String format)throws IOException{return submit(null,copies,sides,color,format,5);}
 public Reply sendDocument(File file,int id,String format)throws IOException{
  ByteArrayOutputStream b=begin(6);DataOutputStream d=new DataOutputStream(b);attr(d,0x21,"job-id",number(id));text(d,0x42,"requesting-user-name","Cliente");
  text(d,0x49,"document-format",format);attr(d,0x22,"last-document",new byte[]{1});d.writeByte(3);return exchange(b.toByteArray(),file);
 }
 public Reply capabilities()throws IOException{ByteArrayOutputStream b=begin(0x000b);b.write(3);return exchange(b.toByteArray(),null);}
 public Reply print(File pdf,int copies,String sides,String color,String format)throws IOException{
  return submit(pdf,copies,sides,color,format,2);
 }
 Reply submit(File pdf,int copies,String sides,String color,String format,int operation)throws IOException{
  ByteArrayOutputStream b=begin(operation);DataOutputStream d=new DataOutputStream(b);
  text(d,0x42,"requesting-user-name","Cliente");text(d,0x42,"job-name",jobName);
  attr(d,0x22,"ipp-attribute-fidelity",new byte[]{1});if(operation==2)text(d,0x49,"document-format",format);
  d.writeByte(2);attr(d,0x21,"copies",number(copies));text(d,0x44,"media","iso_a4_210x297mm");
  text(d,0x44,"sides",sides);text(d,0x44,"print-color-mode",color);d.writeByte(3);
  return exchange(b.toByteArray(),pdf);
 }
 Reply exchange(byte[] header,File file)throws IOException{
  HttpURLConnection c=(HttpURLConnection)new URL("http",resolver==null?host:resolver.resolve(host),port,path).openConnection();
  c.setConnectTimeout(7000);c.setReadTimeout(45000);c.setInstanceFollowRedirects(false);c.setRequestMethod("POST");c.setDoOutput(true);c.setRequestProperty("Content-Type","application/ipp");
  c.setFixedLengthStreamingMode((long)header.length+(file==null?0:file.length()));
  try{
   try(OutputStream o=c.getOutputStream()){o.write(header);if(file!=null)try(InputStream in=new FileInputStream(file)){byte[] buf=new byte[32768];int n;while((n=in.read(buf))!=-1){if(stop!=null&&stop.get())throw new InterruptedIOException("Envío interrumpido por el usuario");o.write(buf,0,n);}}}
   if(c.getResponseCode()!=200)throw new IOException("La impresora respondió HTTP "+c.getResponseCode());
   try(InputStream in=c.getInputStream()){Reply r=parse(in);if(r.status>2)throw new IOException("IPP 0x"+Integer.toHexString(r.status)+": "+r.strings("status-message"));return r;}
  }finally{c.disconnect();}
 }
 public static Reply parse(InputStream in)throws IOException{
  DataInputStream d=new DataInputStream(new BufferedInputStream(in));d.readUnsignedShort();Reply r=new Reply();r.status=d.readUnsignedShort();d.readInt();String key="";int total=0;
  for(;;){int tag=d.readUnsignedByte();if(tag==3)break;if(tag<=15){key="";continue;}
   int nl=d.readUnsignedShort();total+=nl; if(total>1048576)throw new IOException("Respuesta IPP demasiado grande");
   byte[] n=new byte[nl];d.readFully(n);if(nl>0)key=new String(n,StandardCharsets.UTF_8);
   int vl=d.readUnsignedShort();total+=vl;if(total>1048576)throw new IOException("Respuesta IPP demasiado grande");byte[] v=new byte[vl];d.readFully(v);
   r.values.computeIfAbsent(key,k->new ArrayList<>()).add(v);
  }return r;
 }
}
