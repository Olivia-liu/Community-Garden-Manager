package itp341.liu.peixuan.finalproject.app.Activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import itp341.liu.peixuan.finalproject.app.Fragment.DetailFragment;
import itp341.liu.peixuan.finalproject.app.R;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "detailActivity.EXTRA_URL";
    public static final String ARG_USERID = "detailActivity.USERID";
    public static final String EXTRA_GARDENS_URL = "detailActivity.EXTRA_GARDENS_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String reference = getIntent().getExtras().getString(EXTRA_URL);
        String userId = getIntent().getExtras().getString(ARG_USERID);
        String gardensRef = getIntent().getExtras().getString(EXTRA_GARDENS_URL);

        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.fragment_container);
        if (f == null){
            f = DetailFragment.newInstance(reference, userId, gardensRef);
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, f);
        ft.commit();
    }
}
