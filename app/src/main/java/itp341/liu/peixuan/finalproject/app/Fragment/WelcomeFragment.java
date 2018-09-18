package itp341.liu.peixuan.finalproject.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import itp341.liu.peixuan.finalproject.app.Activity.GardenListActivity;
import itp341.liu.peixuan.finalproject.app.Activity.MainActivity;
import itp341.liu.peixuan.finalproject.app.R;


public class WelcomeFragment extends Fragment {

    private final static String TAG = WelcomeFragment.class.getSimpleName();
    //private ImageButton imageButtonWelcome;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonSignup;
    private RelativeLayout rl;
    private FirebaseAuth mAuth;


    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        //imageButtonWelcome = (ImageButton)v.findViewById(R.id.imageButton_welcome);
        editTextEmail = (EditText)v.findViewById(R.id.editText_email);
        editTextPassword = (EditText)v.findViewById(R.id.editText_password);
        buttonLogin = (Button)v.findViewById(R.id.button_Login);
        buttonSignup = (Button)v.findViewById(R.id.button_Signup);
        rl = (RelativeLayout) v.findViewById(R.id.loadingPanel);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if(password!=null && password.length() != 0 &&
                        email != null && email.length() != 0){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                        rl.setVisibility(View.GONE);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        rl.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }
                                }
                            });
                }
                else{
                    rl.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Invalid input",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if(password!=null && password.length() != 0 &&
                        email != null && email.length() != 0){
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                        rl.setVisibility(View.GONE);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        rl.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), getResources().getString(R.string.warning_fail),
                                                Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }


                                }
                            });
                }
                else{
                    rl.setVisibility(View.GONE);
                    Toast.makeText(getContext(), getResources().getString(R.string.warning_invalid),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d(TAG, "Current user: " + currentUser.getEmail());
            //updateUI(currentUser);
        }

    }

    //onDetach
    @Override
    public void onDetach(){
        super.onDetach();
        mAuth.signOut();
        Log.d(TAG, "Signed out");
        updateUI(null);
    }

    public void updateUI(FirebaseUser currentUser){
        if(currentUser == null){
            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);
        }
        else {
            Intent i = new Intent(getActivity(), GardenListActivity.class);
            i.putExtra(GardenListActivity.EXTRA_CURRENT_USER_ID, currentUser.getUid());
            startActivity(i);
        }
    }
}
