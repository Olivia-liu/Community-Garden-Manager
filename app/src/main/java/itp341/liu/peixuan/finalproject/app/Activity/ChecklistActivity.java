package itp341.liu.peixuan.finalproject.app.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import itp341.liu.peixuan.finalproject.app.Fragment.ChecklistFragment;
import itp341.liu.peixuan.finalproject.app.R;

public class ChecklistActivity extends AppCompatActivity {
    public static final String ARG_USER_ID = "ChecklistActivity.ARG_USER_ID";
    public static final String ARG_GARDENS_URL = "ChecklistActivity.ARG_GARDENS_REFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userId = getIntent().getExtras().getString(ARG_USER_ID);
        //String userId = i.getStringExtra(ARG_USER_ID);
        Log.d(ARG_USER_ID, userId);

        String gardensUrl = getIntent().getExtras().getString(ARG_GARDENS_URL);
        Log.d(ARG_GARDENS_URL, gardensUrl);

        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.fragment_container);
        if (f == null){
            f = ChecklistFragment.newInstance(userId, gardensUrl);
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, f);
        ft.commit();
    }
}