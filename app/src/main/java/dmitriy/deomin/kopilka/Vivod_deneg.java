package dmitriy.deomin.kopilka;

import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by 58627 on 19.12.2015.
 */
public class Vivod_deneg extends AsyncTask< Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  Log.w("TTT","добавляем текст");
        }

        @Override
        protected String doInBackground( Void... params) {

            String loginURL = "http://i9027296.bget.ru/Kopilka/Vivod.php";
            try {
                //делаем пост запрос
                Connection connection = Jsoup.connect(loginURL)
                        .data("time", String.valueOf(Main.boblo))
                        .data("vivod", "ok")
                        .data("user", String.valueOf(Main.save_read("u_p")))
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

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //или удачно очистим панель ввода
            if (s.length() < 3) {
                Toast.makeText(Main.context, "Запрос отправлен,сумма будет переведена на "+String.valueOf(Main.save_read("akkaunt")), Toast.LENGTH_LONG).show();
                Main.boblo = 0;
            } else {
                Toast.makeText(Main.context, "Ошибка попробуйте еще раз"+"\n("+s+")", Toast.LENGTH_LONG).show();
            }
        }
    }
