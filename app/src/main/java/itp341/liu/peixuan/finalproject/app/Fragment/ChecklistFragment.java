package itp341.liu.peixuan.finalproject.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import itp341.liu.peixuan.finalproject.app.Activity.DetailActivity;
import itp341.liu.peixuan.finalproject.app.R;


public class ChecklistFragment extends android.support.v4.app.Fragment {
    public static final String ARG_USERID = "ChecklistFragment.ARG_USERID";
    public static final String ARG_GARDENS_URL = "ChecklistFragment.ARG_GARDEN_URL";
    private final static String TAG = ChecklistFragment.class.getSimpleName();
    Button buttonContinue;

    public ChecklistFragment() {
        // Required empty public constructor
    }

    public static ChecklistFragment newInstance(String userId, String gardenUrl)
    {
        Bundle b = new Bundle();
        b.putString(ARG_USERID, userId);
        b.putString(ARG_GARDENS_URL, gardenUrl);
        Log.d(TAG, gardenUrl);

        ChecklistFragment newFragment = new ChecklistFragment();
        newFragment.setArguments(b);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_checklist, container, false);
        buttonContinue = (Button)v.findViewById(R.id.button_continue);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra(DetailActivity.ARG_USERID, getArguments().getString(ARG_USERID));
                i.putExtra(DetailActivity.EXTRA_GARDENS_URL, getArguments().getString(ARG_GARDENS_URL));
                startActivity(i);
                getActivity().finish();
            }
        });
        return v;
    }
}