package vn.com.zinza.zinzamessenger.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.activity.HistoryFileActivity;
import vn.com.zinza.zinzamessenger.model.FileHistory;

/**
 * Created by dell on 17/02/2017.
 */

public class AdapterHistoryFile extends ArrayAdapter<FileHistory> {
    private ArrayList<FileHistory> arrFile;
    private ArrayList<FileHistory> searchList;
    private LayoutInflater inflater;
    private Context context;
    private Dialog mDialogDetail;
    private TextView mTvNameFile, mTvPathFile, mTvSizeFile;
    private Button mBtnClose;

    public AdapterHistoryFile(Context mContext, int resource, ArrayList<FileHistory> objects) {
        super(mContext, resource, objects);
        context = mContext;
        arrFile = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchList = new ArrayList<>();
        searchList.addAll(arrFile);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_history_file, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imFile);
            TextView tvName = (TextView) convertView.findViewById(R.id.tvNameFile);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvTimeFile);
            ImageView imgOption = (ImageView) convertView.findViewById(R.id.imgOption);
            viewHolder.img = imageView;
            viewHolder.tvName = tvName;
            viewHolder.tvDate = tvDate;
            viewHolder.imgOption = imgOption;
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
//        viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(arrFile.get(position).getImg()));
        viewHolder.tvDate.setText(arrFile.get(position).getDate());
        String typeFile = arrFile.get(position).getName();
        if (typeFile.toLowerCase().contains(".html")) {
//            viewHolder.img.setImageDrawable(R.drawable.);
        } else if (typeFile.toLowerCase().contains(".xls") || typeFile.contains(".xlsx")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.exel));
        } else if (typeFile.toLowerCase().contains(".doc") || typeFile.contains(".docx")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.doc));
        } else if (typeFile.toLowerCase().contains(".zip") || typeFile.contains(".rar")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.rar));
        } else if (typeFile.toLowerCase().contains(".ppt")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ppt));
        } else if (typeFile.toLowerCase().contains(".pdf")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.pdf));
        } else if (typeFile.toLowerCase().contains(".mp3")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.mp3));
        } else if (typeFile.toLowerCase().contains(".mp4") || typeFile.contains(".3gp") || typeFile.contains(".avi") || typeFile.contains(".flv") || typeFile.contains(".mpg4")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.mp4));
        } else if (typeFile.toLowerCase().contains(".txt")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.txt));
        } else if (typeFile.toLowerCase().contains(".jpg") || typeFile.contains(".png") || typeFile.contains(".jpeg")) {
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.jpg));
        } else {
            // other file
            viewHolder.img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.default_file));
        }


        viewHolder.tvName.setText(arrFile.get(position).getName() + "");
        viewHolder.tvDate.setText(arrFile.get(position).getDate() + "");
        viewHolder.imgOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.imgOption:
                        PopupMenu popup = new PopupMenu(getContext(), view);
                        popup.getMenuInflater().inflate(R.menu.popup_menu,
                                popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.openFile:
                                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                                        Intent newIntent = new Intent(Intent.ACTION_VIEW);
                                        String mimeType = myMime.getMimeTypeFromExtension(fileExt(arrFile.get(position).getName()).substring(1));
                                        Log.e("mimeType", mimeType);
                                        File file = new File(arrFile.get(position).getPathFileInStorage());
                                        newIntent.setDataAndType(Uri.fromFile(file), mimeType);
                                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        try {
                                            context.startActivity(newIntent);
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                                        }
                                        break;
                                    case R.id.showDetail:
                                        String nameFile = arrFile.get(position).getName();
                                        String pathFile = arrFile.get(position).getPathFileInStorage();
                                        String sizeFile = arrFile.get(position).getSize();
                                        initDialogDetail(nameFile, pathFile, sizeFile);
                                        break;
                                    case R.id.delete:
                                        File fileDel = new File(arrFile.get(position).getPathFileInStorage());
                                        boolean deleted = fileDel.delete();
                                        if (deleted) {
                                            Toast.makeText(getContext(), "Deleted file", Toast.LENGTH_LONG).show();
                                            notifyDataSetChanged();
                                            Intent myIntent = new Intent(context, HistoryFileActivity.class);
                                            context.startActivity(myIntent);
                                            ((Activity)context).finish();
                                        } else {

                                        }
                                        break;
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });

                        break;
                    default:
                        break;
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        private ImageView img, imgOption;
        private TextView tvName, tvDate;
    }

    private void initDialogDetail(String nameFile, String pathFile, String sizeFile) {
        mDialogDetail = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        mDialogDetail.setContentView(R.layout.dialog_show_file_info);
        mDialogDetail.show();
        mDialogDetail.setCancelable(true);

        mTvNameFile = (TextView) mDialogDetail.findViewById(R.id.tvNameFileDl);
        mTvPathFile = (TextView) mDialogDetail.findViewById(R.id.tvPathDl);
        mTvSizeFile = (TextView) mDialogDetail.findViewById(R.id.tvSizeFileDl);
        mBtnClose = (Button) mDialogDetail.findViewById(R.id.btnClose);

        String sizeF = convertSizeFile(sizeFile);

        if (nameFile != null) {
            mTvNameFile.setText("- Name: " + nameFile);
            mTvPathFile.setText("- Path: " + pathFile);
            mTvSizeFile.setText("- Size: " + sizeF);
        }

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogDetail.dismiss();
            }
        });
    }

    private String convertSizeFile(String sizeFile) {
        String sizeReturn = "";
        int sizeF = Integer.parseInt(sizeFile);
        int kb = 1024;
        int mb = 1024 * 1024;

        if (sizeF > kb && sizeF < mb) {
            sizeReturn = sizeF / kb + " Kbs";
        } else if (sizeF < kb) {
            sizeReturn = sizeF + " Bytes";
        } else if (sizeF > mb) {
            double i = sizeF / mb;
            i = Math.floor(i * 100) / 100;
            sizeReturn = i + " Mbs";
        } else {

        }
        return sizeReturn;
    }

    public static String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            Log.e("fileExt", ext);
            return ("." + ext.toLowerCase());

        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrFile.clear();
        if (charText.length() == 0) {
            arrFile.addAll(searchList);
        } else {
            for (FileHistory s : searchList) {
                String nameSearch = s.getName();
                if (nameSearch.toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrFile.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }
}
