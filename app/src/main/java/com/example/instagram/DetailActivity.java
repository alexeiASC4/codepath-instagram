package com.example.instagram;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;

import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    TextView mUsername;
    TextView mCaption;
    TextView mDate;
    ImageView ivPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mUsername = findViewById(R.id.tvUsername);
        mCaption = findViewById(R.id.tvCaption);
        mDate = findViewById(R.id.tvDate);
        ivPhoto = findViewById(R.id.ivPhoto);


        Post p = getIntent().getParcelableExtra("post");

        mUsername.setText(p.getUser().getUsername());
        mCaption.setText(p.getDescription());
        mDate.setText(getRelativeTimeAgo(p.getCreatedAt()));

        Glide.with(this).load(p.getImage().getUrl()).into(ivPhoto);
    }

    public static String getRelativeTimeAgo(Date date) {
        String relativeDate = "";
        long dateMillis = date.getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        return relativeDate;
    }

}
