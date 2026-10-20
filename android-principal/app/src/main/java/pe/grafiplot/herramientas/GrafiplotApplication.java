package pe.grafiplot.herramientas;
public final class GrafiplotApplication extends android.app.Application {
 @Override public void onCreate(){super.onCreate();GrafiplotPush.initialize(this);}
}
