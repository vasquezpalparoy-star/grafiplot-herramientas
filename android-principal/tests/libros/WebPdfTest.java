package com.grifplot.libros;
import java.io.*;
public class WebPdfTest {
 static void check(boolean v,String n){if(!v)throw new AssertionError(n);System.out.println("OK "+n);}
 public static void main(String[] args)throws Exception{String base=args[0];File out=new File(args[1]);WebPdf.Progress p=new WebPdf.Progress(){public boolean cancelled(){return false;}public void update(long n,long total){}};WebPdf.download(base+"/pdf","Test",null,null,out,p);check(out.length()>5,"descarga PDF válida");WebPdf.download(base+"/redirect","Test",null,null,out,p);check(out.length()>5,"redirección a PDF");for(String endpoint:new String[]{"/html","/forbidden","/broken","/insecure"}){boolean failed=false;try{WebPdf.download(base+endpoint,"Test",null,null,out,p);}catch(IOException e){failed=true;}check(failed,"rechazo de "+endpoint);}boolean cancel=false;try{WebPdf.download(base+"/pdf","Test",null,null,out,new WebPdf.Progress(){public boolean cancelled(){return true;}public void update(long n,long total){}});}catch(IOException e){cancel=true;}check(cancel,"cancelar antes de descargar");out.delete();}
}
