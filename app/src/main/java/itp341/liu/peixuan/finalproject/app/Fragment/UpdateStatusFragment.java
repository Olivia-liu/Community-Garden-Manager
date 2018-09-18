package itp341.liu.peixuan.finalproject.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import itp341.liu.peixuan.finalproject.app.Model.Garden;
import itp341.liu.peixuan.finalproject.app.R;


public class UpdateStatusFragment extends android.support.v4.app.Fragment {
    public final static String EXTRA_UPDATE_STATUS_ANSWER = "UpdateStatusFragment.EXTRA_UPDATE_STATUS_ANSWER";
    public static final String ARG_URL = "UpdateStatusFragment.ARG_GARDEN_URL";
    private final static String TAG = UpdateStatusFragment.class.getSimpleName();
    private Spinner spinnerStatus;
    private EditText editTextStatusDetail;
    private Button buttonSave;
    private Button buttonCancel;
    private DatabaseReference dbRefGardenToEdit;
    private Garden curGarden;

    public UpdateStatusFragment() {
        // Required empty public constructor
    }

    public static UpdateStatusFragment newInstance(String url)
    {
        Bundle b = new Bundle();
        b.putString(ARG_URL, url);

        UpdateStatusFragment newFragment = new UpdateStatusFragment();
        newFragment.setArguments(b);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Bundle args = getArguments();
        //get reference to note to be edited (if it exists)
        if(args != null){
            String url = args.getString(ARG_URL);
            if(url!=null){
                Log.d(TAG, url);
                dbRefGardenToEdit = database.getReferenceFromUrl(url);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_update_status, container, false);
        buttonSave = (Button) v.findViewById(R.id.button_save_status);
        buttonCancel = (Button) v.findViewById(R.id.button_cancel_status);
        spinnerStatus = (Spinner)v.findViewById(R.id.spinner_status);
        editTextStatusDetail = (EditText)v.findViewById(R.id.editText_status_detail);

        //read selected note
        if(dbRefGardenToEdit != null){ //means editing existing note --> read existing note data
            //this is how to read ONCE from a specific (ie. NOT CONSTANTLY UPDATED)
            dbRefGardenToEdit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //this method is called once a read is performed
                    //dataSnapShot represents all the data you are trying to read
                    curGarden = dataSnapshot.getValue(Garden.class);
                    editTextStatusDetail.setText(curGarden.getStatusDetail());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();    //dummy
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                curGarden.setStatus(spinnerStatus.getSelectedItem().toString());
                curGarden.setStatusLastUpdatedBy(user.getEmail());
                curGarden.setStatusDetail(editTextStatusDetail.getText().toString());

                dbRefGardenToEdit.setValue(curGarden);

                i.putExtra(EXTRA_UPDATE_STATUS_ANSWER, "save");
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(EXTRA_UPDATE_STATUS_ANSWER, "cancel");
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
            }
        });

        return v;
    }

}
