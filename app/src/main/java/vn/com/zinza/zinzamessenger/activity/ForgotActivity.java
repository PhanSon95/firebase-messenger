package vn.com.zinza.zinzamessenger.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Utils;

public class ForgotActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DatabaseReference mReference;
    private EditText edtEmail, edtPass, edtconfirm;
    private Button btnChangePass,btnVerify;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        setInstance();
        initView();
        changePass();
        verifyEmail();

    }

    private void setInstance() {
        mReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbarFr);
        edtEmail = (EditText) findViewById(R.id.txtForgotEmail);
        edtPass = (EditText) findViewById(R.id.txtForgotPass);
        edtconfirm = (EditText) findViewById(R.id.txtForgotConfirm);
        btnChangePass = (Button) findViewById(R.id.btnChangePass);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void verifyEmail() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().equals("")) {
                    mProgressDialog.dismiss();
                    edtEmail.setError("Email is required");
                    edtEmail.setFocusableInTouchMode(true);
                    edtEmail.requestFocus();
                    edtEmail.setFocusable(true);
                } else {
                    mReference.child(Utils.TBL_USERS).orderByKey().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean isExist = false;
                            if(dataSnapshot.exists()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    User user = ds.getValue(User.class);
                                    if (user.getmEmail().contains(edtEmail.getText().toString())) {
                                        isExist = true;
                                        Log.e(ds.getKey(),String.valueOf(user.getmEmail().equals(edtEmail.getText().toString())));
                                    }
                                }
                            } else {
                                isExist = false;
                            }
                            if (isExist) {
                                Utils.showToast("Email correct", getApplicationContext());
                            } else {
                                edtEmail.setError("Email is incorrect!");
                                edtEmail.setFocusableInTouchMode(true);
                                edtEmail.requestFocus();
                                edtEmail.setFocusable(true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

    }

    private void changePass() {
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("Changing password..", "Please wait");
                if (edtPass.getText().equals("") || edtPass.getText().equals("") || edtconfirm.getText().equals("") || (edtEmail.getText().equals("") && edtPass.getText().equals("") && edtconfirm.getText().equals(""))) {
                    mProgressDialog.dismiss();
                    edtEmail.setFocusableInTouchMode(true);
                    edtEmail.requestFocus();
                    edtEmail.setFocusable(true);
                    edtEmail.setError("Email is required");
                } else if (edtEmail.getText().equals("")) {
                    mProgressDialog.dismiss();
                    edtEmail.setError("Email is required");
                    edtEmail.setFocusableInTouchMode(true);
                    edtEmail.requestFocus();
                    edtEmail.setFocusable(true);
                } else if (edtPass.getText().equals("")) {
                    mProgressDialog.dismiss();
                    edtPass.setError("Password is required");
                    edtPass.setFocusableInTouchMode(true);
                    edtPass.requestFocus();
                    edtPass.setFocusable(true);
                } else if (edtconfirm.getText().equals("")) {
                    mProgressDialog.dismiss();
                    edtconfirm.setError("Confirm is required");
                    edtconfirm.setFocusableInTouchMode(true);
                    edtconfirm.requestFocus();
                    edtconfirm.setFocusable(true);
                } else {

                }

            }
        });
    }

    private void showProgress(String title, String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
}
