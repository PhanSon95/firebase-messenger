package vn.com.zinza.zinzamessenger.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;

import java.io.File;
import java.util.ArrayList;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.adapter.AdapterHistoryFile;
import vn.com.zinza.zinzamessenger.model.FileHistory;
import vn.com.zinza.zinzamessenger.utils.FileUtils;
import vn.com.zinza.zinzamessenger.utils.Utils;

/**
 * Created by dell on 17/02/2017.
 */

public class HistoryFileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Toolbar mToolbarHis;
    private ListView lvListHistory;
    private AdapterHistoryFile adapterHistoryFile;
    private ArrayList<FileHistory> listFileHis;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_history_file);
        initViews();
        Uri uri = MediaStore.Files.getContentUri("external");
        Log.e("MediaStore.Files", uri+"");
//        getListFile();

    }

//    private void getListFile(){
//        String path = Utils.ROOT_FOLDER;
//        Log.d("Files", "Path: " + path);
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//        Log.d("Files", "Size: "+ files.length);
//        for (int i = 0; i < files.length; i++)
//        {
//            Log.d("Files", "FileName:" + files[i].getName());
//        }
//    }

    private void initViews() {
        mToolbarHis= (Toolbar) findViewById(R.id.historyToolbar);
        setSupportActionBar(mToolbarHis);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbarHis.setNavigationIcon(R.drawable.ic_action_back);
        lvListHistory= (ListView) findViewById(R.id.lvListFileHistory);
        loadData();
        adapterHistoryFile= new AdapterHistoryFile(this,android.R.layout.simple_list_item_1,listFileHis);
        lvListHistory.setAdapter(adapterHistoryFile);
        lvListHistory.setOnItemClickListener(this);

        mToolbarHis.setNavigationOnClickListener(this);
    }

    //load data file.
    private void loadData() {
        listFileHis= new ArrayList<>();
        FileUtils fileUtils= new FileUtils();
        ArrayList<FileHistory> arrayListFile= fileUtils.getListFile();
        if (arrayListFile.isEmpty()){
            Toast.makeText(this,"Not have any files",Toast.LENGTH_SHORT).show();
        }
        else {
            listFileHis.addAll(arrayListFile);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history_file,menu);
        MenuItem item = menu.findItem(R.id.action_search_history);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("Search file...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapterHistoryFile.filter("");
                    lvListHistory.clearTextFilter();
                } else {
                    adapterHistoryFile.filter(newText);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(this,"Open file at position: "+i,Toast.LENGTH_LONG).show();

        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(AdapterHistoryFile.fileExt(listFileHis.get(i).getName()).substring(1));
        Log.e("mimeType",mimeType);
        File file=new File(listFileHis.get(i).getPathFileInStorage());
        newIntent.setDataAndType(Uri.fromFile(file),mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            this.startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }
}
