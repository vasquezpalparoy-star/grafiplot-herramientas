package pe.grafiplot.herramientas;
import android.content.*;import android.database.*;import android.net.Uri;import android.os.ParcelFileDescriptor;import android.provider.OpenableColumns;import android.webkit.MimeTypeMap;import java.io.*;
/** Read-only access to a single, explicitly prepared document attachment. */
public final class DocumentShareProvider extends ContentProvider {
 public boolean onCreate(){return true;}
 private File resolve(Uri uri)throws FileNotFoundException{
  String name=uri.getLastPathSegment();if(name==null||!name.matches("[a-zA-Z0-9_.-]{1,160}"))throw new FileNotFoundException();
  File root=new File(getContext().getCacheDir(),"document-shares"),file=new File(root,name);
  try{if(!file.isFile()||!file.getCanonicalPath().startsWith(root.getCanonicalPath()+File.separator))throw new FileNotFoundException();}catch(IOException e){throw new FileNotFoundException();}return file;
 }
 public String getType(Uri uri){String name=uri.getLastPathSegment();if(name==null)return "application/octet-stream";int at=name.lastIndexOf('.');String mime=at<0?null:MimeTypeMap.getSingleton().getMimeTypeFromExtension(name.substring(at+1).toLowerCase(java.util.Locale.ROOT));return mime==null?"application/octet-stream":mime;}
 public ParcelFileDescriptor openFile(Uri uri,String mode)throws FileNotFoundException{if(!"r".equals(mode))throw new FileNotFoundException("Solo lectura");return ParcelFileDescriptor.open(resolve(uri),ParcelFileDescriptor.MODE_READ_ONLY);}
 public Cursor query(Uri uri,String[] projection,String selection,String[] args,String order){try{File f=resolve(uri);String[] columns=projection==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:projection;MatrixCursor c=new MatrixCursor(columns);Object[] row=new Object[columns.length];for(int i=0;i<row.length;i++)row[i]=OpenableColumns.DISPLAY_NAME.equals(columns[i])?f.getName():OpenableColumns.SIZE.equals(columns[i])?f.length():null;c.addRow(row);return c;}catch(FileNotFoundException e){return null;}}
 public Uri insert(Uri uri,ContentValues v){throw new UnsupportedOperationException();}public int update(Uri uri,ContentValues v,String where,String[] args){throw new UnsupportedOperationException();}public int delete(Uri uri,String where,String[] args){throw new UnsupportedOperationException();}
}
