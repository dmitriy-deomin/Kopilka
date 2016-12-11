package dmitriy.deomin.kopilka;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
    }

    public void Open_gruppa(View view) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/kopilka_game"));
        startActivity(browseIntent);
    }

}
