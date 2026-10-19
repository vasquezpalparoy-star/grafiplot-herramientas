package com.grifplot.libros;
import java.net.*;import java.util.*;
final class BrowserRules {
 static String google(String query,boolean pdfOnly){try{return "https://www.google.com/search?q="+URLEncoder.encode(query.trim()+(pdfOnly?" filetype:pdf":""),"UTF-8")+"&hl=es";}catch(Exception e){throw new IllegalArgumentException(e);}}
 static boolean web(String url){try{String s=new URI(url).getScheme();return "https".equalsIgnoreCase(s)||"http".equalsIgnoreCase(s);}catch(Exception e){return false;}}
 static boolean pdf(String url){try{return new URI(url).getPath().toLowerCase(Locale.ROOT).endsWith(".pdf");}catch(Exception e){return false;}}
}
