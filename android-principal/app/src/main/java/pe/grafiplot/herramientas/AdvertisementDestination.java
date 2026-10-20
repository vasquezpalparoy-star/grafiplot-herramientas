package pe.grafiplot.herramientas;
import java.net.URI;
/** Compatible with the existing Firestore advertisement schema. */
final class AdvertisementDestination {
 static final String MARKER="\n\nEnlace del anuncio: ";
 static String url(String target,String body){
  if(!"pagina-web".equals(target)||body==null)return "";
  int at=body.lastIndexOf(MARKER);if(at<0)return "";
  String raw=body.substring(at+MARKER.length()).trim();if(raw.length()>250)return "";
  try{URI u=new URI(raw);return "https".equals(u.getScheme())&&u.getHost()!=null&&u.getRawUserInfo()==null?raw:"";}catch(Exception e){return "";}
 }
 static String text(String target,String body){return url(target,body).isEmpty()?body:body.substring(0,body.lastIndexOf(MARKER));}
}
