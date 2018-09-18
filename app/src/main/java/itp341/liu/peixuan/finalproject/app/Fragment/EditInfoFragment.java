//photo select & upload reference: https://theengineerscafe.com/firebase-storage-android-tutorial/
package itp341.liu.peixuan.finalproject.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import itp341.liu.peixuan.finalproject.app.Model.Garden;
import itp341.liu.peixuan.finalproject.app.R;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class EditInfoFragment extends android.support.v4.app.Fragment {
    private static final int SELECT_PHOTO = 1;
    public final static String EXTRA_EDIT_INFO_ANSWER = "EditInfoFragment.EXTRA_EDIT_INFO_ANSWER";
    public static final String ARG_URL = "EditInfoFragment.ARG_GARDEN_URL";
    private final static String TAG = WelcomeFragment.class.getSimpleName();
    private DatabaseReference dbRefGardenToEdit;
    private Garden curGarden;
    private StorageReference mStorageRef, mImageRef;
    Uri selectedImage;
    UploadTask uploadTask;
    ProgressDialog progressDialog;

    Button buttonSave;
    Button buttonCancel;
    ImageButton imageButtonPicture;
    EditText editTextName;
    //EditText editTextMembers;
    EditText editTextDescription;
    EditText editTextLocation;

    public EditInfoFragment() {
        // Required empty public constructor
    }

    public static EditInfoFragment newInstance(String url)
    {
        Bundle b = new Bundle();
        b.putString(ARG_URL, url);

        EditInfoFragment newFragment = new EditInfoFragment();
        newFragment.setArguments(b);
        return newFragment;
    }

    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    uploadImage(getView());
                }
        }
    }

    public void uploadImage(View view) {
        //create reference to images folder and assing a name to the file that will be uploaded
        mImageRef = mStorageRef.child("images/" + selectedImage.getLastPathSegment());
        //creating and showing progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(1);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        //starting upload
        uploadTask = mImageRef.putFile(selectedImage);
        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (1.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //sets and increments value of progressbar
                progressDialog.incrementProgressBy((int) progress);
            }
        });
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), "Error in uploading!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                curGarden.setImageUrl(downloadUrl.toString());
                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                //showing the uploaded image in ImageView using the download url
                Picasso.with(getActivity()).load(downloadUrl).into(imageButtonPicture);
            }
        });
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

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_edit_info, container, false);
        editTextName = (EditText)v.findViewById(R.id.editText_edit_garden_name);
        editTextLocation = (EditText)v.findViewById(R.id.editText_edit_location);
        editTextDescription = (EditText)v.findViewById(R.id.editText_edit_description);
        //editTextMembers = (EditText)v.findViewById(R.id.editText_edit_members);
        buttonSave = (Button) v.findViewById(R.id.button_save_info);
        buttonCancel = (Button) v.findViewById(R.id.button_cancel_info);
        imageButtonPicture = (ImageButton)v.findViewById(R.id.imageButton_edit_picture);


        //read selected note
        if(dbRefGardenToEdit != null){ //means editing existing note --> read existing note data
            //this is how to read ONCE from a specific (ie. NOT CONSTANTLY UPDATED)
            dbRefGardenToEdit.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //this method is called once a read is performed
                    //dataSnapShot represents all the data you are trying to read
                    curGarden = dataSnapshot.getValue(Garden.class);
                    editTextName.setText(curGarden.getName());
                    editTextLocation.setText(curGarden.getLocation());
                    editTextDescription.setText(curGarden.getDescription());
                    Uri downloadUrl = Uri.parse(curGarden.getImageUrl());
                    Picasso.with(getActivity()).load(downloadUrl).into(imageButtonPicture);
                    //editTextMembers.setText(curGarden.getMembers());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        imageButtonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                curGarden.setLocation(editTextLocation.getText().toString());
                curGarden.setDescription(editTextDescription.getText().toString());
                //curGarden.setMembers(editTextMembers.getText().toString());
                curGarden.setName(editTextName.getText().toString());

                dbRefGardenToEdit.setValue(curGarden);
                Intent i = new Intent();    //dummy
                i.putExtra(EXTRA_EDIT_INFO_ANSWER, "save");
                getActivity().setResult(RESULT_OK, i);
                getActivity().finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(EXTRA_EDIT_INFO_ANSWER, "cancel");
                getActivity().setResult(RESULT_OK, i);
                getActivity().finish();
            }
        });

        return v;
    }

}
