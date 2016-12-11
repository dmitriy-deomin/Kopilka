package dmitriy.deomin.kopilka;

import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by 58627 on 20.12.2015.
 */
public class ReadTime_proverka extends AsyncTask<String, Void, String> {
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
            Toast.makeText(Main.context, s, Toast.LENGTH_LONG).show();
        } else {

            try{
                int i = Integer.parseInt(s);
                if (i>Main.boblo) { // если на сервере больше значение
                    Main.vopros(s);
                } else {
                    Main.Send_server(); // сохраняем значение на сервере
                }
            }catch(NumberFormatException ex){ // handle your exception

            }



        }


    }
}