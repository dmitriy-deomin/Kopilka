package dmitriy.deomin.kopilka;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Registacia extends AppCompatActivity {

    static Context context;
    AddData addData;
    User_proverka user_proverka;
    Update_time update_time;
    String user_phone;
    String user_pasvord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registacia);
        context = getApplicationContext();
    }

    public void Send_data(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ((Button)findViewById(R.id.button3)).startAnimation(anim);

     if(isNetworkConnected()) {
         user_phone = ((EditText) findViewById(R.id.editText_phone)).getText().toString();
         user_pasvord = ((EditText) findViewById(R.id.editText_pasvord)).getText().toString();
         if(user_phone.length()<11){
             Toast.makeText(getApplicationContext(),"Введите номер Qiwi кощелька(11 цифр,номер телефона)",Toast.LENGTH_LONG).show();
         }else{
             if(user_pasvord.length()<2){
                 Toast.makeText(getApplicationContext(),"Введите пароль(любой)",Toast.LENGTH_LONG).show();
             }else{
                 user_proverka = new User_proverka();
                 user_proverka.execute("http://i9027296.bget.ru/Kopilka/Read_user.php?key="+user_phone+user_pasvord);
             }

         }
      }else{
      Toast.makeText(getApplicationContext(),"Извените но нужно интернет соединение",Toast.LENGTH_LONG).show();
     }
    }


    class User_proverka extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Log.w("TTT", "загружаем текст");
        }

        @Override
        protected String doInBackground(String... params) {
            Document doc = null;
            try {
                doc = Jsoup
                        .connect(params[0].toString())
                        .timeout(0)
                        .maxBodySize(0)
                        .get();


            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!doc.select("user").isEmpty()) {
                return doc.select("user").text();
            } else {
                return "Ошибка проверки на сервере";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Ошибка")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            if (s.equals("1")) {
                Toast.makeText(getApplicationContext(), "Такой аккаунт существует , входим...", Toast.LENGTH_LONG).show();
                Main.save_value("run", 100);
                Main.save_value("u_p", string_to_long(user_phone+user_pasvord));
                Main.save_value("akkaunt", string_to_long(user_phone));
                update_time = new Update_time();
                update_time.execute("http://i9027296.bget.ru/Kopilka/Read_time.php?key=" + user_phone+user_pasvord);
            }
            if (s.equals("0")) {
                addData = new AddData();
                addData.execute();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }


    class AddData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  Log.w("TTT","добавляем текст");
        }

        @Override
        protected String doInBackground(Void... params) {

            String loginURL = "http://i9027296.bget.ru/Kopilka/Registracia.php";
            try {

                //делаем пост запрос
                Connection connection = Jsoup.connect(loginURL)
                        .data("user_p", user_phone+user_pasvord)
                        .method(Connection.Method.POST)
                        .followRedirects(true);
                Connection.Response response = connection.execute();


                if (response.statusCode() == 200) {
                    return "";
                } else {
                    return "Ошибка: " + response.statusCode();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return params[0].toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //или удачно очистим панель ввода
            if (s.length() < 3) {
                Toast.makeText(getApplicationContext(), "Хорошо", Toast.LENGTH_LONG).show();
                Main.save_value("run", 100);
                Main.save_value("u_p", string_to_long(user_phone+user_pasvord));
                Main.save_value("akkaunt", string_to_long(user_phone));
                pizdec();
            } else {
                Toast.makeText(getApplicationContext(), "Ошибка попробуйте еще раз", Toast.LENGTH_LONG).show();
            }
        }
    }

    class Update_time extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Log.w("TTT", "загружаем текст");
        }

        @Override
        protected String doInBackground(String... params) {
            Document doc = null;
            try {
                doc = Jsoup
                        .connect(params[0].toString())
                        .timeout(0)
                        .maxBodySize(0)
                        .get();


            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!doc.select("time").isEmpty()) {
                return doc.select("time").text();
            } else {
                return "Ошибка проверки на сервере";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("Ошибка")) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            } else {
                Main.boblo =  string_to_long(s);
                pizdec();
            }


        }
    }


    public long string_to_long(String s){
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
           return 0;
        }

    }

    private void pizdec(){
        this.finish();
    }
}
