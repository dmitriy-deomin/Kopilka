package dmitriy.deomin.kopilka;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by 58627 on 20.12.2015.
 */
public class AddTime extends AsyncTask< Void, Void, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //  Log.w("TTT","добавляем текст");
    }

    @Override
    protected String doInBackground( Void... params) {

        String loginURL = "http://i9027296.bget.ru/Kopilka/Kopilka.php";
        try {
            //делаем пост запрос
            Connection connection = Jsoup.connect(loginURL)
                    .data("time", String.valueOf(Main.boblo))
                    .data("user",Main.user_p)
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
            // Toast.makeText(context, "Обновили копилку на сервере", Toast.LENGTH_LONG).show();
        } else {
            // Toast.makeText(context, "Ошибка попробуйте еще раз"+"\n("+s+")", Toast.LENGTH_LONG).show();
        }
    }
}
