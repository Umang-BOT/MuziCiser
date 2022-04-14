package com.example.music_player;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.lights.LightState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.style.EasyEditSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String[] items;
    AlertDialog.Builder ab;
    AlertDialog ad;
    ListView listview;
    MenuItem men;
    ArrayAdapter<String> Adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview=findViewById(R.id.list);
        runtimeperm();
        //System.out.println(items.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        switch(id){
            case R.id.about:
                Intent in=new Intent(MainActivity.this,abouttt.class);
                startActivity(in);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed(){
        ab=new AlertDialog.Builder(this);
        ab.setTitle("are you sure want to quit?");
        ab.setMessage("exit?");
        ab.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        ab.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        ad=ab.create();
        ad.show();
    }
    public void runtimeperm(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                display();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
    public ArrayList<File> song(File file){
        ArrayList<File> lists=new ArrayList<>();
        File[] files=file.listFiles();
        if(files!=null) {
            for (File filess : files) {
                if (filess.isDirectory() && !filess.isHidden()) {
                    lists.addAll(song(filess));
                } else {
                    if (filess.getName().endsWith(".mp3") || filess.getName().endsWith(".wav")) {
                        lists.add(filess);
                    }
                }
            }
        }return lists;
    }
    public void display() {
        final ArrayList<File> mysong = song(Environment.getExternalStorageDirectory());
        items = new String[mysong.size()];
        for (int i = 0; i < mysong.size(); i++) {
            items[i] = mysong.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }
        adap adapter = new adap();;
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songname = (String) listview.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), soundscreen.class)
                        .putExtra("song", mysong).putExtra("name", songname).putExtra("pos", i));
            }
        });
    }
    class adap extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v1=getLayoutInflater().inflate(R.layout.sampleimages,null);
            TextView text=v1.findViewById(R.id.textView);
            text.setSelected(true);
            text.setText(items[i]);
            return v1;
        }
    }

}