package itp341.liu.peixuan.finalproject.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import itp341.liu.peixuan.finalproject.app.Activity.EditInfoActivity;
import itp341.liu.peixuan.finalproject.app.Activity.UpdateStatusActivity;
import itp341.liu.peixuan.finalproject.app.Constant.FirebaseRefs;
import itp341.liu.peixuan.finalproject.app.Model.Garden;
import itp341.liu.peixuan.finalproject.app.R;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


public class DetailFragment extends android.support.v4.app.Fragment {

    private final static String TAG = DetailFragment.class.getSimpleName();
    private static final String ARG_URL = "detailFragment.ARG_URL";
    private static final String ARG_GARDENS_URL = "detailFragment.ARG_GARDENS_URL";
    private static final String ARG_USERID = "detailFragment.ARG_USERID";

    private Button buttonUpdateStatus;
    private Button buttonUpdateInfo;

    private Button buttonJoin;
    private TextView textViewStatus;
    private TextView textViewStatusDetail;
    private TextView textViewLastUpdatedBy;
    private TextView textViewMembers;
    private TextView textViewDescription;
    private TextView textViewName;
    private TextView textTakeMeThere;
    private ImageButton imageButton;

    //database references
    private DatabaseReference dbRefGardens;
    private DatabaseReference dbRefGardenToEdit;

    private String curUrl;
    private Garden curGarden;
    private FirebaseUser user;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(String reference, String userId, String gardensRef)
    {
        Bundle b = new Bundle();
        b.putString(ARG_URL, reference);
        b.putString(ARG_USERID, userId);
        b.putString(ARG_GARDENS_URL, gardensRef);
        DetailFragment newFragment = new DetailFragment();
        newFragment.setArguments(b);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //get database reference paths
        dbRefGardens = database.getReference(FirebaseRefs.GARDENS);

        Bundle args = getArguments();

        //get reference to note to be edited (if it exists)
        if(args != null){
            String url = args.getString(ARG_URL);
            if(url!=null){
                dbRefGardenToEdit = database.getReferenceFromUrl(url);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        buttonUpdateStatus = (Button)v.findViewById(R.id.button_update_status);
        buttonUpdateInfo = (Button)v.findViewById(R.id.button_update_info);
        textTakeMeThere = (TextView)v.findViewById(R.id.textView_location);

        buttonJoin = (Button)v.findViewById(R.id.buttonJoin);

        textViewStatus = (TextView)v.findViewById(R.id.textView_status);
        textViewStatusDetail = (TextView)v.findViewById(R.id.textView_status_detail);
        textViewLastUpdatedBy = (TextView)v.findViewById(R.id.textView_last_updated_by);
        textViewMembers = (TextView) v.findViewById(R.id.textView_members);
        textViewDescription = (TextView) v.findViewById(R.id.textView_description);
        textViewName = (TextView)v.findViewById(R.id.textView_garden_name);
        imageButton = (ImageButton)v.findViewById(R.id.imageButton_detail_picture);

        //read selected note
        if(dbRefGardenToEdit != null){ //means editing existing note --> read existing note data
            user = FirebaseAuth.getInstance().getCurrentUser();
            readGarden();
        }

        else{ //means creating new one
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            Garden g = new Garden();
            g.setDescription(textViewDescription.getText().toString());
            g.setLocation(textTakeMeThere.getText().toString());
            g.setImageUrl("https://firebasestorage.googleapis.com/v0/b/finalprojectliupeixuan.appspot.com/o/images%2Fgarden.jpg?alt=media&token=64eab059-04e5-43f8-8788-80d55d3915c4");
            g.setLeader(user.getEmail());
            ArrayList<String> members = new ArrayList<>();
            members.add(user.getEmail());
            g.setMembers(members);
            g.setStatus(textViewStatus.getText().toString());
            g.setStatusDetail(textViewStatusDetail.getText().toString());
            g.setName(textViewName.getText().toString());
            g.setLocation(getResources().getString(R.string.location_default));
            g.setStatusLastUpdatedBy(getResources().getString(R.string.last_updated_default));
            ArrayList<String> users = new ArrayList<>();
            users.add(user.getUid());
            g.setAuthUsers(users);

            Log.d(TAG, getArguments().getString(ARG_GARDENS_URL));
            DatabaseReference dbRefGardens = database.getReferenceFromUrl(getArguments().getString(ARG_GARDENS_URL));
            DatabaseReference dbRefNewGarden = dbRefGardens.push(); //generate new unique id for new node
            dbRefNewGarden.setValue(g);
            dbRefGardenToEdit = dbRefNewGarden;
            curGarden = g;
            curUrl = dbRefNewGarden.toString();
            readGarden();
        }

        buttonUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAuthUser(user.getUid())) {buttonJoin.setVisibility(View.VISIBLE);}
                if(isAuthUser(user.getUid())) {
                    Intent i = new Intent(getActivity(), UpdateStatusActivity.class);
                    i.putExtra(UpdateStatusActivity.ARG_URL, curUrl);
                    startActivityForResult(i, 1);
                }
                else{
                    Toast.makeText(getContext(), getResources().getString(R.string.warning_join), Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAuthUser(user.getUid())) {buttonJoin.setVisibility(View.VISIBLE);}
                if(isAuthUser(user.getUid())) {
                    Intent i = new Intent(getActivity(), EditInfoActivity.class);
                    i.putExtra(EditInfoActivity.ARG_URL, curUrl);
                    startActivityForResult(i, 2);
                }
                else{
                    Toast.makeText(getContext(), getResources().getString(R.string.warning_join), Toast.LENGTH_LONG).show();
                }
            }
        });

        textTakeMeThere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = curGarden.getLocation();
                String parameters = "";
                if(location != null && !location.isEmpty() && !location.equals(getResources().getString(R.string.location_default))){
                    parameters = location.replace(' ', '+').replace(",", "%2");
                    Log.d(TAG, parameters);
                    Uri uri = Uri.parse(getResources().getString(R.string.map_search_url)+parameters);
                    Log.d(TAG, getResources().getString(R.string.map_search_url)+parameters);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getContext(), getResources().getString(R.string.warning_no_location), Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonJoin.getText().toString().equals(getResources().getString(R.string.Join))) {
                    Toast.makeText(getContext(), "Joined!", Toast.LENGTH_SHORT).show();
                    curGarden.getAuthUsers().add(user.getUid());
                    curGarden.getMembers().add(user.getEmail());
                    dbRefGardenToEdit.setValue(curGarden);
                    //Intent i = new Intent(getActivity(), GardenListActivity.class);
                    //startActivity(i);
                    buttonJoin.setText(getResources().getString(R.string.Leave));
                }
                else{
                    Toast.makeText(getContext(), "Left!", Toast.LENGTH_SHORT).show();
                    curGarden.getAuthUsers().remove(user.getUid());
                    curGarden.getMembers().remove(user.getEmail());
                    dbRefGardenToEdit.setValue(curGarden);
                    //Intent i = new Intent(getActivity(), GardenListActivity.class);
                    //startActivity(i);
                    buttonJoin.setText(getResources().getString(R.string.Join));
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Intent i = new Intent();
            readGarden();
    }

    private boolean isAuthUser(String id){
        if(dbRefGardenToEdit==null){
            return true;
        }

        else{
            for(int i = 0; i < curGarden.getAuthUsers().size(); i++){
                if(curGarden.getAuthUsers().get(i).equals(id)){
                    return true;
                }
            }

        }
        return false;
    }

    private void readGarden(){
        //read selected note
        if(dbRefGardenToEdit != null){ //means editing existing note --> read existing note data
            //this is how to read ONCE from a specific (ie. NOT CONSTANTLY UPDATED)
            curUrl = dbRefGardenToEdit.toString();
            dbRefGardenToEdit.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //this method is called once a read is performed
                    //dataSnapShot represents all the data you are trying to read
                    curGarden = dataSnapshot.getValue(Garden.class);

                    String status = curGarden.getStatus();

                    if(!status.equals("Good")){
                        textViewStatus.setTextColor(Color.parseColor("#ff0000"));
                    }
                    else{
                        textViewStatus.setTextColor(Color.parseColor("#295b05"));
                    }

                    Uri downloadUrl = Uri.parse(curGarden.getImageUrl());
                    Picasso.with(getActivity()).load(downloadUrl).into(imageButton);
                    textViewStatus.setText(curGarden.getStatus());
                    textViewStatusDetail.setText(curGarden.getStatusDetail());
                    textViewLastUpdatedBy.setText(curGarden.getStatusLastUpdatedBy());
                    textViewName.setText(curGarden.getName());
                    String members = "";
                    for(int i = 0; i < curGarden.getMembers().size(); i++){
                        members += curGarden.getMembers().get(i) + " ";
                    }
                    if(members.isEmpty()){
                        members = getResources().getString(R.string.member_default);
                    }
                    textViewMembers.setText(members);
                    textViewDescription.setText(curGarden.getDescription());
                    textTakeMeThere.setText(curGarden.getLocation());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
