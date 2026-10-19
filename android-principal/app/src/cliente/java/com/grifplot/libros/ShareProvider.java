package com.grifplot.libros;
import android.content.*;import android.database.*;import android.net.*;import android.os.*;import android.provider.*;import java.io.*;
/** Grants a receiving app access to one prepared PDF; never exposes the catalog or folders. */
public final class ShareProvider extends ContentProvider {
 public boolean onCreate(){return true;}
 File resolve(Uri u)throws FileNotFoundException{String name=u.getLastPathSegment();if(name==null||!name.matches("[a-zA-Z0-9_.-]+\\.(pdf|zip|txt)"))throw new FileNotFoundException();File root=new File(getContext().getCacheDir(),"shares");File f=new File(root,name);try{if(!f.getCanonicalPath().startsWith(root.getCanonicalPath()+File.separator)||!f.isFile())throw new FileNotFoundException();}catch(IOException e){throw new FileNotFoundException();}return f;}
 public String getType(Uri uri){return uri.toString().endsWith(".zip")?"application/zip":uri.toString().endsWith(".txt")?"text/plain":"application/pdf";}
 public ParcelFileDescriptor openFile(Uri uri,String mode)throws FileNotFoundException{if(!"r".equals(mode))throw new FileNotFoundException("Solo lectura");return ParcelFileDescriptor.open(resolve(uri),ParcelFileDescriptor.MODE_READ_ONLY);}
 public Cursor query(Uri uri,String[] projection,String selection,String[] args,String sort){try{File f=resolve(uri);String[] cols=projection==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:projection;MatrixCursor c=new MatrixCursor(cols);Object[] row=new Object[cols.length];for(int i=0;i<cols.length;i++)row[i]=cols[i].equals(OpenableColumns.DISPLAY_NAME)?f.getName():cols[i].equals(OpenableColumns.SIZE)?f.length():null;c.addRow(row);return c;}catch(Exception e){return null;}}
 public Uri insert(Uri u,ContentValues v){throw new UnsupportedOperationException();}public int update(Uri u,ContentValues v,String s,String[] args){throw new UnsupportedOperationException();}public int delete(Uri u,String s,String[] args){throw new UnsupportedOperationException();}
}
