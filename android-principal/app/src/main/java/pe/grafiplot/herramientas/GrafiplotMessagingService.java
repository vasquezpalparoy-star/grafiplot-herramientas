package pe.grafiplot.herramientas;
import com.google.firebase.messaging.*;
public final class GrafiplotMessagingService extends FirebaseMessagingService {
 @Override public void onMessageReceived(RemoteMessage message){GrafiplotPush.display(getApplicationContext(),message);}
}
