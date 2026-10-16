package com.impresionrapida;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
public final class Raster {
 public static int resolution(Ipp.Reply caps)throws IOException{
  int best=Integer.MAX_VALUE;
  for(byte[] v:caps.values.getOrDefault("pwg-raster-document-resolution-supported",Collections.emptyList())){
   if(v.length!=9||v[8]!=3)continue;ByteBuffer b=ByteBuffer.wrap(v);int x=b.getInt(),y=b.getInt();if(x==y&&x>=150&&x<=300)best=Math.min(best,x);
  }
  if(best==Integer.MAX_VALUE)throw new IOException("La HP no anuncia una resolución PWG de 150 a 300 dpi. Comparte el diagnóstico para adaptar esta versión.");return best;
 }
 public static File create(File pdf,File out,Ipp.Reply caps,boolean rgb,String sides,boolean landscape,AtomicBoolean stop)throws IOException{
  int dpi=resolution(caps);int w=(int)((landscape?297:210)*dpi/25.4),h=(int)((landscape?210:297)*dpi/25.4);int tx=1,ty=1;
  boolean duplex=!sides.equals("one-sided"),tumble=sides.equals("two-sided-short-edge");
  String back=caps.strings("pwg-raster-document-sheet-back").isEmpty()?"normal":caps.strings("pwg-raster-document-sheet-back").get(0);
  if(duplex){if(back.equals("flipped")){if(tumble)tx=-1;else ty=-1;}else if((back.equals("rotated")&&!tumble)||(back.equals("manual-tumble")&&tumble)){tx=-1;ty=-1;}else if(!Arrays.asList("normal","rotated","manual-tumble").contains(back))throw new IOException("Transformación de reverso no compatible: "+back);}
  try(ParcelFileDescriptor fd=ParcelFileDescriptor.open(pdf,ParcelFileDescriptor.MODE_READ_ONLY);PdfRenderer r=new PdfRenderer(fd);OutputStream os=new BufferedOutputStream(new FileOutputStream(out))){
   os.write(new byte[]{0x52,0x61,0x53,0x32});int count=r.getPageCount();
   if((long)count*w*h*(rgb?3:1)>250L*1024*1024)throw new IOException("Trabajo muy grande. Divide el PDF en archivos más pequeños.");
   for(int i=0;i<count;i++){
    if(stop.get())throw new InterruptedIOException("Preparación cancelada");
    os.write(RasterCodec.header(w,h,dpi,rgb,duplex,tumble,count,tx,ty,landscape));
    Bitmap bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);bitmap.eraseColor(Color.WHITE);
    try(PdfRenderer.Page page=r.openPage(i)){
     Matrix m=new Matrix();
     float s=Math.min((float)w/page.getWidth(),(float)h/page.getHeight());m.setValues(new float[]{s,0,(w-page.getWidth()*s)/2,0,s,(h-page.getHeight()*s)/2,0,0,1});
     page.render(bitmap,null,m,PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
     int[] pixels=new int[w];byte[] row=new byte[w*(rgb?3:1)];
     for(int y=0;y<h;y++){
      if(stop.get())throw new InterruptedIOException("Preparación cancelada");
      boolean reverse=duplex&&i%2==1;int sourceY=reverse&&ty==-1?h-1-y:y;bitmap.getPixels(pixels,0,w,0,sourceY,w,1);
      for(int x=0;x<w;x++){int c=pixels[reverse&&tx==-1?w-1-x:x];int red=(c>>>16)&255,green=(c>>>8)&255,blue=c&255;
       if(rgb){row[x*3]=(byte)red;row[x*3+1]=(byte)green;row[x*3+2]=(byte)blue;}else row[x]=(byte)((red*54+green*183+blue*19)>>8);
      }RasterCodec.row(os,row,rgb?3:1);
     }
    }finally{bitmap.recycle();}
   }
  }return out;
 }
}
