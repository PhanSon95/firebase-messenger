package vn.com.zinza.zinzamessenger.adapter.chat;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import me.himanshusoni.chatmessageview.ChatMessageView;
import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.activity.ChattingActivity;
import vn.com.zinza.zinzamessenger.downloadfirebase.DownloadThread;
import vn.com.zinza.zinzamessenger.firebasestorage.Download;
import vn.com.zinza.zinzamessenger.firebasestorage.DownloadService;
import vn.com.zinza.zinzamessenger.model.Message;
import vn.com.zinza.zinzamessenger.model.User;
import vn.com.zinza.zinzamessenger.utils.Helper;
import vn.com.zinza.zinzamessenger.utils.Utils;

import static vn.com.zinza.zinzamessenger.activity.ChattingActivity.MESSAGE_PROGRESS;

/**
 * Created by ASUS on 02/20/2017.
 */

public class AdapterMessageChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int SENDER = 0;
    public static final int RECIPENT = 1;
    public static final int SENDER_TEXT = 2;
    public static final int SENDER_IMAGE = 3;
    public static final int RECIPENT_TEXT = 4;
    public static final int RECIPENT_IMAGE = 5;

    public static final int SENDER_FILE = 6;
    public static final int SENDER_VIDEO = 7;
    public static final int RECIPENT_FILE = 8;
    public static final int RECIPENT_VIDEO = 9;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private Context mContext;
    private List<Message> mList;
    private int mLayout;
    private StorageReference mStorageReference;
    private DatabaseReference mRefColor;
    private DatabaseReference mRefUser;

    private ProgressDialog mProgressDialog;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private String color;

    public AdapterMessageChat(Context mContext, List<Message> mList) {
        this.mContext = mContext;
        this.mList = mList;
        registerReceiver();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mRefUser = FirebaseDatabase.getInstance().getReference(Utils.TBL_USERS);
    }

    public void addMessage(Message message) {
        mList.add(message);
        notifyItemInserted(getItemCount() - 1);
    }

    public void changeColor(String color) {
        this.color = color;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case SENDER_TEXT:
                View viewSenderText = inflater.inflate(R.layout.layout_sender_message, parent, false);
                viewHolder = new ViewHolderSenderText(viewSenderText);

                break;
            case SENDER_IMAGE:
                View viewSenderImage = inflater.inflate(R.layout.layout_sender_image, parent, false);
                viewHolder = new ViewHolderSenderImage(viewSenderImage);
                break;
            case SENDER_FILE:
                View viewSenderFile = inflater.inflate(R.layout.layout_sender_file, parent, false);
                viewHolder = new ViewHolderSenderFile(viewSenderFile);
                break;
            case SENDER_VIDEO:
                View viewSenderVideo = inflater.inflate(R.layout.layout_sender_video, parent, false);
                viewHolder = new ViewHolderSenderVideoFile(viewSenderVideo);
                break;
            case RECIPENT_TEXT:
                View viewRecipientText = inflater.inflate(R.layout.layout_recipent_message, parent, false);
                viewHolder = new ViewHolderRecipientText(viewRecipientText);
                break;
            case RECIPENT_IMAGE:
                View viewRecipientImage = inflater.inflate(R.layout.layout_recipent_image, parent, false);
                viewHolder = new ViewHolderRecipientImage(viewRecipientImage);
                break;
            case RECIPENT_FILE:
                View viewRecipientFile = inflater.inflate(R.layout.layout_recipient_file, parent, false);
                viewHolder = new ViewHolderRecipientFile(viewRecipientFile);
                break;
            case RECIPENT_VIDEO:
                View viewRecipientVideo = inflater.inflate(R.layout.layout_recipient_video, parent, false);
                viewHolder = new ViewHolderRecipientVideoFile(viewRecipientVideo);
                break;
            default:
                View viewSenderDefault = inflater.inflate(R.layout.layout_sender_message, parent, false);
                viewHolder = new ViewHolderSenderText(viewSenderDefault);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case SENDER_TEXT:
                ViewHolderSenderText viewHolderSenderText = (ViewHolderSenderText) holder;
                configureMessageSenderView(viewHolderSenderText, position);
                break;
            case SENDER_IMAGE:
                ViewHolderSenderImage viewHolderSenderImage = (ViewHolderSenderImage) holder;
                configureImageSenderView(viewHolderSenderImage, position);
                ImageView img = (ImageView) viewHolderSenderImage.getSenderImage();
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetailImage(mList.get(position).getmContent());
                    }
                });
                break;
            case SENDER_FILE:
                ViewHolderSenderFile viewHolderSenderFile = (ViewHolderSenderFile) holder;
                configureFileSender(viewHolderSenderFile, position);

                break;
            case SENDER_VIDEO:
                ViewHolderSenderVideoFile viewHolderSenderVideoFile = (ViewHolderSenderVideoFile) holder;
                configureFileVideoSender(viewHolderSenderVideoFile, position);
                break;
            case RECIPENT_TEXT:
                ViewHolderRecipientText viewHolderRecipientText = (ViewHolderRecipientText) holder;
                configureMessageRecipientView(viewHolderRecipientText, position);
                break;
            case RECIPENT_IMAGE:
                ViewHolderRecipientImage viewHolderRecipientImage = (ViewHolderRecipientImage) holder;
                configureImageRecipientView(viewHolderRecipientImage, position);
                ImageView img2 = (ImageView) viewHolderRecipientImage.getRecipientImage();
                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetailImage(mList.get(position).getmContent());
                    }
                });
                break;
            case RECIPENT_VIDEO:
                ViewHolderRecipientVideoFile viewHolderRecipientVideoFile = (ViewHolderRecipientVideoFile) holder;
                configureFileVideoRecipient(viewHolderRecipientVideoFile, position);
                ChatMessageView chatMessageView1 = viewHolderRecipientVideoFile.getChatView();
                chatMessageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        PopupMenu popup = new PopupMenu(mContext, view);
//                        popup.getMenuInflater().inflate(R.menu.popup_menu_video,
//                                popup.getMenu());
//                        popup.show();
//                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                            @Override
//                            public boolean onMenuItemClick(MenuItem item) {
//                                switch (item.getItemId()) {
//                                    case R.id.openVideo:
//                                        String urlToStream = Helper.getURLImage(mList.get(position).getmContent());
//                                        Intent myIntent = new Intent(mContext,
//                                                VideoViewActivity.class);
//                                        myIntent.putExtra(Utils.URL_STREAMING, urlToStream);
//                                        mContext.startActivity(myIntent);
//                                        break;
//                                    case R.id.downloadVideo:
////                                        String urlToDownload = Helper.getUrlDownload(mList.get(position).getmContent());
////                                        Utils.NAME_FILE = Helper.getName(mList.get(position).getmContent());
////                                        Utils.FIREBASE_END_URL = urlToDownload;
////                                        Utils.showToast("Video đã bắt đầu được tải về", mContext);
////                                        startDownload();
//
//                                        break;
//                                    default:
//                                        break;
//                                }
//                                return true;
//                            }
//                        });
                        Utils.URL_CONTENT = mList.get(position).getmContent();
                        new DownLoadTask().execute(Utils.URL_CONTENT);
                    }
                });
                break;
            case RECIPENT_FILE:
                ViewHolderRecipientFile viewHolderRecipientFile = (ViewHolderRecipientFile) holder;
                configureFileRecipient(viewHolderRecipientFile, position);
                ChatMessageView chatMessageView = viewHolderRecipientFile.getChatView();
                chatMessageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.URL_CONTENT = mList.get(position).getmContent();
                        new DownLoadTask().execute(Utils.URL_CONTENT);
                    }
                });
                break;

        }
    }


    private void sendNotification(Download download) {

        sendIntent(download);
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_action_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

    }

    private class DownLoadTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            // do above Server call here
            Download download = new Download();
            sendNotification(download);
            download(params[0]);

            return "some message";
        }

        @Override
        protected void onPostExecute(String message) {
            //process message
//            Merge();
//            long end = System.currentTimeMillis();   // start láº¥y thá»�i gian theo millisecond
            System.out.println(" merge done");
        }
    }

    private void checkPart(String part) {
        String part1 = part.substring(0, 5);
        switch (part1) {
            case "Part1":
                Utils.URL_PART_1 = part;
                break;
            case "Part2":
                Utils.URL_PART_2 = part;
                break;
            case "Part3":
                Utils.URL_PART_3 = part;
                break;
            case "Part4":
                Utils.URL_PART_4 = part;
                break;
            case "Part5":
                Utils.URL_PART_5 = part;
                break;
        }
    }

    private void download(final String content) {
        final long start = System.currentTimeMillis();
        String fullPart = Helper.getFullPart(Utils.URL_CONTENT);
        final String urlDown = Helper.getUrlStorageDownload(Utils.URL_CONTENT);

        String result = fullPart;

        String p1 = result.substring(0, result.indexOf("*"));
        String result1 = result.substring(result.indexOf("*") + 1);
        Log.e("P1-", p1);

        String p2 = result1.substring(0, result1.indexOf("*"));
        String result2 = result1.substring(result1.indexOf("*") + 1);
        Log.e("P2-", p2);

        String p3 = result2.substring(0, result2.indexOf("*"));
        String result3 = result2.substring(result2.indexOf("*") + 1);
        Log.e("P3-", p3);

        String p4 = result3.substring(0, result3.indexOf("*"));
        String result4 = result3.substring(result3.indexOf("*") + 1);
        Log.e("P4-", p4);

        String p5 = result4.substring(0, result4.indexOf("*"));
        String result5 = result4.substring(result4.indexOf("*") + 1);
        Log.e("P5-", p5);

        checkPart(p1);
        checkPart(p2);
        checkPart(p3);
        checkPart(p4);
        checkPart(p5);
        DownloadThread d1 = new DownloadThread(Helper.getUrlFileDownload(Utils.URL_PART_1), Utils.ROOT_FOLDER + "/1");
        DownloadThread d2 = new DownloadThread(Helper.getUrlFileDownload(Utils.URL_PART_2), Utils.ROOT_FOLDER + "/2");
        DownloadThread d3 = new DownloadThread(Helper.getUrlFileDownload(Utils.URL_PART_3), Utils.ROOT_FOLDER + "/3");
        DownloadThread d4 = new DownloadThread(Helper.getUrlFileDownload(Utils.URL_PART_4), Utils.ROOT_FOLDER + "/4");
        DownloadThread d5 = new DownloadThread(Helper.getUrlFileDownload(Utils.URL_PART_5), Utils.ROOT_FOLDER + "/5");
        Thread t1 = new Thread(d1);
        Thread t2 = new Thread(d2);
        Thread t3 = new Thread(d3);
        Thread t4 = new Thread(d4);
        Thread t5 = new Thread(d5);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        while ((d1.done + d2.done + d3.done + d4.done + d5.done) < 5) {
            try {
                Thread.sleep(100);

            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        String type = Helper.checkType(urlDown);
        String name = Helper.getNameFile(Utils.URL_CONTENT);
        Merge(name, type);
        long end = System.currentTimeMillis();
        System.out.println("Time Millis: " + (end - start) + " merge done");
        onDownloadComplete();
    }

    public static void Merge(String nameFile, String type) {
        File file1 = new File(Utils.ROOT_FOLDER + "/1");
        File file2 = new File(Utils.ROOT_FOLDER + "/2");
        File file3 = new File(Utils.ROOT_FOLDER + "/3");
        File file4 = new File(Utils.ROOT_FOLDER + "/4");
        File file5 = new File(Utils.ROOT_FOLDER + "/5");
        File oFile = new File(Utils.ROOT_FOLDER + "/" + nameFile);
        try {
            FileInputStream is1 = new FileInputStream(file1);
            FileInputStream is2 = new FileInputStream(file2);
            FileInputStream is3 = new FileInputStream(file3);
            FileInputStream is4 = new FileInputStream(file4);
            FileInputStream is5 = new FileInputStream(file5);

            FileOutputStream fos = new FileOutputStream(oFile);

            FileChannel f1 = is1.getChannel();
            FileChannel f2 = is2.getChannel();
            FileChannel f3 = is3.getChannel();
            FileChannel f4 = is4.getChannel();
            FileChannel f5 = is5.getChannel();
            FileChannel f6 = fos.getChannel();

            f6.transferFrom(f1, 0, f1.size());
            f6.transferFrom(f2, f1.size(), f2.size());
            f6.transferFrom(f3, f1.size() + f2.size(), f3.size());
            f6.transferFrom(f4, f1.size() + f2.size() + f3.size(), f4.size());
            f6.transferFrom(f5, f1.size() + f2.size() + f3.size() + f4.size(), f5.size());

            f1.close();
            f2.close();
            f3.close();
            f4.close();
            f5.close();
            f6.close();

            fos.close();
            is1.close();
            is2.close();
            is3.close();
            is4.close();
            is5.close();

            file1.delete();
            file2.delete();
            file3.delete();
            file4.delete();
            file5.delete();


        } catch (Exception e) {

        }
    }


    private void sendIntent(Download download) {

        Intent intent = new Intent(ChattingActivity.MESSAGE_PROGRESS);
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void onDownloadComplete() {

        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());

    }

    private void showProgress(String title, String message) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public int getItemViewType(int position) {
        String type = mList.get(position).getmType();
        int status = mList.get(position).getRecipientOrSenderStatus();
        if (status == SENDER_TEXT && type.equals(Utils.TEXT)) {
            return SENDER_TEXT;
        } else if (status == SENDER_IMAGE && type.equals(Utils.IMAGE)) {
            return SENDER_IMAGE;
        } else if (status == SENDER_FILE && type.equals(Utils.FILE)) {
            return SENDER_FILE;
        } else if (status == SENDER_VIDEO && type.equals(Utils.VIDEO)) {
            return SENDER_VIDEO;
        } else if (status == RECIPENT_TEXT && type.equals(Utils.TEXT)) {
            return RECIPENT_TEXT;
        } else if (status == RECIPENT_IMAGE && type.equals(Utils.IMAGE)) {
            return RECIPENT_IMAGE;
        } else if (status == RECIPENT_FILE && type.equals(Utils.FILE)) {
            return RECIPENT_FILE;
        } else {
            return RECIPENT_VIDEO;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void configureMessageSenderView(ViewHolderSenderText viewHolderSender, int position) {
        Message senderFireMessage = mList.get(position);
        viewHolderSender.getSenderContent().setText(senderFireMessage.getmContent());
        viewHolderSender.getTime().setText(Helper.convertTime(senderFireMessage.getmTime()));
        viewHolderSender.getChatView().setAnimation(Helper.generateAnimation());


    }

    private void configureImageSenderView(ViewHolderSenderImage viewHolderSenderImage, final int position) {
        Message senderFireMessage = mList.get(position);
        Glide.with(mContext)
                .load(senderFireMessage.getmContent())
                .placeholder(R.drawable.place_hoder)
                .crossFade()
                .into(viewHolderSenderImage.getSenderImage());
        viewHolderSenderImage.getTime().setText(Helper.convertTime(senderFireMessage.getmTime()));
        viewHolderSenderImage.getChatView().setAnimation(Helper.generateAnimation());
    }

    private void configureFileSender(ViewHolderSenderFile viewHolderSenderFile, final int position) {
        Message senderFireMessage = mList.get(position);
        viewHolderSenderFile.getSenderFile().setText(Helper.getNameFile(senderFireMessage.getmContent()));
        viewHolderSenderFile.getTime().setText(Helper.convertTime(senderFireMessage.getmTime()));
        viewHolderSenderFile.getChatView().setAnimation(Helper.generateAnimation());
    }

    private void configureFileVideoSender(ViewHolderSenderVideoFile viewHolderSenderVideoFile, final int position) {
        Message senderFireMessage = mList.get(position);
//        viewHolderSenderVideoFile.get().setText(Helper.getName(senderFireMessage.getmContent()));
        viewHolderSenderVideoFile.getTvNameVideo().setText(Helper.getName(senderFireMessage.getmContent()));
        viewHolderSenderVideoFile.getTvTime().setText(Helper.convertTime(senderFireMessage.getmTime()));
        viewHolderSenderVideoFile.getChatView().setAnimation(Helper.generateAnimation());
    }

    private void configureFileVideoRecipient(ViewHolderRecipientVideoFile viewHolderRecipientVideoFile, final int position) {
        Message senderFireMessage = mList.get(position);
//        viewHolderSenderVideoFile.get().setText(Helper.getName(senderFireMessage.getmContent()));
        viewHolderRecipientVideoFile.getTvNameVideo().setText(Helper.getName(senderFireMessage.getmContent()));
        viewHolderRecipientVideoFile.getTvTime().setText(Helper.convertTime(senderFireMessage.getmTime()));
        viewHolderRecipientVideoFile.getChatView().setAnimation(Helper.generateAnimation());
        setAvatar(viewHolderRecipientVideoFile.getImgRecipentVideo(),senderFireMessage.getmIdSender());
    }

    private void configureMessageRecipientView(ViewHolderRecipientText viewHolderMessageRecipient, int position) {
        Message senderFireMessage = mList.get(position);
        viewHolderMessageRecipient.getRecipientContent().setText(senderFireMessage.getmContent());
        viewHolderMessageRecipient.getTime().setText(Helper.convertTime(senderFireMessage.getmTime()));
        viewHolderMessageRecipient.getChatView().setAnimation(Helper.generateAnimation());
        setAvatar(viewHolderMessageRecipient.getImgRecipent(),senderFireMessage.getmIdSender());

    }

    private void configureImageRecipientView(ViewHolderRecipientImage viewHolderRecipientImage, final int position) {
        Message senderFireMessage = mList.get(position);
        Glide.with(mContext)
                .load(senderFireMessage.getmContent())
                .placeholder(R.drawable.place_hoder)
                .crossFade()
                .into(viewHolderRecipientImage.getRecipientImage());
        viewHolderRecipientImage.getTime().setText(Helper.convertTime(senderFireMessage.getmTime()));
        viewHolderRecipientImage.getChatView().setAnimation(Helper.generateAnimation());
        setAvatar(viewHolderRecipientImage.getImgRecipentImage(),senderFireMessage.getmIdSender());
    }

    private void configureFileRecipient(ViewHolderRecipientFile viewHolderRecipientFile, final int position) {
        Message senderFireMessage = mList.get(position);
        viewHolderRecipientFile.getRecipientFile().setText(Helper.getNameFile(senderFireMessage.getmContent()));
        viewHolderRecipientFile.getTime().setText(Helper.convertTime(senderFireMessage.getmTime()));
        viewHolderRecipientFile.getChatView().setAnimation(Helper.generateAnimation());
        setAvatar(viewHolderRecipientFile.getImgRecipentFile(),senderFireMessage.getmIdSender());
    }
    private void setAvatar(final ImageView imageView, String id){
        mRefUser.orderByChild("mId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String urlAvatar="";
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    urlAvatar = user.getmAvatar();
                }
                if(!urlAvatar.equals("")){
                    Glide.with(mContext).load(urlAvatar).crossFade().into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.ic_avatar_df);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void cleanUp() {
        mList.clear();
        notifyDataSetChanged();
    }

    public class ViewHolderSenderText extends RecyclerView.ViewHolder {
        EmojiconTextView content;
        TextView time;
        ChatMessageView view;

        public ViewHolderSenderText(View itemView) {
            super(itemView);
            content = (EmojiconTextView) itemView.findViewById(R.id.text_view_sender_message);
            time = (TextView) itemView.findViewById(R.id.text_view_time_sender);
            view = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
        }

        public ChatMessageView getChatView() {
            return view;
        }

        public EmojiconTextView getSenderContent() {
            return content;
        }

        public TextView getTime() {
            return time;
        }
    }

    public class ViewHolderRecipientText extends RecyclerView.ViewHolder {
        EmojiconTextView content;
        TextView time;
        ChatMessageView view;
        ImageView imgRecipent;

        public ViewHolderRecipientText(View itemView) {
            super(itemView);
            content = (EmojiconTextView) itemView.findViewById(R.id.text_view_recipient_message);
            time = (TextView) itemView.findViewById(R.id.text_view_time_recipent);
            view = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
            imgRecipent = (ImageView)itemView.findViewById(R.id.imgReciepent);
        }

        public ChatMessageView getChatView() {
            return view;
        }
        public EmojiconTextView getRecipientContent() {
            return content;
        }

        public TextView getTime() {
            return time;
        }
        public ImageView getImgRecipent(){
            return imgRecipent;
        }
    }

    public class ViewHolderSenderImage extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView time;
        ChatMessageView view;

        public ViewHolderSenderImage(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_message_sender);
            time = (TextView) itemView.findViewById(R.id.text_view_time_sender_image);
            view = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
        }

        public ChatMessageView getChatView() {
            return view;
        }

        public ImageView getSenderImage() {
            return imageView;
        }

        public TextView getTime() {
            return time;
        }
    }

    public class ViewHolderRecipientImage extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView time;
        ChatMessageView view;
        ImageView imgRecipentImage;

        public ViewHolderRecipientImage(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_message_recipent);
            time = (TextView) itemView.findViewById(R.id.text_view_time_recipent_image);
            view = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
            imgRecipentImage = (ImageView)itemView.findViewById(R.id.imgReciepentImage);
        }

        public ChatMessageView getChatView() {
            return view;
        }

        public ImageView getRecipientImage() {
            return imageView;
        }

        public TextView getTime() {
            return time;
        }
        public ImageView getImgRecipentImage(){
            return imgRecipentImage;
        }
    }

    public class ViewHolderSenderFile extends RecyclerView.ViewHolder {
        TextView txtNameFile;
        TextView time;
        ChatMessageView view;

        public ViewHolderSenderFile(View itemView) {
            super(itemView);
            txtNameFile = (TextView) itemView.findViewById(R.id.text_view_sender_file);
            time = (TextView) itemView.findViewById(R.id.file_time_sender);
            view = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
        }

        public ChatMessageView getChatView() {
            return view;
        }

        public TextView getSenderFile() {
            return txtNameFile;
        }

        public TextView getTime() {
            return time;
        }
    }
    //

    public class ViewHolderSenderVideoFile extends RecyclerView.ViewHolder {
        ImageView imgThumble;
        TextView tvNameVideo;
        TextView tvTime;
        ChatMessageView view;

        public ViewHolderSenderVideoFile(View itemView) {
            super(itemView);
            this.tvNameVideo = (TextView) itemView.findViewById(R.id.tvNameFileVideo);
            this.tvTime = (TextView) itemView.findViewById(R.id.text_view_time_sender);
            view = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
        }

        public ChatMessageView getChatView() {
            return view;
        }

        public ImageView getImgThumble() {
            return imgThumble;
        }

        public TextView getTvNameVideo() {
            return tvNameVideo;
        }

        public TextView getTvTime() {
            return tvTime;
        }
    }

    public class ViewHolderRecipientVideoFile extends RecyclerView.ViewHolder {
        ImageView imgThumble;
        TextView tvNameVideo;
        TextView tvTime;
        ChatMessageView view;
        ImageView imgRecipentVideo;

        public ViewHolderRecipientVideoFile(View itemView) {
            super(itemView);
            this.tvNameVideo = (TextView) itemView.findViewById(R.id.tvNameFileVideo);
            this.tvTime = (TextView) itemView.findViewById(R.id.text_view_time_recipent);
            view = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
            imgRecipentVideo = (ImageView)itemView.findViewById(R.id.imgReciepentVideo);
        }

        public ImageView getImgThumble() {
            return imgThumble;
        }

        public TextView getTvNameVideo() {
            return tvNameVideo;
        }

        public TextView getTvTime() {
            return tvTime;
        }

        public ChatMessageView getChatView() {
            return view;
        }
        public ImageView getImgRecipentVideo(){
            return imgRecipentVideo;
        }
    }

    public class ViewHolderRecipientFile extends RecyclerView.ViewHolder {
        TextView txtNameFile;
        TextView time;
        ChatMessageView view;
        ImageView imgRecipentFile;


        public ViewHolderRecipientFile(View itemView) {
            super(itemView);
            txtNameFile = (TextView) itemView.findViewById(R.id.text_view_recipient_file);
            time = (TextView) itemView.findViewById(R.id.file_time_recipent);
            view = (ChatMessageView) itemView.findViewById(R.id.contentMessageChat);
            imgRecipentFile = (ImageView)itemView.findViewById(R.id.imgReciepentFile);
        }

        public ChatMessageView getChatView() {
            return view;
        }

        public TextView getRecipientFile() {
            return txtNameFile;
        }

        public TextView getTime() {
            return time;
        }
        public ImageView getImgRecipentFile(){
            return imgRecipentFile;
        }
    }

    private void showDetailImage(final String url) {
        final Dialog nagDialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(true);
        nagDialog.setContentView(R.layout.detail_image);
        Button btnDownload = (Button) nagDialog.findViewById(R.id.btnDownloadImage);
        ImageView ivPreview = (ImageView) nagDialog.findViewById(R.id.imgDetail);
        String urlToDownload = Helper.getUrlDownload(url);
        String urlToShowImage = url;
        Utils.FIREBASE_END_URL = urlToDownload;
//        Picasso.with(mContext).load(urlToShowImage).placeholder(R.drawable.place_hoder).into(ivPreview);
        Glide.with(mContext).load(url).placeholder(R.drawable.place_hoder).into(ivPreview);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nagDialog.dismiss();
                Utils.showToast("File đã bắt đầu được tải về", mContext);
                startDownload();

            }
        });
        nagDialog.show();
    }

    private void startDownload() {
        Intent intent = new Intent(mContext, DownloadService.class);
        mContext.startService(intent);

    }

    private void registerReceiver() {

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MESSAGE_PROGRESS)) {
                Download download = intent.getParcelableExtra("download");
                if (download.getProgress() == 100) {

                } else {

                }
            }
        }
    };

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }
//    private void requestPermission(){
//
//        ActivityCompat.requestPermissions(mContext,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
//
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    startDownload();
//                } else {
//
//
//                }
//                break;
//        }
//    }


}
