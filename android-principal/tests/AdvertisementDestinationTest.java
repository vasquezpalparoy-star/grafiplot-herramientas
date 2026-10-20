package pe.grafiplot.herramientas;
public final class AdvertisementDestinationTest {
 public static void main(String[] args){
  String body="Promoción"+AdvertisementDestination.MARKER+"https://example.com/oferta?a=1&b=2";
  check(AdvertisementDestination.url("pagina-web",body).equals("https://example.com/oferta?a=1&b=2"));
  check(AdvertisementDestination.text("pagina-web",body).equals("Promoción"));
  check(AdvertisementDestination.url("inicio",body).isEmpty());
  for(String bad:new String[]{"javascript:alert(1)","http://example.com","https://user:pass@example.com","https:///missing"})check(AdvertisementDestination.url("pagina-web","Texto"+AdvertisementDestination.MARKER+bad).isEmpty());
  check(AdvertisementDestination.text("pagina-web","Anuncio anterior").equals("Anuncio anterior"));
  System.out.println("PASS: Android notification links and visible copy; invalid destinations blocked.");
 }
 static void check(boolean yes){if(!yes)throw new AssertionError();}
}
