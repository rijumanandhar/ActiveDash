package com.example.activedash.loginregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.activedash.FirebaseQueryLiveData;
import com.example.activedash.main.MainActivity;
import com.example.activedash.R;
import com.example.activedash.Repository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private final String TAG = RegisterFragment.class.getSimpleName();

    private Button  signUpBtn;
    private EditText nameText, usernameText,emailText, passwordText, cPasswordText, dobText;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    public final int GALLERY_INT = 2;

    private StorageReference mStorage;

    Repository repository;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();

        repository = new Repository();

        mStorage = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        signUpBtn = rootView.findViewById(R.id.signUpButton);

        nameText = rootView.findViewById(R.id.nameText);
        usernameText = rootView.findViewById(R.id.userNameText);
        emailText = rootView.findViewById(R.id.emailText);
        passwordText =  rootView.findViewById(R.id.passwordText);
        cPasswordText = rootView.findViewById(R.id.cPasswordText);
        dobText = rootView.findViewById(R.id.dobText);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUp();
            }
        });

        return rootView;
    }



    public void startSignUp(){
        final String name = nameText.getText().toString().trim();
        final String username = usernameText.getText().toString().trim();
        final String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String cPassword = cPasswordText.getText().toString().trim();
        final String dob = dobText.getText().toString().trim();

        //validation
        if (validate(name,username,email,password,cPassword,dob)){
            Log.d("sad","validate is true");
            progressDialog.setMessage("Signing Up");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    String user_id = mAuth.getCurrentUser().getUid();
                    Log.d(TAG,"user_id "+user_id);
                    repository.insertUserData(user_id,name,email,username,dob,"default",0,1,0,0,0,150);
                    progressDialog.dismiss();

                    Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }
    }

    public boolean validate (String name, String username, String email, String password, String cpassword, String dob){
        if (!name.equals("")&& !username.equals("")&& !password.equals("")&& !email.equals("")&& !cpassword.equals("")&& !dob.equals("")){
            if (password.equals(cpassword) ){
                return true;
            }else{
                Toast.makeText(getActivity(),"Passwords don't match",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getActivity(),"Empty Fields",Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
