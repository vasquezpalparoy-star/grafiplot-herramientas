package com.impresionrapida;
import android.graphics.*;
import android.graphics.pdf.*;
import android.os.ParcelFileDescriptor;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
public final class Documents {
 public static int[] booklet(int pages,int sheet,boolean back){int total=((pages+3)/4)*4;int[] p=back?new int[]{2*sheet+1,total-2*sheet-2}:new int[]{total-2*sheet-1,2*sheet};for(int i=0;i<2;i++)if(p[i]>=pages)p[i]=-1;return p;}
 static void paint(PdfRenderer r,int index,Canvas canvas,RectF box)throws IOException{
  if(index<0)return;
  try(PdfRenderer.Page p=r.openPage(index)){
   float scale=Math.min(box.width()/p.getWidth(),box.height()/p.getHeight());
   int w=Math.max(1,Math.round(p.getWidth()*scale*2)),h=Math.max(1,Math.round(p.getHeight()*scale*2));
   Bitmap b=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);b.eraseColor(Color.WHITE);
   try{p.render(b,null,null,PdfRenderer.Page.RENDER_MODE_FOR_PRINT);float dw=w/2f,dh=h/2f;canvas.drawBitmap(b,null,new RectF(box.centerX()-dw/2,box.centerY()-dh/2,box.centerX()+dw/2,box.centerY()+dh/2),new Paint(3));}finally{b.recycle();}
  }
 }
 public static File prepare(File input,File out,boolean booklet)throws IOException{return prepare(input,out,booklet,false,new AtomicBoolean(false));}
 public static File prepare(File input,File out,boolean booklet,boolean landscape,AtomicBoolean stop)throws IOException{
  PdfDocument doc=new PdfDocument();
  try(ParcelFileDescriptor fd=ParcelFileDescriptor.open(input,ParcelFileDescriptor.MODE_READ_ONLY);PdfRenderer r=new PdfRenderer(fd)){
   int count=r.getPageCount();int sides=booklet?((count+3)/4)*2:count;
   if(count<1||count>100)throw new IOException("Usa un PDF de entre 1 y 100 páginas para esta versión de prueba.");
   for(int i=0;i<sides;i++){
    if(stop.get())throw new InterruptedIOException("Preparación cancelada");
    int w=(booklet||landscape)?842:595,h=(booklet||landscape)?595:842;
    PdfDocument.Page p=doc.startPage(new PdfDocument.PageInfo.Builder(w,h,i+1).create());
    p.getCanvas().drawColor(Color.WHITE);
    if(booklet){int[] pair=booklet(count,i/2,i%2==1);paint(r,pair[0],p.getCanvas(),new RectF(18,18,w/2f-9,h-18));paint(r,pair[1],p.getCanvas(),new RectF(w/2f+9,18,w-18,h-18));}
    else paint(r,i,p.getCanvas(),new RectF(18,18,w-18,h-18));
    doc.finishPage(p);
   }
   try(OutputStream o=new FileOutputStream(out)){doc.writeTo(o);}
  }finally{doc.close();}return out;
 }
}
