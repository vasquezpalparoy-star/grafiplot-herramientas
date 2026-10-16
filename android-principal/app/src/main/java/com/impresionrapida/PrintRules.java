package com.impresionrapida;
public final class PrintRules {
 private PrintRules(){}
 public static String sides(int mode,boolean landscape){return mode==0?"one-sided":landscape?"two-sided-short-edge":"two-sided-long-edge";}
 public static int sheets(int pages,int mode,int copies){if(pages<1||copies<1||copies>5||mode<0||mode>2)throw new IllegalArgumentException("Opciones inválidas");return ((pages+(mode==0?1:mode==1?2:4)-1)/(mode==0?1:mode==1?2:4))*copies;}
 public static void enforceLimit(int pages,int mode,int copies){int n=sheets(pages,mode,copies);if(n>5)throw new IllegalArgumentException("Este pedido requiere "+n+" hojas. El máximo permitido es 5 hojas, contando las copias.");}
 public static boolean terminal(int state){return state>=7&&state<=9;}
 public static String state(int state){switch(state){case 3:return "En cola";case 4:return "Retenido por la impresora";case 5:return "Imprimiendo";case 6:return "Impresión detenida";case 7:return "Impresión cancelada";case 8:return "La impresora abortó el trabajo";case 9:return "Impresión terminada";default:return "Estado pendiente de confirmar";}}
 public static String host(String input){String h=input.trim();if(h.isEmpty()||h.length()>253||!h.matches("[a-zA-Z0-9](?:[a-zA-Z0-9.-]*[a-zA-Z0-9])?"))throw new IllegalArgumentException("Escribe solo el nombre de host, por ejemplo HPE802D0.");if(h.matches("[0-9.]+")){String[] p=h.split("\\.",-1);if(p.length!=4)throw new IllegalArgumentException("Dirección IP inválida");for(String v:p)if(v.isEmpty()||v.length()>3||Integer.parseInt(v)>255)throw new IllegalArgumentException("Dirección IP inválida");}else for(String part:h.split("\\.",-1))if(part.isEmpty()||part.length()>63||part.startsWith("-")||part.endsWith("-"))throw new IllegalArgumentException("Nombre de host inválido");return h;}
}
