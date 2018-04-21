package sisir.cmu.edu.project4android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MostPlayedGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /*
         * The click listener will need a reference to this object, so that upon successfully retrieve data from Steam, it
         * can callback to this object with the resulting game information including pictures.  The "this" of the OnClick will be the OnClickListener, not
         * this MostPlayedGame.
         */
        final MostPlayedGame ma = this;

         /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);


        // Add a listener to the submit button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String steamID = ((EditText)findViewById(R.id.steam_id)).getText().toString();
                GetGame gp = new GetGame();
                gp.search(steamID, ma); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetGame object when the picture is ready.  This allows for passing back the data for updating the corresponding views
     */
    public void gameReady(Bitmap picture, String gc, String appid, String name, String pt) {

        // get view objects
        TextView searchView = (EditText)findViewById(R.id.steam_id);
        TextView steamIDView = findViewById(R.id.steam_id_display);
        ImageView pictureView = findViewById(R.id.picture);
        TextView game_cntView = findViewById(R.id.game_cnt);
        TextView appidView = findViewById(R.id.app_id);
        TextView nameView = findViewById(R.id.name);
        TextView playtimeView = findViewById(R.id.played_time);

        TextView steam_id_labelView = findViewById(R.id.steam_id_label);
        TextView game_cntlabelView = findViewById(R.id.game_cnt_label);
        TextView appidlabelView = findViewById(R.id.app_id_label);
        TextView namelabelView = findViewById(R.id.name_label);
        TextView playtimelabelView = findViewById(R.id.played_time_label);




        //update content
        if (picture != null) {
            pictureView.setImageBitmap(picture);
            pictureView.setVisibility(View.VISIBLE);
        } else {
            pictureView.setImageResource(R.mipmap.ic_launcher);
            pictureView.setVisibility(View.INVISIBLE);
        }

        steamIDView.setText(searchView.getText());
        game_cntView.setText(gc);
        steamIDView.setVisibility(View.VISIBLE);
        game_cntView.setVisibility(View.VISIBLE);
        steam_id_labelView.setVisibility(View.VISIBLE);
        game_cntlabelView.setVisibility(View.VISIBLE);

        if (gc.equals("0")) {
            appidView.setVisibility(View.INVISIBLE);
            nameView.setVisibility(View.INVISIBLE);
            playtimeView.setVisibility(View.INVISIBLE);
            appidlabelView.setVisibility(View.INVISIBLE);
            namelabelView.setVisibility(View.INVISIBLE);
            playtimelabelView.setVisibility(View.INVISIBLE);

        } else {
            appidView.setText(appid);
            nameView.setText(name);
            playtimeView.setText(pt);
            appidView.setVisibility(View.VISIBLE);
            nameView.setVisibility(View.VISIBLE);
            playtimeView.setVisibility(View.VISIBLE);
            appidlabelView.setVisibility(View.VISIBLE);
            namelabelView.setVisibility(View.VISIBLE);
            playtimelabelView.setVisibility(View.VISIBLE);


        }

        searchView.setText("");
        pictureView.invalidate();
    }


}
