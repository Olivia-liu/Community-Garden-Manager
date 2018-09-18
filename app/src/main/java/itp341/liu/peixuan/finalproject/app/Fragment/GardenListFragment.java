package itp341.liu.peixuan.finalproject.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import itp341.liu.peixuan.finalproject.app.Activity.ChecklistActivity;
import itp341.liu.peixuan.finalproject.app.Activity.DetailActivity;
import itp341.liu.peixuan.finalproject.app.Constant.FirebaseRefs;
import itp341.liu.peixuan.finalproject.app.Model.Garden;
import itp341.liu.peixuan.finalproject.app.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseListAdapter;

public class GardenListFragment extends Fragment {
    public static final String ARG_USERID = "GardenListFragment.ARG_USERID";
    private final static String TAG = GardenListFragment.class.getSimpleName();
    private ListView list;
    private Button buttonNewGarden;
    private RadioGroup radioGroup;
    private boolean onlyShowMine = false;
    private FirebaseUser user;

    //database references
    private DatabaseReference dbRefGardens;
    private FirebaseListAdapter mAdapter;

    public static GardenListFragment newInstance(String userId)
    {
        Bundle b = new Bundle();
        Log.d(ARG_USERID, userId);
        b.putString(ARG_USERID, userId);

        GardenListFragment newFragment = new GardenListFragment();
        newFragment.setArguments(b);
        return newFragment;
    }

    public GardenListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        //get database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRefGardens = database.getReference(FirebaseRefs.GARDENS);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_garden_list, container, false);

        Log.d(TAG, "onCreateView called");
        list = (ListView) v.findViewById(R.id.gardenFragmentList);
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup_show);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId)
                {
                    case R.id.radioButton_only_show:
                        onlyShowMine = true;
                        mAdapter = new GardensAdapter(getActivity(), Garden.class, R.layout.garden_item, dbRefGardens, onlyShowMine);
                        list.setAdapter(mAdapter);
                        break;
                    case R.id.radioButton_show_all:
                        onlyShowMine = false;
                        mAdapter = new GardensAdapter(getActivity(), Garden.class, R.layout.garden_item, dbRefGardens, onlyShowMine);
                        list.setAdapter(mAdapter);
                        break;
                }
            }
        });

        //instantiate adapter
        //set adapter for listview
        mAdapter = new GardensAdapter(getActivity(), Garden.class, R.layout.garden_item, dbRefGardens, onlyShowMine);
        list.setAdapter(mAdapter);
        //list item click listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DatabaseReference dbRefClickedGarden = mAdapter.getRef(position);
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra(DetailActivity.EXTRA_URL, dbRefClickedGarden.toString());
                i.putExtra(DetailActivity.ARG_USERID, getArguments().getString(ARG_USERID));
                startActivity(i);
            }
        });

        buttonNewGarden = (Button) v.findViewById(R.id.buttonNewGarden);
        buttonNewGarden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ChecklistActivity.class);
                //Log.d(TAG, "userid: " + getArguments().getString(ARG_USERID));
                i.putExtra(ChecklistActivity.ARG_USER_ID, getArguments().getString(ARG_USERID));
                //Log.d(TAG, "userid: " + getArguments().getString(ARG_USERID));
                //Log.d(TAG, "dbrefgardensurl: " + dbRefGardens.toString());
                i.putExtra(ChecklistActivity.ARG_GARDENS_URL, dbRefGardens.toString());
                //Log.d(TAG, "dbrefgardensurl: " + dbRefGardens.toString());
                startActivity(i);
            }
        });

        return v;
    }

    public void onDetach() {
        super.onDetach();
        mAdapter.cleanup();
    }

    //create custom FirebaseListAdapter
    //purpose: to act as the CONTROLLER (MVC) to load models from Firebase into ListView
    public class GardensAdapter extends FirebaseListAdapter<Garden>{
        //default constructor
        private boolean onlyShowMine;
        public GardensAdapter(Activity activity, Class<Garden> modelClass, int modelLayout, DatabaseReference ref, boolean onlySHowMine) {
            super(activity, modelClass, modelLayout, ref);
            this.onlyShowMine = onlySHowMine;
        }

        @Override
        protected void populateView(View v, Garden model, int position) {
            TextView textName = (TextView) v.findViewById(R.id.listGardenName);
            TextView textLeader = (TextView) v.findViewById(R.id.listGardenLeader);
            TextView textLocation = (TextView) v.findViewById(R.id.listGardenLocation);
            CardView cv = (CardView)v.findViewById(R.id.card_view);

            //show all gardens
            if(!onlyShowMine){
                cv.setVisibility(View.VISIBLE);
                textName.setText(model.getName());
                textLeader.setText(model.getLeader());
                textLocation.setText(model.getLocation());
                if(model.getLeader().equals(user.getEmail())){
                    textName.setBackgroundColor(getResources().getColor(R.color.holo_green));
                }
                else{
                    boolean isAMember = false;
                    for(int i = 0; i < model.getMembers().size(); i++){
                        if(model.getMembers().get(i).equals(user.getEmail())){
                            isAMember = true;
                            break;
                        }
                    }
                    if(isAMember) {
                        textName.setBackgroundColor(getResources().getColor(R.color.holo_green));
                    }
                    else{
                        textName.setBackgroundColor(getResources().getColor(R.color.super_light_green));
                    }
                }
            }

            //only show mine gardens
            else{
                if(model.getLeader().equals(user.getEmail())){
                    cv.setVisibility(View.VISIBLE);
                    textName.setText(model.getName());
                    textLeader.setText(model.getLeader());
                    textName.setBackgroundColor(getResources().getColor(R.color.holo_green));
                    textLocation.setText(model.getLocation());
                }
                else{
                    boolean isAMember = false;
                    for(int i = 0; i < model.getMembers().size(); i++){
                        if(model.getMembers().get(i).equals(user.getEmail())){
                            isAMember = true;
                            break;
                        }
                    }

                    if(isAMember) {
                        cv.setVisibility(View.VISIBLE);
                        textName.setBackgroundColor(getResources().getColor(R.color.holo_green));
                        textName.setText(model.getName());
                        textLeader.setText(model.getLeader());
                        textLocation.setText(model.getLocation());
                    }

                    else{
                        cv.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
