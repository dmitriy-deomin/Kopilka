package dmitriy.deomin.kopilka;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends AppCompatActivity implements TextView.OnEditorActionListener {

    static Context context;
    long run; // первый ли раз запущена программы
    long stat;
    static long auto_save;
    TextView time_natikalo;
    TextView boblo_natikalo;
    Button boblo_vivesti;

    //777777777777777777777777777777
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    public static long boblo;
    //7777777777777777777777777777777


    int heitch; // ширина экрана


    //game1
    String vopros;
    String otvet;
    String masiv_voprosov[];
    int podskazka;
    int pravelno_otvetil;
    EditText editText_otvet;


    //game2
    String vigral_user,vigral2_user;
    BitmapDrawable dbitmap_fon,dbitmap_o,dbitmap_x;





    //game3
    private WebView mWebView;
    private long mLastBackPress;
    private static final long mBackPressThreshold = 3500;
    private static final String IS_FULLSCREEN_PREF = "is_fullscreen_pref";
    private static boolean DEF_FULLSCREEN = true;
    private long mLastTouch;
    private static final long mTouchThreshold = 2000;



    public static String user_p;

    int brightness;
    float old_svet;
    int return_svet;

    static SharedPreferences mSettings; // сохранялка
    final String APP_PREFERENCES = "mysettings"; // файл сохранялки


    @SuppressLint({ "SetJavaScriptEnabled", "NewApi", "ShowToast" })

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = getApplicationContext();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //ширина экрана шшшшшшшшшшшшшшшшшшшшшшшшшш
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        heitch = display.getHeight();
        //шшшшшшшшшшшшшшшшшшшшшшшшшшшшшшшшшшшшш
        int cub = heitch/3-10;
        Bitmap bitmap_fon,bitmap_o,bitmap_x;

        bitmap_fon = BitmapFactory.decodeResource(context.getResources(),R.drawable.button_background);
        bitmap_fon = Bitmap.createScaledBitmap(bitmap_fon,cub,cub, true);
        bitmap_x = BitmapFactory.decodeResource(context.getResources(),R.drawable.iks);
        bitmap_x = Bitmap.createScaledBitmap(bitmap_x,cub,cub, true);
        bitmap_o = BitmapFactory.decodeResource(context.getResources(),R.drawable.nol);
        bitmap_o = Bitmap.createScaledBitmap(bitmap_o,cub,cub, true);

        dbitmap_fon = new BitmapDrawable(getResources(), bitmap_fon);
        dbitmap_x = new BitmapDrawable(getResources(), bitmap_x);
        dbitmap_o = new BitmapDrawable(getResources(), bitmap_o);


        run = save_read("run");
        stat = save_read("stat");

        //**************яркость текущая ****************************
        try {
            old_svet = Settings.System.getFloat(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
            Log.d("TTT",String.valueOf(old_svet));//max 255.0f , min 10.0f
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if(old_svet>10.0f) {
            ((Button) findViewById(R.id.button_svet_regulator)).setText("Убавить яркость");
        }else{
            ((Button) findViewById(R.id.button_svet_regulator)).setText("Восстановить");
        }

        return_svet = save_read_int("return_svet");
        //**************************************

        time_natikalo = (TextView)findViewById(R.id.textView_vrema);
        boblo_natikalo = (TextView)findViewById(R.id.textView_boblo);
        boblo_vivesti = (Button)findViewById(R.id.button_boblo_snat);

        masiv_voprosov = getResources().getStringArray(R.array.voprosi);
        podskazka = 0;

        user_p = String.valueOf(save_read("u_p"));
        ((TextView)findViewById(R.id.textView_phone)).setText(String.valueOf(save_read("akkaunt")));
      //  ((TextView)findViewById(R.id.textView5)).setText("Ver:"+getVersion());

        ((LinearLayout)findViewById(R.id.menu_game)).setVisibility(View.VISIBLE);



        editText_otvet = (EditText)findViewById(R.id.editText_otvet);
        editText_otvet.setOnEditorActionListener(this);


        if(save_read_string("r_1").length()<1){
            save_value_string("r_1", "Comp");
            ((Button) findViewById(R.id.button_game2_regim_igri)).setText("Comp");
        }else {
            ((Button) findViewById(R.id.button_game2_regim_igri)).setText(save_read_string("r_1"));
        }

        if(isNetworkConnected()) {
            if (run == 0) {
                Intent i = new Intent(this, Registacia.class);
                startActivity(i);
            } else {
                ReadTime readTime = new ReadTime();
                readTime.execute("http://i9027296.bget.ru/Kopilka/Read_time.php?key=" + user_p);

                if (stat == 0) {
                    ((LinearLayout) findViewById(R.id.statistika)).setVisibility(View.GONE);
                } else {
                    ((LinearLayout) findViewById(R.id.statistika)).setVisibility(View.VISIBLE);
                }

                smena_texta(true);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Нужно интернет соединение",Toast.LENGTH_LONG).show();
        }



        //game3************************

        // Load webview with game
        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings settings = mWebView.getSettings();
        String packageName = getPackageName();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setDatabasePath("/data/data/" + packageName + "/databases");

        // If there is a previous instance restore it in the webview
        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            mWebView.loadUrl("file:///android_asset/2048/index.html");
        }


        // Set fullscreen toggle on webview LongClick
        mWebView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Implement a long touch action by comparing
                // time between action up and action down
                long currentTime = System.currentTimeMillis();
                if ((event.getAction() == MotionEvent.ACTION_UP)
                        && (Math.abs(currentTime - mLastTouch) > mTouchThreshold)) {
                    boolean toggledFullScreen = !isFullScreen();
                    saveFullScreen(toggledFullScreen);
                    applyFullScreen(toggledFullScreen);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mLastTouch = currentTime;
                }
                // return so that the event isn't consumed but used
                // by the webview as well
                return false;
            }
        });

    }



public void svet(View v){

    if(old_svet>10.0f){
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        WindowManager.LayoutParams layoutpars = getWindow().getAttributes();
        layoutpars.screenBrightness = 10.0f;
        return_svet = (int) old_svet;//сохраняем
        save_value_int("return_svet",return_svet); // сохраним в память

        old_svet = 10.0f;
        getWindow().setAttributes(layoutpars);
        ((Button) findViewById(R.id.button_svet_regulator)).setText("Восстановить");
    }else{
        return_svet = save_read_int("return_svet");
        old_svet = return_svet;
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, return_svet);
        ((Button) findViewById(R.id.button_svet_regulator)).setText("Убавить яркость");
    }

}

    //game3
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mWebView.saveState(outState);
    }
    private void saveFullScreen(boolean isFullScreen) {
        // save in preferences
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean(IS_FULLSCREEN_PREF, isFullScreen);
        editor.commit();
    }

    private boolean isFullScreen() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(IS_FULLSCREEN_PREF,
                DEF_FULLSCREEN);
    }

    /**
     * Toggles the activitys fullscreen mode by setting the corresponding window flag
     * @param isFullScreen
     */
    private void applyFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



    public void Help(View view) {
        Intent i = new Intent(this,Help.class);
        startActivity(i);
    }

    private String getVersion(){
        try {
            PackageManager packageManager=getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        }
        catch (  PackageManager.NameNotFoundException e) {
            return "?";
        }
    }
    //*****************************************************************************************************
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void save_value(String Key,long Value){ //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong(Key, Value);
        editor.apply();
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void save_value_int(String Key,int Value){ //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(Key, Value);
        editor.apply();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void save_value_string(String Key,String Value){ //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public static long save_read(String key_save){  // чтение настройки
        if(mSettings.contains(key_save)) {
            return (mSettings.getLong(key_save, 0));
        }else{
            return 0;
        }
    }
    public static int save_read_int(String key_save){  // чтение настройки
        if(mSettings.contains(key_save)) {
            return (mSettings.getInt(key_save, 0));
        }else{
            return 0;
        }
    }

    public String save_read_string(String key_save){  // чтение настройки
        if(mSettings.contains(key_save)) {
            return (mSettings.getString(key_save, ""));
        }else{
            return "";
        }
    }

    private static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

//***********************************************************************************

    public static void Send_server() {
        if(isNetworkConnected()){
           AddTime addTime = new AddTime();
            addTime.execute();
        }
    }

    public void Hide_Shou(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_stat)).startAnimation(anim);
        if(stat==0){
            stat = 1;
            save_value("stat",1);
            ((LinearLayout)findViewById(R.id.statistika)).setVisibility(View.VISIBLE);
        }else{
            stat = 0;
            save_value("stat",0);
            ((LinearLayout)findViewById(R.id.statistika)).setVisibility(View.GONE);
        }
    }

    public void Dosug(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_dosug)).startAnimation(anim);

        if(((LinearLayout)findViewById(R.id.menu_game)).getVisibility()==View.GONE){
            ((RelativeLayout)findViewById(R.id.Voprosnik)).setVisibility(View.GONE);
            ((RelativeLayout)findViewById(R.id.Tik_tak_toe)).setVisibility(View.GONE);
            ((RelativeLayout)findViewById(R.id.game3_loaut)).setVisibility(View.GONE);
            ((LinearLayout)findViewById(R.id.menu_game)).setVisibility(View.VISIBLE);
        }else{
            ((LinearLayout)findViewById(R.id.menu_game)).setVisibility(View.GONE);
        }

    }

    public void Game1(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_game1)).startAnimation(anim);
        ((LinearLayout)findViewById(R.id.menu_game)).setVisibility(View.GONE);

        ((RelativeLayout)findViewById(R.id.Voprosnik)).setVisibility(View.VISIBLE);




        String vop;
        int rand = random(masiv_voprosov.length-1,0);
        vop = masiv_voprosov[rand].toString();

        ((TextView)findViewById(R.id.textView_game1_size_voprosi)).setText(String.valueOf(masiv_voprosov.length)+"/"+String.valueOf(rand));

        String mas2[] = vop.split("otv");

        vopros = mas2[0].toString();
        otvet = mas2[1].toString();

        ((TextView) findViewById(R.id.textView_ava_vopros)).setText(vopros);
    }

    public int random(int max,int min){
        Random rand = new Random();
        int n = rand.nextInt(max)+min;
        return n;
    }

    public void Game1_propustit(View view) {
        String vop;
        vop = masiv_voprosov[random(masiv_voprosov.length-1,0)].toString();
        String mas2[] = vop.split("otv");
        vopros = mas2[0].toString();
        otvet = mas2[1].toString();
        ((TextView) findViewById(R.id.textView_ava_vopros)).setText(vopros);
        podskazka =0;
    }

    public void Otvet_game1(View view) {


        if(view.getId() == ((Button)findViewById(R.id.button_close_game1)).getId()){
            ((RelativeLayout)findViewById(R.id.Voprosnik)).setVisibility(View.GONE);
            return;
        }


       if(otvet.equals(((EditText)findViewById(R.id.editText_otvet)).getText().toString())){
           Toast.makeText(getApplicationContext(),"Правельно",Toast.LENGTH_LONG).show();
           ((EditText)findViewById(R.id.editText_otvet)).setText("");
           podskazka =0;
           pravelno_otvetil++;
           ((TextView)findViewById(R.id.textView_game1_good_otvet)).setText(String.valueOf(pravelno_otvetil));

           String vop;
           int rand  = random(masiv_voprosov.length-1,0);
           vop = masiv_voprosov[rand].toString();
           ((TextView)findViewById(R.id.textView_game1_size_voprosi)).setText(String.valueOf(masiv_voprosov.length)+"/"+String.valueOf(rand));

           String mas2[] = vop.split("otv");
           vopros = mas2[0].toString();
           otvet = mas2[1].toString();
           ((TextView) findViewById(R.id.textView_ava_vopros)).setText(vopros);

       }else{
           podskazka = podskazka+1;
           if(podskazka==otvet.length()){
               podskazka =0;
               Toast.makeText(getApplicationContext(), "Правельный ответ:" + otvet, Toast.LENGTH_LONG).show();
               ((EditText)findViewById(R.id.editText_otvet)).setText("");

               String vop;
               int rand  = random(masiv_voprosov.length-1,0);
               vop = masiv_voprosov[rand].toString();
               ((TextView)findViewById(R.id.textView_game1_size_voprosi)).setText(String.valueOf(masiv_voprosov.length)+"/"+String.valueOf(rand));

               String mas2[] = vop.split("otv");
               vopros = mas2[0].toString();
               otvet = mas2[1].toString();
               ((TextView) findViewById(R.id.textView_ava_vopros)).setText(vopros);


           }else {
               Toast.makeText(getApplicationContext(), "Непривельно\nподсказка: " + otvet.substring(0, podskazka), Toast.LENGTH_LONG).show();
           }
       }
    }
//Game2 kod***************************************************
    public void Game2(View view){

        if(view.getId()==((Button)findViewById(R.id.button_game2_regim_igri)).getId()){

            if(((Button)findViewById(R.id.button_game2_regim_igri)).getText().equals("1/1")){
                save_value_string("r_1", "Comp");
                ((Button) findViewById(R.id.button_game2_regim_igri)).setText("Comp");
                vigral_user = "Вы выйграли!!!";
                vigral2_user = "Вы проиграли (";
            }else{
                save_value_string("r_1","1/1");
                ((Button)findViewById(R.id.button_game2_regim_igri)).setText("1/1");
                vigral_user = "Выграл первый игрок";
                vigral2_user = "Выграл второй игрок";
            }

        }


        //open
        if(view.getId()==((Button)findViewById(R.id.button_game2)).getId()){
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
            ((Button)findViewById(R.id.button_game2)).startAnimation(anim);
            ((LinearLayout)findViewById(R.id.menu_game)).setVisibility(View.GONE);
            ((RelativeLayout)findViewById(R.id.Tik_tak_toe)).setVisibility(View.VISIBLE);



            ((Button)findViewById(R.id.button_tik1)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik2)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik3)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik4)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik5)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik6)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik7)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik8)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik9)).setBackground(dbitmap_fon);

            Game2_on_of_button(true);
            ((TextView)findViewById(R.id.textView_game2_info)).setText("Ходит первый игрок");

            if(!((Button)findViewById(R.id.button_game2_regim_igri)).getText().equals("1/1")){
                vigral_user = "Вы выйграли!!!";
                vigral2_user = "Вы проиграли (";
            }else{
                vigral_user = "Выйграл первый игрок";
                vigral2_user = "Выйграл второй игрок";
            }
        }

        //

        //restart
        if(view.getId()==((Button)findViewById(R.id.button_game2_restart)).getId()){
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
            ((Button)findViewById(R.id.button_game2_restart)).startAnimation(anim);

            ((Button)findViewById(R.id.button_tik1)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik2)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik3)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik4)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik5)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik6)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik7)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik8)).setBackground(dbitmap_fon);
            ((Button)findViewById(R.id.button_tik9)).setBackground(dbitmap_fon);

            Game2_on_of_button(true);
            ((TextView)findViewById(R.id.textView_game2_info)).setText("Ходит первый игрок");
            if(!((Button)findViewById(R.id.button_game2_regim_igri)).getText().equals("1/1")){
                vigral_user = "Вы выйграли!!!";
                vigral2_user = "Вы проиграли (";
            }else{
                vigral_user = "Выйграл первый игрок";
                vigral2_user = "Выйграл второй игрок";
            }
        }
        //


        if(view.getId()==((Button)findViewById(R.id.button_tik1)).getId()){
            if(((Button)findViewById(R.id.button_tik1)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState()))
            {
                ((Button) findViewById(R.id.button_tik1)).setBackground(dbitmap_x);
                if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                    Game2_on_of_button(false);
                    Game2_hod_comp();
                }
            }
        }
        if(view.getId()==((Button)findViewById(R.id.button_tik2)).getId()){
            if(((Button)findViewById(R.id.button_tik2)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState())) {
                ((Button) findViewById(R.id.button_tik2)).setBackground(dbitmap_x);
                if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                    Game2_on_of_button(false);
                    Game2_hod_comp();
                }
            }
        }
        if(view.getId()==((Button)findViewById(R.id.button_tik3)).getId()){
            if(((Button)findViewById(R.id.button_tik3)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState())) {
                ((Button) findViewById(R.id.button_tik3)).setBackground(dbitmap_x);
                if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                    Game2_on_of_button(false);
                    Game2_hod_comp();
                }
            }

        }
        if(view.getId()==((Button)findViewById(R.id.button_tik4)).getId()) {
            if (((Button) findViewById(R.id.button_tik4)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState())) {
                ((Button) findViewById(R.id.button_tik4)).setBackground(dbitmap_x);
            if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                Game2_on_of_button(false);
                Game2_hod_comp();
            }
        }

        }
        if(view.getId()==((Button)findViewById(R.id.button_tik5)).getId()){
            if(((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState())) {
                ((Button) findViewById(R.id.button_tik5)).setBackground(dbitmap_x);
                if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                    Game2_on_of_button(false);
                    Game2_hod_comp();
                }
            }

        }
        if(view.getId()==((Button)findViewById(R.id.button_tik6)).getId()){
            if(((Button)findViewById(R.id.button_tik6)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState())) {
                ((Button) findViewById(R.id.button_tik6)).setBackground(dbitmap_x);
                if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                    Game2_on_of_button(false);
                    Game2_hod_comp();
                }
            }

        }
        if(view.getId()==((Button)findViewById(R.id.button_tik7)).getId()){
            if(((Button)findViewById(R.id.button_tik7)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState())) {
                ((Button) findViewById(R.id.button_tik7)).setBackground(dbitmap_x);
                if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                    Game2_on_of_button(false);
                    Game2_hod_comp();
                }
            }

        }
        if(view.getId()==((Button)findViewById(R.id.button_tik8)).getId()){
            if(((Button)findViewById(R.id.button_tik8)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState())) {
                ((Button) findViewById(R.id.button_tik8)).setBackground(dbitmap_x);
                if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                    Game2_on_of_button(false);
                    Game2_hod_comp();
                }
            }

        }
        if(view.getId()==((Button)findViewById(R.id.button_tik9)).getId()){
            if(((Button)findViewById(R.id.button_tik9)).getBackground().getConstantState().equals(dbitmap_fon.getConstantState())) {
                ((Button) findViewById(R.id.button_tik9)).setBackground(dbitmap_x);
                if (Game2_proverka_viigrish_krest() == false & Game2_proverka_viigrish_nol() == false) {
                    Game2_on_of_button(false);
                    Game2_hod_comp();
                }
            }

        }
        //close
        if(view.getId()==((Button)findViewById(R.id.button_game2_close)).getId()){
            ((RelativeLayout)findViewById(R.id.Tik_tak_toe)).setVisibility(View.GONE);
        }
    }

    public boolean Game2_proverka_viigrish_krest(){
        if(
                ((Button)findViewById(R.id.button_tik1)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik2)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik3)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik1)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik4)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik7)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik1)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik9)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik2)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik8)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik3)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik6)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik9)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik3)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik7)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik4)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik6)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik7)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik8)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())&
                ((Button)findViewById(R.id.button_tik9)).getBackground().getConstantState().equals(dbitmap_x.getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral_user);
            Game2_on_of_button(false);
            return true;
        }
        return false;
    }

    public boolean Game2_proverka_viigrish_nol(){
        if(
                ((Button)findViewById(R.id.button_tik1)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik2)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik3)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral2_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik1)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik4)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik7)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral2_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik1)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik9)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral2_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik2)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik8)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral2_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik3)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik6)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik9)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral2_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik3)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik7)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral2_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik4)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik5)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik6)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral2_user);
            Game2_on_of_button(false);
            return true;
        }
        if(
                ((Button)findViewById(R.id.button_tik7)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik8)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())&
                        ((Button)findViewById(R.id.button_tik9)).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.nol).getConstantState())
                ){
            ((TextView)findViewById(R.id.textView_game2_info)).setText(vigral2_user);
            Game2_on_of_button(false);
            return true;
        }
        return false;
    }

    public void Game2_on_of_button(boolean sos){
        ((Button) findViewById(R.id.button_tik1)).setEnabled(sos);
        ((Button) findViewById(R.id.button_tik2)).setEnabled(sos);
        ((Button) findViewById(R.id.button_tik3)).setEnabled(sos);
        ((Button) findViewById(R.id.button_tik4)).setEnabled(sos);
        ((Button) findViewById(R.id.button_tik5)).setEnabled(sos);
        ((Button) findViewById(R.id.button_tik6)).setEnabled(sos);
        ((Button) findViewById(R.id.button_tik7)).setEnabled(sos);
        ((Button) findViewById(R.id.button_tik8)).setEnabled(sos);
        ((Button) findViewById(R.id.button_tik9)).setEnabled(sos);
    }

    public void Game2_hod_comp() {
        //1-1
        if(((Button)findViewById(R.id.button_game2_regim_igri)).getText().equals("1/1")){
            if(drawbl.getConstantState().equals(getResources().getDrawable(R.drawable.iks).getConstantState())) {
                drawbl = getResources().getDrawable(R.drawable.nol);
                ((TextView)findViewById(R.id.textView_game2_info)).setText("Ходит второй игрок");
            }else {
                drawbl =  getResources().getDrawable(R.drawable.iks);
                ((TextView)findViewById(R.id.textView_game2_info)).setText("Ходит первый игрок");
            }

            Game2_on_of_button(true);
            //1-comp
        }else {


            Drawable.ConstantState b1; // 1,2,3\1,4,7\1,5,9
            Drawable.ConstantState b2; //1,2,3\2,5,8
            Drawable.ConstantState b3; //1,2,3\3,6,9
            Drawable.ConstantState b4; //1,4,7\4,5,6
            Drawable.ConstantState b5; //1,5,9\2,5,8\3,5,7\4,5,6
            Drawable.ConstantState b6; //3,6,9\4,5,6
            Drawable.ConstantState b7; //1,4,7\3,5,7\7,8,9
            Drawable.ConstantState b8; //2,5,8\7,8,9
            Drawable.ConstantState b9; //1,5,9\3,6,9\7,8,9


            b1 = ((Button) findViewById(R.id.button_tik1)).getBackground().getConstantState();
            b2 = ((Button) findViewById(R.id.button_tik2)).getBackground().getConstantState();
            b3 = ((Button) findViewById(R.id.button_tik3)).getBackground().getConstantState();
            b4 = ((Button) findViewById(R.id.button_tik4)).getBackground().getConstantState();
            b5 = ((Button) findViewById(R.id.button_tik5)).getBackground().getConstantState();
            b6 = ((Button) findViewById(R.id.button_tik6)).getBackground().getConstantState();
            b7 = ((Button) findViewById(R.id.button_tik7)).getBackground().getConstantState();
            b8 = ((Button) findViewById(R.id.button_tik8)).getBackground().getConstantState();
            b9 = ((Button) findViewById(R.id.button_tik9)).getBackground().getConstantState();

            if(b1.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik1)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }
            if(b2.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik2)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }
            if(b3.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik3)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }
            if(b4.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik4)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }
            if(b5.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik5)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }
            if(b6.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik6)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }
            if(b7.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik7)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }
            if(b8.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik8)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }
            if(b9.equals(dbitmap_fon.getConstantState())){
                tormoz(500);
                ((Button) findViewById(R.id.button_tik9)).setBackground(dbitmap_o);
                Game2_on_of_button(true);
                return;
            }

        }
    }
 //*************************************************************
//game2****

    public void Game3(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button_game3)).startAnimation(anim);
        ((LinearLayout)findViewById(R.id.menu_game)).setVisibility(View.GONE);
        ((RelativeLayout)findViewById(R.id.game3_loaut)).setVisibility(View.VISIBLE);
    }



    //*****




    public void tormoz(int msek){
        try {
            Thread.sleep(msek);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            // обрабатываем нажатие кнопки поиска
            Otvet_game1(((Button)findViewById(R.id.button_vopros_ok)));
            return true;
        }

        return false;
    }

    public void vivod_deneg(View view) {
        if(boblo>60000){
            Vivod_deneg v = new Vivod_deneg();
            v.execute();
        }else {
            Toast.makeText(getApplicationContext(), "Наберите минимально 1000 коп", Toast.LENGTH_LONG).show();
        }

    }





    public static void vopros(final String time_server){
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle("Вопросик")
                    .setMessage("На сервере значение больше чем мы пытаемся сохранить\nПерезаписать"+time_server
                    +" на "+String.valueOf(boblo)+" ?")
                    .setCancelable(true)
                    .setNegativeButton("Обновить на устройстве", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           boblo = Integer.valueOf(time_server);
                        }
                    })
                    .setPositiveButton("Да(подумай)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Send_server(); // сохраняем значение на сервере
                        }
                    });
            AlertDialog alert = b.create();
            alert.show();
        } // вопрос и звонок



    public void smena_texta(boolean on_of){
        if(on_of){
            if (mTimer != null) {
                mTimer.cancel();
            }
            mTimer = new Timer();
            mMyTimerTask = new MyTimerTask();
            mTimer.schedule(mMyTimerTask, 100, 1000);
        }else{
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boblo = boblo + 1;
                    time_natikalo.setText(String.valueOf(boblo) + " сек");
                    boblo_natikalo.setText("~"+String.valueOf(boblo/60)+" коп");

                    //смотрим скок бобла и показываем или скрываем кнопку снять болло
                    if(boblo>60000){
                        boblo_vivesti.setTextColor(Color.GREEN);
                    }else {
                        boblo_vivesti.setTextColor(Color.GRAY);
                    }

                    if(boblo>auto_save+300){ //каждые 5 минут автосохранение
                       ReadTime_proverka readTime_proverka = new ReadTime_proverka();
                        readTime_proverka.execute("http://i9027296.bget.ru/Kopilka/Read_time.php?key=" + user_p);
                        auto_save = auto_save+300;
                    }


                }
            });
        }
    }


    //**************************************************************
    @Override
    public void onPause() {
        run = save_read("run");
        if(isNetworkConnected()) {
            if (run != 0) {
                ReadTime_proverka readTime_proverka = new ReadTime_proverka();
                readTime_proverka.execute("http://i9027296.bget.ru/Kopilka/Read_time.php?key=" + user_p);
                smena_texta(false); // вырубаем таймер
            }
        }else{
            Toast.makeText(getApplicationContext(),"Нужно интернет соединение",Toast.LENGTH_LONG).show();
        }
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

        run = save_read("run");
        if(isNetworkConnected()) {
            if (run != 0) {
                //востанавливаем значение
                ReadTime readTime = new ReadTime();
                readTime.execute("http://i9027296.bget.ru/Kopilka/Read_time.php?key=" + user_p);


                if (stat == 0) {
                    ((LinearLayout) findViewById(R.id.statistika)).setVisibility(View.GONE);
                } else {
                    ((LinearLayout) findViewById(R.id.statistika)).setVisibility(View.VISIBLE);
                }
                user_p = String.valueOf(save_read("u_p"));
                ((TextView) findViewById(R.id.textView_phone)).setText(String.valueOf(save_read("akkaunt")));

                smena_texta(true);
            }
        }else{
            Toast.makeText(getApplicationContext(),"Нужно интернет соединение",Toast.LENGTH_LONG).show();
        }
    }

    //***************************************************************


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(Main.this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }


}
