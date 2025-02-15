package com.example.createreview;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        findViewById(R.id.button_image).setOnClickListener(view -> replaceFragment(new ImageFragment()));
        findViewById(R.id.button_video).setOnClickListener(view -> replaceFragment(new VideoFragment()));
        findViewById(R.id.button_note).setOnClickListener(view -> replaceFragment(new NoteFragment()));
        findViewById(R.id.button_review).setOnClickListener(view -> replaceFragment(new ReviewFragment()));
    }

    private void replaceFragment(ImageFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceFragment(VideoFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceFragment(NoteFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void replaceFragment(ReviewFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
