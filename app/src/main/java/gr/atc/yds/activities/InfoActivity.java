package gr.atc.yds.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gr.atc.yds.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        setUIEventListeners();

    }

    //Initialize UI
    private void initUI(){

        //Set view
        setContentView(R.layout.activity_info);

        //Set title
        setTitle(getString(R.string.activityInfoTitle));

        //Make links clickable
        TextView infoTextView = (TextView) findViewById(R.id.activityInfo_infoTextView);
        infoTextView.setMovementMethod(LinkMovementMethod.getInstance());

        //Set size
        setSize();

    }

    //Initialize window size
    private void setSize(){

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .95), (int) (height * .4));
    }

    //Set UI event listeners
    private void setUIEventListeners (){

        //Close btn clicked
        ImageView closeBtn = (ImageView) findViewById(R.id.activityInfo_closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
