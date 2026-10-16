package pe.grafiplot.herramientas;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONObject;
import java.net.*;
import java.io.*;
public final class GrafiplotChat {
 static android.content.SharedPreferences prefs(){return GrafiplotPush.context.getSharedPreferences("grafiplot_chat",0);}
 public static void enable(Activity activity){if(Build.VERSION.SDK_INT>=33&&activity.checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED){activity.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},42);return;}prefs().edit().putBoolean("consent",true).apply();}
 public static void granted(){prefs().edit().putBoolean("consent",true).apply();}
 public static boolean accepts(String uid){return prefs().getBoolean("consent",false)&&uid!=null&&uid.equals(prefs().getString("uid",""));}
 public static void clear(String idToken){String token=prefs().getString("deviceToken","");prefs().edit().remove("uid").putInt("generation",prefs().getInt("generation",0)+1).apply();NotificationManager manager=(NotificationManager)GrafiplotPush.context.getSystemService(Context.NOTIFICATION_SERVICE);for(android.service.notification.StatusBarNotification n:manager.getActiveNotifications())if("grafiplot_chat".equals(n.getTag()))manager.cancel(n.getTag(),n.getId());if(!token.isEmpty())post(idToken,token,true,-1);}
 public static void register(String idToken){if(!GrafiplotPush.initialized||!GrafiplotPush.permitted()||!prefs().getBoolean("consent",false))return;final int generation=prefs().getInt("generation",0);FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token->post(idToken,token,false,generation));}
 static void post(String idToken,String token,boolean remove,int generation){if(idToken==null||idToken.length()<20||idToken.length()>10000)return;String endpoint=GrafiplotPush.config.optString("chatDeviceUrl","");if(!"https://intsmwiivbdaversvlky.supabase.co/functions/v1/grafiplot-chat".equals(endpoint))return;new Thread(()->{try{HttpURLConnection connection=(HttpURLConnection)new URL(endpoint).openConnection();connection.setConnectTimeout(10000);connection.setReadTimeout(10000);connection.setRequestMethod("POST");connection.setDoOutput(true);connection.setRequestProperty("Content-Type","application/json");connection.setRequestProperty("Authorization","Bearer "+idToken);try{byte[] body=new JSONObject().put("token",token).put("remove",remove).toString().getBytes("UTF-8");try(OutputStream out=connection.getOutputStream()){out.write(body);}if(connection.getResponseCode()==200&&!remove){JSONObject result=new JSONObject(GrafiplotPush.read(connection.getInputStream(),16000));if(result.optBoolean("registered")&&generation==prefs().getInt("generation",0))prefs().edit().putString("uid",result.getString("uid")).putString("deviceToken",token).apply();}}finally{connection.disconnect();}}catch(Exception ignored){}},"grafiplot-chat-device").start();}
}
