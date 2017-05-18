package vn.com.zinza.zinzamessenger.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by dell on 13/02/2017.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mImgBack;
    private EditText mEdtUsername;
    private EditText mEdtEmail;
    private EditText mEdtPassword;
    private EditText mEdtConfirm;
    private EditText mEdtDOB;
    private Button mBtnSignup;
    private ImageView mImgGetAvatar;

    private static final int REQUEST_CAMERA = 123;
    private static final int REQUEST_GALLERY = 231;
    private String url_avtar = "";

    private FirebaseAuth mAuth;
    private AlertDialog mDialog;
    private ProgressDialog mProgressDialog;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private StorageReference mStorageReference;

    private Calendar datetime = Calendar.getInstance();
    private DateFormat mDateFormat = DateFormat.getDateInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        setAuthInstance();
        setDbReference();
        initView();
        takeCamera();
        showDiaglogPicker();
    }

    private void initView() {
        mImgBack = (ImageView) findViewById(R.id.btnBackSignUp);
        mImgBack.setOnClickListener(this);
        mBtnSignup = (Button) findViewById(R.id.btnSignUp);
        mBtnSignup.setOnClickListener(this);
        mImgGetAvatar = (ImageView)findViewById(R.id.img_get_avatar);
        registerForContextMenu(mImgGetAvatar);
        mEdtUsername = (EditText) findViewById(R.id.edtUsernameSign);
        mEdtEmail = (EditText) findViewById(R.id.edtEmailSign);
        mEdtPassword = (EditText) findViewById(R.id.edtPassWrLogin);
        mEdtConfirm = (EditText) findViewById(R.id.edtConfirm);
        mEdtDOB = (EditText) findViewById(R.id.edtDob);
    }
    private void takeCamera(){
        mImgGetAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(v);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.takeAvatarCamera:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)) {
                            showAlert();
                        } else {
                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    }
                } else {
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentCamera, REQUEST_CAMERA);
                }
                break;
            case R.id.takeAvatarGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    uploadAvatar(data,"Uploading...",REQUEST_CAMERA);
                }
                break;
            case REQUEST_GALLERY:
                if(resultCode == RESULT_OK){
                    uploadAvatar(data,"Uploading...",REQUEST_GALLERY);
                }
                break;
        }
    }
    public void uploadAvatar(Intent data, final String title,int type) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        Uri uri = null;
        if(data.getData()!=null){
            uri = data.getData();
            if(type == REQUEST_CAMERA){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                mImgGetAvatar.setImageBitmap(photo);
            } else {
                mImgGetAvatar.setImageURI(uri);
            }
            Utils.NAME_FILE = getNameData(uri);
            StorageReference filePath = mStorageReference.child("FOLDER_AVATAR").child(Utils.NAME_FILE);
            Log.e("File path:", filePath + "--- " + uri.getLastPathSegment() + "");
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressDialog.dismiss();
                    url_avtar = taskSnapshot.getDownloadUrl().toString();
//                sendMessageAttach(url.toString(), type, AdapterMessageChat.SENDER_IMAGE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Utils.showToast(e.toString(), getApplicationContext());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    mProgressDialog.setMessage(title + " " + ((int) progress) + "%...");
                }
            });

        } else {
            mProgressDialog.dismiss();
            Utils.showToast("Can't choose image right now!!",this);
        }

    }
    private String getNameData(Uri uri) {
        String nameFile = "";
        if (uri.toString().startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    nameFile = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        return nameFile;
    }
    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA);
                    }
                });
        alertDialog.show();
    }

    private void showDiaglogPicker() {
        mEdtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignUpActivity.this, d, datetime.get(Calendar.YEAR), datetime.get(Calendar.MONTH), datetime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            datetime.set(Calendar.YEAR, year);
            datetime.set(Calendar.MONTH, month);
            datetime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mEdtDOB.setText(mDateFormat.format(datetime.getTime()));
        }
    };

    private void signIn() {
        final String username = mEdtUsername.getText().toString().trim();
        final String email = mEdtEmail.getText().toString().trim();
        final String password = mEdtPassword.getText().toString().trim();
        final String confirm = mEdtConfirm.getText().toString().trim();
        final String dateOfBirth = mEdtDOB.getText().toString().trim();
        if (username.equals("") || email.equals("") || dateOfBirth.equals("") || password.equals("") || confirm.equals("") || url_avtar.equals("")) {
            Utils.showToast("Not enough information", this);
        } else if (Utils.isValidEmail(email) == false) {
            Utils.showToast("Email is uncorrect", this);
        } else if (isCorrectPassword(password, confirm) == false) {
            Utils.showToast("Password is uncorrect", this);
        } else if(url_avtar.equals("")){
            Utils.showToast("You haven't choose avatar",this);
        } else {
            showProgress("Sign in", "Registering");
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        String id = task.getResult().getUser().getUid();
                        Utils.USER_ID = id;
                        User mUser = new User(id, username, email, password, url_avtar, dateOfBirth, "on", Utils.USER_TOKEN, Utils.createAt());
                        mReference.child(id).setValue(mUser);
                        Utils.showToast("Register success", getApplicationContext());
                        goToMainActivity();
                    } else {
                        showAlertDialog(task.getException().getMessage(), true);
                    }
                }
            });
            mProgressDialog.dismiss();
        }

    }

    private void showAlertDialog(String message, boolean isCancelable) {
        mDialog = Utils.buildAlertDialog(getString(R.string.title_alert), message, isCancelable, SignUpActivity.this);
        mDialog.show();
    }

    private void dismissAlertDialog() {
        mDialog.dismiss();
    }

    private void setDbReference() {
        mReference = mDatabase.getInstance().getReference(Utils.TBL_USERS);
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    private void setAuthInstance() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnBackSignUp:
                Intent intent2 = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.btnSignUp:
                signIn();
                break;
        }
    }

    private boolean isCorrectPassword(String pass, String confirm) {
        if (pass.equals(confirm)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_take_photo,menu);
    }

    private void showProgress(String title, String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

}