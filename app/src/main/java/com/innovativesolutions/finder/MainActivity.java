package com.innovativesolutions.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.innovativesolutions.finder.fileUtil.MediaFile;
import com.innovativesolutions.finder.fileUtil.MimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Adapter.OnClickListener {

    private static final int REQUEST_PATH = 1;
    GridView grid;
    ListView list;
    private File currentDir;
    private long timeStamp;

    RadioButton phone, sdcard;
    RadioGroup radioGroup, listViewOptionRadioG;
    TextView hearderTitle;


    private RecyclerView recyclerView=null;
    public Adapter adapter=null;
    private ArrayList<Item> dir;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view); // Adding Left Nav View
        navigationView.setNavigationItemSelectedListener(this);
        // Radio btn for select phone or SD
        phone = (RadioButton) findViewById(R.id.rPhone);
        sdcard = (RadioButton) findViewById(R.id.rSdCard);
        phone.setChecked(true);
        radioGroup = (RadioGroup) findViewById(R.id.rdGroup);
        // Header Tile
        hearderTitle = findViewById(R.id.header_title);
        hearderTitle.setText("Phone");
       /* //  Grid view
        grid = (GridView) findViewById(R.id.grid);
        // LIst VIew
        list = (ListView) findViewById(R.id.listView);*/

        // to know the file system path
        File f3[] = getApplicationContext().getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);

        // set phone storage on app firt time launches
        currentDir = new File("/storage/emulated/0/");
        fill(currentDir);









    }



        private void fill(File f) {

        TextView absolutePath = findViewById(R.id.filePath);
        File[] dirs = f.listFiles();
        absolutePath.setText(" => : " + f.getAbsolutePath());
           dir = new ArrayList<Item>();
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
                    fls.add(new com.innovativesolutions.finder.Item(ff.getName(), convertByteToMb(ff.length()), date_modify, ff.getAbsolutePath(), "file_icon"));
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new com.innovativesolutions.finder.Item("..", "Parent Directory", "", f.getParent(), "directory_up"));


            recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager(manager);
            adapter=new Adapter(dir,MainActivity.this,this);
            recyclerView.setAdapter(adapter);


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



    public void fetchSpecificSubDir(String dir) {
        String seletedRadio = getSelectedRadioGroup(radioGroup);
        if (seletedRadio == "phone") {
            hearderTitle.setText("Phone - " + dir);
            currentDir = new File("/storage/emulated/0/" + dir + "/");
            fill(currentDir);
        } else if (seletedRadio == "sdcard") {
            hearderTitle.setText("SD Card - " + dir);
            currentDir = new File("/storage/sdcard1/" + dir + "/");
            fill(currentDir);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.download) {
            fetchSpecificSubDir("Download");
        } else if (id == R.id.camera) {
            fetchSpecificSubDir("DCIM");
        } else if (id == R.id.screenshot) {
            fetchSpecificSubDir("Screenshot");
        } else if (id == R.id.pictures) {
            fetchSpecificSubDir("Pictures");
        } else if (id == R.id.music) {
            fetchSpecificSubDir("Music");
        } else if (id == R.id.videos) {
            fetchSpecificSubDir("Vidoe");
        } else if (id == R.id.documents) {
            fetchSpecificSubDir("Documents");
        } else if (id == R.id.whatsapp) {
            fetchSpecificSubDir("WhatsApp/Media");
        }


        return true;

    }

    public String convertByteToMb(long size) {
        String hrSize = "";
        double m = size / 1048576.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }


    @Override
    public void onBackPressed() {
        String parent = currentDir.getParent() != null ? currentDir.getParent() : null;
        if (parent != null) {
            currentDir = new File(currentDir.getParent());
            fill(currentDir);
        } else {
            Toast.makeText(getApplicationContext(), "AT THE ROOT Folder, press one more back to Exit App", Toast.LENGTH_SHORT).show();
            if (System.currentTimeMillis() - timeStamp < 200) {
                super.onBackPressed();
            }
            timeStamp = System.currentTimeMillis();
        }
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







    public void createNewFolder(View view)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText m_edtinput = new EditText(this);
        // Specify the type of input expected;
        m_edtinput.setInputType(InputType.TYPE_CLASS_TEXT);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_text = " ";
                m_text = m_edtinput.getText().toString();

                Log.d("cur dir", currentDir.getAbsolutePath());

                File m_newPath = new File(currentDir, m_text);
                if (!m_newPath.exists()) {
                    m_newPath.mkdirs();
                }else {
                    // give toast folder already create with the name.
                    Toast.makeText(getApplicationContext(), "Folder already creaetd.", Toast.LENGTH_SHORT).show();

                }
                fill(currentDir);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(m_edtinput);
        builder.show();
    }




    @Override
    public void onClickListener(int position, Item model, String element) {

        if(element == "textView"){

            try{
                dir.set(position,model);
            }catch (Exception e){

            }

        }else if(element == "image"){
            com.innovativesolutions.finder.Item o = adapter.getItem(position);

            System.out.println("---- ONCLICK ------");

            if (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up")) {
                currentDir = new File(o.getPath());
                fill(currentDir);
            } else {
                File temp_file = new File(o.getPath());
                String mimeType = MediaFile.getMimeTypeForFile(temp_file.toString()); //getContentResolver().getType(Uri.parse("file://" + temp_file));

                String fileName = temp_file.getName();
                int dotposition = fileName.lastIndexOf(".");
                String file_Extension = "";
                if (dotposition != -1) {
                    String filename_Without_Ext = fileName.substring(0, dotposition);
                    file_Extension = fileName.substring(dotposition + 1, fileName.length());
                }
                if (mimeType == null) {
                    mimeType = MimeUtils.guessMimeTypeFromExtension(file_Extension);
                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + temp_file), mimeType);
                ResolveInfo info = getPackageManager().resolveActivity(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                if (info != null) {
                    //startActivity(Intent.createChooser(intent, "Complete action using"));
                    startActivity(intent);
                }
            }

        }else {

        }


    }
}






