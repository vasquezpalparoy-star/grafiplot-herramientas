package com.impresionrapida;
import android.app.Activity;
import android.os.*;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.concurrent.*;

/** A bounded-memory, page-by-page viewer for every source or imposed page. */
public final class PreviewActivity extends Activity {
 final ExecutorService render=Executors.newSingleThreadExecutor();volatile int revision;volatile boolean closed;int index,count,mode,sourcePages;boolean source;TextView counter;Button previous,next;SeekBar seek;Spinner version;ZoomPage image;
 int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
 @Override public void onCreate(Bundle state){super.onCreate(state);mode=getIntent().getIntExtra("mode",0);sourcePages=getIntent().getIntExtra("pages",1);
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(16),dp(12),dp(16),dp(12));root.setBackgroundColor(0xfff3f6ff);root.setOnApplyWindowInsetsListener((v,i)->{v.setPadding(dp(16),i.getSystemWindowInsetTop()+dp(12),dp(16),i.getSystemWindowInsetBottom()+dp(12));return i;});setContentView(root);
  TextView title=new TextView(this);title.setText("Vista previa completa");title.setTextSize(24);title.setTextColor(0xff18264b);root.addView(title);
  version=new Spinner(this);version.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Como se imprimirá","Documento original"}));root.addView(version);
  counter=new TextView(this);counter.setTextSize(16);counter.setPadding(0,dp(8),0,dp(8));root.addView(counter);
  image=new ZoomPage();root.addView(image,new LinearLayout.LayoutParams(-1,0,1));
  TextView hint=new TextView(this);hint.setText("Amplía con dos dedos y arrastra para revisar.");hint.setTextColor(0xff586689);hint.setTextSize(13);root.addView(hint);
  seek=new SeekBar(this);root.addView(seek);seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onStartTrackingTouch(SeekBar s){}public void onProgressChanged(SeekBar s,int p,boolean user){if(user){index=p;load();}}public void onStopTrackingTouch(SeekBar s){}});
  LinearLayout nav=new LinearLayout(this);root.addView(nav);previous=new Button(this);previous.setText("‹ Anterior");next=new Button(this);next.setText("Siguiente ›");nav.addView(previous,new LinearLayout.LayoutParams(0,dp(52),1));nav.addView(next,new LinearLayout.LayoutParams(0,dp(52),1));previous.setOnClickListener(v->{if(index>0){index--;load();}});next.setOnClickListener(v->{if(index+1<count){index++;load();}});
  Button done=new Button(this);done.setText("Volver a las opciones");done.setAllCaps(false);done.setTextColor(0xff6554df);root.addView(done);done.setOnClickListener(v->finish());
  version.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int pos,long id){source=pos==1;index=0;load();}});
 }
 void load(){final int ticket=++revision;final int pageIndex=index;final boolean original=source;counter.setText("Cargando página "+(index+1)+"…");render.execute(()->{if(closed||ticket!=revision)return;Bitmap bitmap=null;try{
   File f=new File(getIntent().getStringExtra(original?"source":"prepared"));if(!f.getCanonicalPath().startsWith(getCacheDir().getCanonicalPath()+File.separator))throw new IOException("Documento inválido");
   int total;try(ParcelFileDescriptor fd=ParcelFileDescriptor.open(f,ParcelFileDescriptor.MODE_READ_ONLY);PdfRenderer pdf=new PdfRenderer(fd)){total=pdf.getPageCount();try(PdfRenderer.Page page=pdf.openPage(Math.min(pageIndex,total-1))){int width=Math.min(1500,Math.max(900,getResources().getDisplayMetrics().widthPixels));int height=Math.max(1,Math.round((float)width*page.getHeight()/page.getWidth()));if(height>2200){width=Math.max(1,Math.round(width*2200f/height));height=2200;}bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);bitmap.eraseColor(Color.WHITE);page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);}}
   final Bitmap ready=bitmap;runOnUiThread(()->{if(closed||ticket!=revision){ready.recycle();return;}count=total;seek.setMax(count-1);seek.setProgress(pageIndex);previous.setEnabled(pageIndex>0);next.setEnabled(pageIndex+1<count);String caption=(original?"Página ":"Cara ")+(pageIndex+1)+" de "+count;
    if(!original&&mode==2){int[] pair=Documents.booklet(sourcePages,pageIndex/2,pageIndex%2==1);caption+=" · hoja "+(pageIndex/2+1)+(pageIndex%2==0?" · frente":" · reverso")+"\nPáginas "+(pair[0]<0?"en blanco":pair[0]+1)+" y "+(pair[1]<0?"en blanco":pair[1]+1);}
    counter.setText(caption);image.setPage(ready);
   });
  }catch(Exception e){if(bitmap!=null)bitmap.recycle();runOnUiThread(()->{if(!closed&&ticket==revision)counter.setText("No se pudo mostrar: "+e.getMessage());});}
 });}
 @Override protected void onDestroy(){closed=true;revision++;render.shutdown();image.setPage(null);super.onDestroy();}
 final class ZoomPage extends View {
  Bitmap bitmap;float zoom=1,panX,panY,lastX,lastY;final Paint paint=new Paint(3);final ScaleGestureDetector scale;final GestureDetector gestures;
  ZoomPage(){super(PreviewActivity.this);setBackgroundColor(0xffdfe5f2);scale=new ScaleGestureDetector(getContext(),new ScaleGestureDetector.SimpleOnScaleGestureListener(){@Override public boolean onScale(ScaleGestureDetector d){zoom=Math.max(1,Math.min(5,zoom*d.getScaleFactor()));invalidate();return true;}});gestures=new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){@Override public boolean onDown(android.view.MotionEvent e){return true;}@Override public boolean onDoubleTap(android.view.MotionEvent e){zoom=1;panX=panY=0;invalidate();return true;}});}
  void setPage(Bitmap next){Bitmap old=bitmap;bitmap=next;zoom=1;panX=panY=0;invalidate();if(old!=null&&old!=next)old.recycle();}
  @Override protected void onDraw(Canvas c){super.onDraw(c);if(bitmap==null||bitmap.isRecycled())return;float fit=Math.min((float)getWidth()/bitmap.getWidth(),(float)getHeight()/bitmap.getHeight())*.96f;float w=bitmap.getWidth()*fit*zoom,h=bitmap.getHeight()*fit*zoom;c.drawBitmap(bitmap,null,new RectF((getWidth()-w)/2+panX,(getHeight()-h)/2+panY,(getWidth()+w)/2+panX,(getHeight()+h)/2+panY),paint);}
  @Override public boolean onTouchEvent(android.view.MotionEvent e){scale.onTouchEvent(e);gestures.onTouchEvent(e);if(e.getActionMasked()==android.view.MotionEvent.ACTION_DOWN){lastX=e.getX();lastY=e.getY();}else if(e.getActionMasked()==android.view.MotionEvent.ACTION_MOVE){if(!scale.isInProgress()&&e.getPointerCount()==1&&zoom>1){panX+=e.getX()-lastX;panY+=e.getY()-lastY;invalidate();}lastX=e.getX();lastY=e.getY();}else if(e.getActionMasked()==android.view.MotionEvent.ACTION_POINTER_UP){int remaining=e.getActionIndex()==0?1:0;lastX=e.getX(remaining);lastY=e.getY(remaining);}return true;}
 }
}
