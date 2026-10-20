package pe.grafiplot.herramientas;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.*;
import android.net.Uri;
import android.widget.Toast;
import com.google.firebase.*;
import com.google.firebase.messaging.*;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;
public final class GrafiplotPush {
 static final String TOPIC="grafiplot_clientes";
 static JSONObject config=new JSONObject();
 static Context context;
 static volatile boolean initialized=false,registering=false;
 static boolean refreshing=false;
 static int revision=0;
 static java.lang.ref.WeakReference<Activity> activity=new java.lang.ref.WeakReference<>(null);
 static android.content.SharedPreferences prefs(){return context.getSharedPreferences("grafiplot_avisos",0);}
 static void changed(){Activity a=activity.get();if(a instanceof MainActivity&&!a.isFinishing())((MainActivity)a).pushChanged();}
 static void error(String text){prefs().edit().putString("last_error",text).apply();changed();}
 public static synchronized void initialize(Context c){
  context=c.getApplicationContext();
  try{JSONObject bundled;try(InputStream in=context.getAssets().open("push-config.json")){bundled=new JSONObject(read(in,16000));}
   String cached=prefs().getString("public_config",null);JSONObject saved=cached==null?bundled:new JSONObject(cached);
   config=bundled.optString("projectId").equals(saved.optString("projectId"))?saved:bundled;startFirebase();
  }catch(Exception e){error("No se pudo leer la configuración de notificaciones.");}
  if(refreshing)return;refreshing=true;
  new Thread(()->{try{HttpURLConnection con=(HttpURLConnection)new URL("https://vasquezpalparoy-star.github.io/grafiplot-herramientas/push-config.json").openConnection();con.setConnectTimeout(5000);con.setReadTimeout(5000);con.setUseCaches(false);try{if(con.getResponseCode()==200){JSONObject remote=new JSONObject(read(con.getInputStream(),16000));synchronized(GrafiplotPush.class){config=remote;prefs().edit().putString("public_config",remote.toString()).apply();startFirebase();changed();}}}finally{con.disconnect();}}catch(Exception ignored){}},"grafiplot-push-config").start();
 }
 static String read(InputStream in,int max)throws IOException{ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1){if(out.size()+n>max)throw new IOException("Respuesta demasiado grande");out.write(b,0,n);}return out.toString("UTF-8");}
 static synchronized void startFirebase(){
  if(!configured())return;
  try{String appId=config.getString("androidAppId");FirebaseApp app=null;try{app=FirebaseApp.getInstance();}catch(IllegalStateException ignored){}
   if(app!=null&&(!appId.equals(app.getOptions().getApplicationId())||!config.getString("projectId").equals(app.getOptions().getProjectId()))){FirebaseMessaging.getInstance().setAutoInitEnabled(false);app.delete();app=null;initialized=false;registering=false;revision++;prefs().edit().putBoolean("subscribed",false).putBoolean("has_token",false).remove("subscribed_project").apply();}
   if(initialized)return;
   if(app==null)FirebaseApp.initializeApp(context,new FirebaseOptions.Builder().setApiKey(config.getString("apiKey")).setApplicationId(appId).setGcmSenderId(config.getString("senderId")).setProjectId(config.getString("projectId")).build());
   FirebaseMessaging.getInstance().setAutoInitEnabled(false);channels();initialized=true;
   if(prefs().getBoolean("consent",false)&&permitted())subscribe();
  }catch(Exception e){initialized=false;error("No se pudo iniciar Firebase. Actualiza la APK y vuelve a intentar.");}
 }
 public static boolean configured(){return !BuildConfig.ADMIN_APP&&config.optBoolean("notificationsEnabled",false)&&config.optBoolean("enabled",false)&&!config.optString("projectId").isEmpty()&&!config.optString("apiKey").isEmpty()&&!config.optString("androidAppId").isEmpty()&&!config.optString("senderId").isEmpty();}
 static boolean permitted(){return ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).areNotificationsEnabled()&&(Build.VERSION.SDK_INT<33||context.checkSelfPermission("android.permission.POST_NOTIFICATIONS")==PackageManager.PERMISSION_GRANTED);}
 static boolean subscribed(){return prefs().getBoolean("consent",false)&&prefs().getBoolean("subscribed",false)&&config.optString("projectId").equals(prefs().getString("subscribed_project",""));}
 public static void resume(Activity a){activity=new java.lang.ref.WeakReference<>(a);startFirebase();if(initialized&&prefs().getBoolean("consent",false)&&permitted()&&!subscribed())subscribe();changed();}
 public static void enable(Activity a){startFirebase();if(!configured()||!initialized){error("Firebase no está listo. Revisa Internet y vuelve a abrir la APK.");return;}
  prefs().edit().putBoolean("consent",true).remove("last_error").apply();
  if(Build.VERSION.SDK_INT>=33&&a.checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED){if(prefs().getBoolean("permission_asked",false)&&!a.shouldShowRequestPermissionRationale("android.permission.POST_NOTIFICATIONS")){settings(a);return;}prefs().edit().putBoolean("permission_asked",true).apply();a.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},41);return;}
  if(!permitted()){settings(a);return;}subscribe();
 }
 static String registrationError(Exception e){String text=e==null?"":String.valueOf(e.getMessage());if(text.contains("FIS_AUTH_ERROR")||text.contains("BAD_CONFIG")||text.contains("API_KEY_INVALID"))return "Firebase rechazó el registro de esta APK. Revisa su configuración de Android.";if(text.contains("SERVICE_NOT_AVAILABLE"))return "No se pudo registrar el celular. Revisa Internet y actualiza los servicios de Google Play.";return "No se pudo registrar el celular en Firebase. "+text.substring(0,Math.min(150,text.length()));}
 public static synchronized void subscribe(){
  if(!initialized||registering||!permitted()||!prefs().getBoolean("consent",false))return;
  channels();registering=true;final int attempt=revision;final String project=config.optString("projectId");prefs().edit().remove("last_error").apply();changed();FirebaseMessaging.getInstance().setAutoInitEnabled(true);
  FirebaseMessaging.getInstance().getToken().addOnCompleteListener(token->{
   if(attempt!=revision)return;
   if(!token.isSuccessful()){registering=false;prefs().edit().putBoolean("subscribed",false).apply();error(registrationError(token.getException()));return;}
   prefs().edit().putBoolean("has_token",true).apply();if(!prefs().getBoolean("consent",false)){registering=false;changed();return;}
   FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).addOnCompleteListener(task->{
    if(attempt!=revision)return;registering=false;if(!prefs().getBoolean("consent",false)){FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);changed();return;}
    prefs().edit().putBoolean("subscribed",task.isSuccessful()).putString("subscribed_project",task.isSuccessful()?project:"").apply();
    if(!task.isSuccessful())error(registrationError(task.getException()));else{prefs().edit().remove("last_error").apply();changed();}
   });
  });
 }
 public static void tokenChanged(Context c){if(context==null)initialize(c);prefs().edit().putBoolean("has_token",true).putBoolean("subscribed",false).apply();if(prefs().getBoolean("consent",false))subscribe();changed();}
 public static void granted(){prefs().edit().putBoolean("consent",true).apply();subscribe();changed();}
 public static void denied(){error("Android no concedió el permiso. Puedes activarlo en Ajustes de Android.");}
 public static void disable(){prefs().edit().putBoolean("consent",false).putBoolean("subscribed",false).apply();if(initialized){FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);FirebaseMessaging.getInstance().setAutoInitEnabled(false);}changed();}
 public static void settings(Activity a){a.startActivity(new Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE,a.getPackageName()));}
 public static void test(Activity a){if(!permitted()||!prefs().getBoolean("consent",false)){enable(a);Toast.makeText(a,"Activa los avisos y vuelve a pulsar Probar.",Toast.LENGTH_LONG).show();return;}Map<String,String> data=new HashMap<>();data.put("announcement_id","prueba-local-"+System.currentTimeMillis());data.put("title","Prueba de Grafiplot");data.put("body","Android permite mostrar avisos. Esta prueba es local y no comprueba el envío de Firebase.");data.put("target","inicio");data.put("style","aviso");displayData(a,data);}
 public static String status(){JSONObject out=new JSONObject();try{NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);NotificationChannel channel=manager.getNotificationChannel("grafiplot_avisos");out.put("configured",configured());out.put("initialized",initialized);out.put("permission",permitted());out.put("consent",prefs().getBoolean("consent",false));out.put("subscribed",subscribed());out.put("registering",registering);out.put("hasToken",prefs().getBoolean("has_token",false));out.put("channelEnabled",channel==null||channel.getImportance()!=NotificationManager.IMPORTANCE_NONE);out.put("error",prefs().getString("last_error",""));out.put("project",config.optString("projectId"));out.put("package",context.getPackageName());}catch(Exception ignored){}return out.toString();}
 public static String messages(){return prefs().getString("messages","[]");}
 public static synchronized void remember(Map<String,String> data){if(context==null)return;String id=data.get("announcement_id");if(id==null||id.length()>100)return;try{JSONArray old=new JSONArray(messages()),list=new JSONArray();JSONObject record=new JSONObject();for(String k:new String[]{"announcement_id","title","body","style","target","imageUrl"}){String v=data.get(k);record.put(k,v==null?"":v.substring(0,Math.min(v.length(),1000)));}record.put("receivedAt",System.currentTimeMillis());list.put(record);for(int i=0;i<old.length()&&list.length()<30;i++)if(!id.equals(old.getJSONObject(i).optString("announcement_id")))list.put(old.getJSONObject(i));prefs().edit().putString("messages",list.toString()).apply();}catch(Exception ignored){}}
 static void channels(){NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);manager.createNotificationChannel(new NotificationChannel("grafiplot_avisos","Avisos de Grafiplot",NotificationManager.IMPORTANCE_DEFAULT));manager.createNotificationChannel(new NotificationChannel("grafiplot_importante","Avisos importantes",NotificationManager.IMPORTANCE_HIGH));}
 public static void display(Context c,RemoteMessage message){context=c.getApplicationContext();Map<String,String> data=new HashMap<>(message.getData());RemoteMessage.Notification n=message.getNotification();if(n!=null){if(!data.containsKey("title"))data.put("title",n.getTitle()==null?"Grafiplot":n.getTitle());if(!data.containsKey("body"))data.put("body",n.getBody()==null?"":n.getBody());}if(n!=null&&n.getImageUrl()!=null&&!data.containsKey("imageUrl"))data.put("imageUrl",n.getImageUrl().toString());if(!data.containsKey("announcement_id"))data.put("announcement_id",message.getMessageId()==null?UUID.randomUUID().toString():message.getMessageId());displayData(c,data);}
 static void displayData(Context c,Map<String,String> data){context=c.getApplicationContext();if("chat".equals(data.get("target")))return;remember(data);if(!permitted()||!prefs().getBoolean("consent",false))return;channels();String bodyText=AdvertisementDestination.text(data.get("target"),data.getOrDefault("body",""));String style=data.getOrDefault("style","aviso"),id=data.get("announcement_id");Intent intent=new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);for(Map.Entry<String,String> e:data.entrySet())intent.putExtra(e.getKey(),e.getValue());PendingIntent action=PendingIntent.getActivity(context,id.hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);Notification.Builder builder=new Notification.Builder(context,style.equals("importante")?"grafiplot_importante":"grafiplot_avisos").setSmallIcon(pe.grafiplot.herramientas.R.drawable.ic_notification).setContentTitle(data.getOrDefault("title","Grafiplot")).setContentText(bodyText).setColor(style.equals("promocion")?0xff177b66:0xffdf122a).setAutoCancel(true).setContentIntent(action).setStyle(new Notification.BigTextStyle().bigText(bodyText)).addAction(new Notification.Action.Builder((android.graphics.drawable.Icon)null,"Abrir",action).build());if(data.get("imageUrl")!=null&&!data.get("imageUrl").isEmpty()){String url=data.get("imageUrl");if(url!=null&&url.startsWith("https://"))try{HttpURLConnection con=(HttpURLConnection)new URL(url).openConnection();con.setConnectTimeout(5000);con.setReadTimeout(5000);try{byte[] bytes=readBytes(con.getInputStream(),1024*1024);BitmapFactory.Options options=new BitmapFactory.Options();options.inJustDecodeBounds=true;BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);options.inSampleSize=Math.max(1,Math.max(options.outWidth,options.outHeight)/1200);options.inJustDecodeBounds=false;Bitmap image=BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);if(image!=null)builder.setStyle(new Notification.BigPictureStyle().bigPicture(image).setSummaryText(bodyText));}finally{con.disconnect();}}catch(Exception ignored){}}((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(null,id.hashCode(),builder.build());}
 static byte[] readBytes(InputStream in,int max)throws IOException{try(InputStream stream=in;ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[4096];int n;while((n=stream.read(b))!=-1){if(out.size()+n>max)throw new IOException("Imagen demasiado grande");out.write(b,0,n);}return out.toByteArray();}}
}
