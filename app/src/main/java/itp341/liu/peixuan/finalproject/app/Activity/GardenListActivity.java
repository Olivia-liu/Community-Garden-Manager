package itp341.liu.peixuan.finalproject.app.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import itp341.liu.peixuan.finalproject.app.Fragment.GardenListFragment;
import itp341.liu.peixuan.finalproject.app.Fragment.WelcomeFragment;
import itp341.liu.peixuan.finalproject.app.R;

public class GardenListActivity extends AppCompatActivity {
    public final static String EXTRA_CURRENT_USER_ID = GardenListActivity.class.getSimpleName() + "_CURRENT_USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        String userId = i.getStringExtra(EXTRA_CURRENT_USER_ID);

        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.fragment_container);
        if (f == null){
            f = GardenListFragment.newInstance(userId);
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, f);
        ft.commit();
    }
}