package com.innovativesolutions.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private static final int REQUEST_PATH = 1;
    GridView grid;
    private File currentDir;

    private com.innovativesolutions.finder.FileArrayAdapter adapter;
    RadioButton phone,sdcard;
    RadioGroup radioGroup;
    TextView hearderTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // launch app in Landscape more

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view); // Adding Left Nav View
        navigationView.setNavigationItemSelectedListener(this);
        // Radio btn for select phone or SD
        phone = (RadioButton)findViewById(R.id.rPhone);
        sdcard = (RadioButton)findViewById(R.id.rSdCard);
        phone.setChecked(true);
        radioGroup = (RadioGroup)findViewById(R.id.rdGroup);

        // Header Tile
        hearderTitle = findViewById(R.id.header_title);
        hearderTitle.setText("Phone");
        //  Grid view
        grid = (GridView) findViewById(R.id.grid);
        // to know the file system path
        File f3[] =  getApplicationContext().getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);

        // set phone storage on app firt time launches
        currentDir = new File("/storage/emulated/0/");
        fill(currentDir);






    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        String str = "";
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rPhone:
                if (checked)
                    str = "Phone Selected";
                hearderTitle.setText("Phone");
                currentDir = new File("/storage/emulated/0/");
                fill(currentDir);

                break;
            case R.id.rSdCard:
                if (checked)
                    str = "SdCard Selected";
                hearderTitle.setText("SD Card");
                currentDir = new File("/storage/sdcard1/");
                fill(currentDir);
                break;
        }
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }



    private void fill(File f) {

        TextView absolutePath = findViewById(R.id.filePath);
        File[] dirs = f.listFiles();
        absolutePath.setText("Current Dir: " + f.getAbsolutePath());
        List<Item> dir = new ArrayList<Item>();
        List<com.innovativesolutions.finder.Item> fls = new ArrayList<com.innovativesolutions.finder.Item>();
        try {
            for (File ff : dirs) {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if (ff.isDirectory()) {


                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if (fbuf != null) {
                        buf = fbuf.length;
                    } else buf = 0;
                    String num_item = String.valueOf(buf);
                    if (buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formated = lastModDate.toString();
                    dir.add(new com.innovativesolutions.finder.Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(), "directory_icon"));
                } else {

                    fls.add(new com.innovativesolutions.finder.Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "file_icon"));
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new com.innovativesolutions.finder.Item("..", "Parent Directory", "", f.getParent(), "directory_up"));
        adapter = new com.innovativesolutions.finder.FileArrayAdapter(com.innovativesolutions.finder.MainActivity.this, R.layout.file_view, dir);


        grid.setAdapter(adapter);
        grid.setOnItemClickListener(this);
        System.out.println("");
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        com.innovativesolutions.finder.Item o = adapter.getItem(position);
        if (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up")) {
            currentDir = new File(o.getPath());
            fill(currentDir);
            //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        } else {
            //onFileClick(o);
        }

    }


    private void onFileClick(com.innovativesolutions.finder.Item o) {
        Toast.makeText(this, "File Clicked: " + currentDir, Toast.LENGTH_SHORT).show();

    }

// get selected Radio button first
    public String getSelectedRadioGroup(RadioGroup radioGroup) {
        String selected = "";
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (checkedRadioButtonId == R.id.rPhone) {
            // Do something with the button
            selected = "phone";
        } else if (checkedRadioButtonId == R.id.rSdCard) {
            selected = "sdcard";

        }
        return selected;
    }


    public void fetchSpecificSubDir(String dir){
        String seletedRadio = getSelectedRadioGroup(radioGroup);
        if(seletedRadio =="phone"){
            currentDir = new File("/storage/emulated/0/"+dir+"/");
            fill(currentDir);
        }else if(seletedRadio =="sdcard") {
            currentDir = new File("/storage/sdcard1/"+dir+"/");
            fill(currentDir);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.download) {
            hearderTitle.setText("Downloads");
            fetchSpecificSubDir("Download");
        } else if (id == R.id.camera) {
            hearderTitle.setText("Camera");
            fetchSpecificSubDir("DCIM");
        } else if (id == R.id.screenshot) {
            hearderTitle.setText("Screenshots");
            fetchSpecificSubDir("Screenshot");
        } else if (id == R.id.pictures) {
            hearderTitle.setText("Pictures");
            fetchSpecificSubDir("Pictures");
        } else if (id == R.id.music) {
            hearderTitle.setText("Music");
            fetchSpecificSubDir("Music");
        } else if (id == R.id.videos) {
            hearderTitle.setText("Vidoes");
            fetchSpecificSubDir("Vidoe");
        } else if (id == R.id.documents) {
            hearderTitle.setText("Documents");
            fetchSpecificSubDir("Documents");
        } else if (id == R.id.whatsapp) {
            hearderTitle.setText("WhatsApp");
            fetchSpecificSubDir("WhatsApp/Media");
        }






        return true;

    }


}