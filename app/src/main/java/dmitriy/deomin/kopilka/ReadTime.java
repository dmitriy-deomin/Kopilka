package dmitriy.deomin.kopilka;

import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by 58627 on 20.12.2015.
 */
public class ReadTime extends AsyncTask<String, Void, String> {
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

        if(!doc.select("time").text().isEmpty()){
            Main.boblo = Integer.valueOf(doc.select("time").text());
            Main.auto_save = Main.boblo; // автосохранение
        }else {
            return "Ошибка, если вы только зарегистрировались то нечего страшного )";
        }



        return doc.select("time").text();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s.length()>5){
            Toast.makeText(Main.context, s, Toast.LENGTH_LONG).show();
        }else {
            // Toast.makeText(getApplicationContext(), "времени натикало: " + s + " секунд", Toast.LENGTH_LONG).show();
        }
    }
}