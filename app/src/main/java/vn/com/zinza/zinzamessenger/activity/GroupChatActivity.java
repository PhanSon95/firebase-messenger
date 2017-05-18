package vn.com.zinza.zinzamessenger.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.adapter.AdapterCreateGroup;
import vn.com.zinza.zinzamessenger.model.BodyRequestGCM;
import vn.com.zinza.zinzamessenger.model.Data;
import vn.com.zinza.zinzamessenger.model.Friend;
import vn.com.zinza.zinzamessenger.model.Group;
import vn.com.zinza.zinzamessenger.model.Notification;
import vn.com.zinza.zinzamessenger.model.ResultRequestFCM;
import vn.com.zinza.zinzamessenger.model.ResultRequestGCM;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.service.FCMService;
import vn.com.zinza.zinzamessenger.utils.Utils;

public class GroupChatActivity extends AppCompatActivity implements ListView.OnItemClickListener,View.OnClickListener{
    private ListView mLstUserToCreaTe;
    private List<User> mLstUser;
    private AdapterCreateGroup mAdapterCreateGroup;

    private Button mBtnCreateGroup;
    private EditText mEdtMembers;
    private EditText mEdtNameGroup;
    private EditText mEdtDescribe;
    private ImageView mImgTakeAvatar;

    private String result = "";
    private DatabaseReference mReference;
    private StorageReference mStorageReference;
    private List<String> mLstUserKeys;

    private ProgressDialog mProgressDialog;

    private static final int REQUEST_CAMERA = 123;
    private static final int REQUEST_GALLERY = 231;
    private String url_avtar = "";
    private List<String> tokenFrs;
    private List<String> tblFriendKeys;
    private List<Friend> mListFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_chat);
        initView();
        setFirebaseInstance();
        takeCamera();
        setInstance();
//        loadMyFriend();
//        loadData();
        getListFr();
        setListView();
    }
    private void initView(){
        mLstUser = new ArrayList<>();
        mLstUserKeys = new ArrayList<>();
        tokenFrs = new ArrayList<>();
        tblFriendKeys = new ArrayList<>();
        mListFriends = new ArrayList<>();
        mLstUserKeys.add(Utils.USER_ID);
        mBtnCreateGroup = (Button)findViewById(R.id.btnCreateGroup);
        mEdtMembers = (EditText)findViewById(R.id.edtMembers);
        mEdtNameGroup = (EditText)findViewById(R.id.edtNamegroup);
        mEdtDescribe = (EditText)findViewById(R.id.edtDescribe);
        mLstUserToCreaTe = (ListView)findViewById(R.id.lstUserToCreate);
        mImgTakeAvatar = (ImageView)findViewById(R.id.imgTakeCamera);

        registerForContextMenu(mImgTakeAvatar);
        mLstUserToCreaTe.setOnItemClickListener(this);
        mBtnCreateGroup.setOnClickListener(this);
    }
    private void takeCamera(){
        mImgTakeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(v);
            }
        });
    }
    private void setInstance(){
        mReference = FirebaseDatabase.getInstance().getReference();
    }
//    private void loadData(){
//        mReference.child(Utils.TBL_USERS).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<User> list = new ArrayList<User>();
//                    for(DataSnapshot ds:dataSnapshot.getChildren()){
//                        User user = ds.getValue(User.class);
//                        list.add(user);
//                    }
//                    mLstUser = list;
//                    setListView();
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
    private void addToLstUser(List<String> keys){
        for(int i=0;i<keys.size();i++){
            String []parts = keys.get(i).split("-");
            if(parts[0].equals(Utils.USER_ID)){
                getListUser(parts[1]);
            } else if(parts[1].equals(Utils.USER_ID)) {
                getListUser(parts[0]);
            } else {

            }
        }
    }
    private void getListUser(String id) {
        mReference.child(Utils.TBL_USERS).orderByChild("mId").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> list = new ArrayList<User>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    mLstUser.add(user);
                }
                setListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadFr(Friend friend) {
        String myFriendID = "";
        String currentID = friend.getmIdCurrentUser();
        String friendID = friend.getmIdFriend();
        if (currentID.equals(Utils.USER_ID)) {
            myFriendID = friendID;
            getListUser(myFriendID);
        } else if (friendID.equals(Utils.USER_ID)) {
            myFriendID = currentID;
            getListUser(myFriendID);

        } else {

        }
    }
    private void getListFr() {
        mReference.child(Utils.TBL_FRIENDS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    mListFriends.add(friend);
                    loadFr(friend);


                } else {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//    private void getListUser(final String id) {
//        mReference.child(Utils.TBL_USERS).orderByChild("mId").equalTo(id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<User> users = new ArrayList<User>();
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    User user = ds.getValue(User.class);
//                    mListUser.add(user);
//                }
//                loadListview();
//                Log.e("Size lstUsr:", mListUser.size() + "");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
    private void loadMyFriend(){
        mReference.child(Utils.TBL_FRIENDS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<String>();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    keys.add(ds.getKey());
                }
                tblFriendKeys = keys;
                addToLstUser(tblFriendKeys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setListView(){
        mAdapterCreateGroup = new AdapterCreateGroup(this,R.layout.item_user_create_gr,mLstUser);
        mLstUserToCreaTe.setAdapter(mAdapterCreateGroup);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = mLstUser.get(position);
        if(!mLstUserKeys.contains(user.getmId())){
            mLstUserKeys.add(user.getmId());
            if(!user.getmToken().equals("")){
                tokenFrs.add(user.getmToken());
            }

            result += user.getmUsername()+"-";

        } else {
        }

        mEdtMembers.setText(result);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCreateGroup:
                createGroup("Zinza Messenger",Utils.USER_NAME);
                break;
        }
    }
    public void createGroup(String title,String body){
        String id = "";
        for(int i=0;i<mLstUserKeys.size();i++){
            id += mLstUserKeys.get(i);

        }
        if(mEdtNameGroup.getText().toString().equals("")){
            mEdtNameGroup.setFocusableInTouchMode(true);
            mEdtNameGroup.requestFocus();
            mEdtNameGroup.setFocusable(true);
            mEdtNameGroup.setError("Name is required");
        } else if(mEdtDescribe.getText().toString().equals("")){
            mEdtDescribe.setFocusableInTouchMode(true);
            mEdtDescribe.requestFocus();
            mEdtDescribe.setFocusable(true);
            mEdtDescribe.setError("Desribe is required");
        } else {
            String idGroup = mReference.push().getKey();
            String nameGroup = mEdtNameGroup.getText().toString();
            String describeGroup = mEdtDescribe.getText().toString();
            Group group = new Group(idGroup,nameGroup,url_avtar,mLstUserKeys,describeGroup,Utils.createAt());
            mReference.child(Utils.TBL_GROUP).child(idGroup).setValue(group);
            String idMessage = mReference.push().getKey();
            vn.com.zinza.zinzamessenger.model.Message message = new vn.com.zinza.zinzamessenger.model.Message(idMessage,Utils.USER_ID,idGroup,Utils.TEXT,"Hãy bắt đầu trò chuyện",Utils.createAt());
            mReference.child(Utils.TBL_GROUP_CHAT).child(idGroup).child(idMessage).setValue(message);
            Notification mNotification = new Notification(title,body);
            Data mData = new Data(Utils.USER_ID,Utils.getToken(),Utils.AVATAR_URL, Utils.TYPE_ADD_GROUP);
            instanceRetrofit(mNotification,mData,tokenFrs);
            finish();
        }

    }
    private void instanceRetrofit(Notification mNotification, Data mData, List<String> tokenFrs){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.GCM_SEND_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FCMService service = retrofit.create(FCMService.class);
        BodyRequestGCM mBodyRequestGCM = new BodyRequestGCM(tokenFrs,mNotification,mData);
        Call<ResultRequestGCM> call = service.sendMultiPush(mBodyRequestGCM);

        call.enqueue(new Callback<ResultRequestGCM>() {
            @Override
            public void onResponse(Response<ResultRequestGCM> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    Log.e("Success","Send all friend success");
                } else {
                    Log.e("Success","Respone is null");
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_take_photo,menu);
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
                mImgTakeAvatar.setImageBitmap(photo);
            } else {
                mImgTakeAvatar.setImageURI(uri);
            }
            Utils.NAME_FILE = getNameData(uri);
            StorageReference filePath = mStorageReference.child(Utils.GROUP_FOLDER).child(Utils.NAME_FILE);
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
        AlertDialog alertDialog = new AlertDialog.Builder(GroupChatActivity.this).create();
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
                        ActivityCompat.requestPermissions(GroupChatActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA);
                    }
                });
        alertDialog.show();
    }
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    private void setFirebaseInstance() {
        mReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }
}
