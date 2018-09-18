package itp341.liu.peixuan.finalproject.app.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import itp341.liu.peixuan.finalproject.app.R;
import itp341.liu.peixuan.finalproject.app.Fragment.WelcomeFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate called");

        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentList = fm.findFragmentById(R.id.fragment_container);
        if (fragmentList == null){
            fragmentList = new WelcomeFragment();
        }

        FragmentTransaction ftList = fm.beginTransaction();
        ftList.replace(R.id.fragment_container, fragmentList);
        ftList.commit();
    }
}
