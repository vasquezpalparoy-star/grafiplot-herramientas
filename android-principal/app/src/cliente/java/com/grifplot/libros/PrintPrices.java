package com.grifplot.libros;
import java.math.*;
final class PrintPrices {
 static final String[] LABELS={"A4 · Negro","A4 · Color","A5 · Negro","A5 · Color"};
 static String valid(String s){if(s==null||s.trim().isEmpty())return "";BigDecimal n=new BigDecimal(s.trim().replace(',','.'));if(n.signum()<0||n.compareTo(new BigDecimal("1000000"))>0||n.scale()>2)throw new IllegalArgumentException("Usa un precio entre 0 y 1000000 con hasta dos decimales");return n.setScale(2,RoundingMode.UNNECESSARY).toPlainString();}
 static String display(String s,String currency){try{String p=valid(s);return p.isEmpty()?"Consultar precio":currency+" "+p;}catch(Exception e){return "Consultar precio";}}
}
