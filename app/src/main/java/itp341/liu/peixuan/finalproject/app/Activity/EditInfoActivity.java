package itp341.liu.peixuan.finalproject.app.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import itp341.liu.peixuan.finalproject.app.Fragment.EditInfoFragment;
import itp341.liu.peixuan.finalproject.app.Fragment.UpdateStatusFragment;
import itp341.liu.peixuan.finalproject.app.R;

public class EditInfoActivity extends AppCompatActivity {
    public static final String ARG_URL = "EDIT_INFO_ACTIVITY.ARG_URL";
    //get UI references

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url  = getIntent().getExtras().getString(ARG_URL);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentList = fm.findFragmentById(R.id.fragment_container);
        if (fragmentList == null){
            fragmentList = EditInfoFragment.newInstance(url);
        }

        FragmentTransaction ftList = fm.beginTransaction();
        ftList.replace(R.id.fragment_container, fragmentList);
        ftList.commit();
    }
}
