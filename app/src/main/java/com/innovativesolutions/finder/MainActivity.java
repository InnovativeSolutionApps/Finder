package com.innovativesolutions.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.innovativesolutions.finder.fileUtil.MediaFile;
import com.innovativesolutions.finder.fileUtil.MimeUtils;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Adapter.OnClickListener , TextWatcher {

    private File currentDir;
    private long timeStamp;

    RadioButton phone, sdcard;
    RadioGroup radioGroup;
    TextView hearderTitle;
    public  TextView itemCountView;
    EditText searchFilesEdit;
    CheckBox selectAllBox;

    private RecyclerView recyclerView=null;
    public Adapter adapter=null;
    private ArrayList<Item> dir;

    public ArrayList<Item> copiedItems = new ArrayList<>();
    public ArrayList<Item> shareItems = new ArrayList<>();
    public ArrayList<Item> deleteItems = new ArrayList<>();


    public static final int STORAGE_PERMISSION_REQUEST_CODE= 1;
    public static final int STORAGE_PERMISSION_WRITE_REQUEST_CODE= 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // launch app in Portrait only

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
        searchFilesEdit = findViewById(R.id.searchFiles);
        searchFilesEdit.addTextChangedListener(this);
        selectAllBox = findViewById(R.id.selectAll);
        itemCountView = findViewById(R.id.itemCountAll);

        currentDir = new File("/storage/emulated/0/");

        askSDCardReadPermissions();
        askSDCardWritePermissions();



    }


    private void askSDCardReadPermissions() {

        int permissionCheckStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        // we already asked for permisson & Permission granted
        if (permissionCheckStorage == PackageManager.PERMISSION_GRANTED) {
            //do what you want
            fill(currentDir);
            //System.out.println("??????? GIven : READ_EXTERNAL_STORAGE ");

        } else {
            // if storage request is denied
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You need to give permission to access storage in order to work this feature.");
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        // Show permission request popup
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                STORAGE_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.show();

            }
            //asking permission for first time
            else {
                // Show permission request popup for the first time
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE);

            }

        }
    }

private void askSDCardWritePermissions() {

    int permissionCheckStorage = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);

    // we already asked for permisson & Permission granted
    if (permissionCheckStorage == PackageManager.PERMISSION_GRANTED) {
        //do what you want
       // System.out.println("??????? GIven : WRITE_EXTERNAL_STORAGE ");

    } else {

        // if storage request is denied
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You need to give permission to access storage in order to work this feature.");
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("GIVE PERMISSION", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    // Show permission request popup
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_WRITE_REQUEST_CODE);
                }
            });
            builder.show();

        }
        //asking permission for first time
        else {
            // Show permission request popup for the first time
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_WRITE_REQUEST_CODE);

        }

    }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // check whether storage permission granted or not.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //do what you want;
                        //System.out.println("#######READ#######");
                    }
                }
                break;
            case STORAGE_PERMISSION_WRITE_REQUEST_CODE:
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // check whether storage permission granted or not.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //do what you want;
                        //System.out.println("######WRITE########");
                    }
                }
                break;
            default:
                break;
        }


    }

    private void fill(File f) {

        TextView absolutePath = findViewById(R.id.filePath);
        absolutePath.setText(" : " + f.getAbsolutePath());

        File[] dirs = f.listFiles();

        dir = new ArrayList<>();
        List<com.innovativesolutions.finder.Item> fls = new ArrayList<com.innovativesolutions.finder.Item>();
        try {
            for (File ff : dirs) {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);

                String fileName = ff.getName();
                int dotposition = fileName.lastIndexOf(".");
                String file_Extension = "";
                if (dotposition != -1) {
                    file_Extension = fileName.substring(dotposition + 1);
                }


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
                    dir.add(new com.innovativesolutions.finder.Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(), "directory_icon",""));
                } else {
                    fls.add(new com.innovativesolutions.finder.Item(ff.getName(), convertByteToMb(ff.length()), date_modify, ff.getAbsolutePath(), "file_icon",file_Extension));
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new com.innovativesolutions.finder.Item("..", "Parent Directory", "", f.getParent(), "directory_up",""));

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
            itemCountView.setText( "0 of 0" );
            fetchSpecificSubDir("Download");
        } else if (id == R.id.camera) {
            itemCountView.setText( "0 of 0" );
            fetchSpecificSubDir("DCIM");
        } else if (id == R.id.screenshot) {
            itemCountView.setText( "0 of 0" );
            fetchSpecificSubDir("Screenshot");
        } else if (id == R.id.pictures) {
            itemCountView.setText( "0 of 0" );
            fetchSpecificSubDir("Pictures");
        } else if (id == R.id.music) {
            itemCountView.setText( "0 of 0" );
            fetchSpecificSubDir("Music");
        } else if (id == R.id.videos) {
            itemCountView.setText( "0 of 0" );
            fetchSpecificSubDir("Video");
        } else if (id == R.id.documents) {
            itemCountView.setText( "0 of 0" );
            fetchSpecificSubDir("Documents");
        } else if (id == R.id.whatsapp) {
            itemCountView.setText( "0 of 0" );
            fetchSpecificSubDir("WhatsApp/Media");
        }
        else if (id == R.id.phone) {
            itemCountView.setText( "0 of 0" );
            currentDir = new File("/storage/emulated/0/");
            hearderTitle.setText("Phone");
            fill(currentDir);
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

        itemCountView.setText( "0 of 0" );
        String parent = currentDir.getParent();
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




    public void createNewFolder(View view1)
    {

        LayoutInflater layoutInflater= MainActivity.this.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.dialog_group_name, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Folder Name");
        builder.setCancelable(false);

        // Set up the input
        final EditText m_edtinput = (EditText) view.findViewById(R.id.edt_groupName);
        // Specify the type of input expected;
        m_edtinput.setInputType(InputType.TYPE_CLASS_TEXT);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String m_text = " ";
                m_text = m_edtinput.getText().toString().trim();
                Log.d("cur dir", currentDir.getAbsolutePath());

                if(m_text.length() > 0){
                    File m_newPath = new File(currentDir, m_text);
                    if (!m_newPath.exists()) {
                        m_newPath.mkdirs();
                    }else {
                        // give toast folder already create with the name.
                        Toast.makeText(getApplicationContext(), "Folder already creaetd.", Toast.LENGTH_SHORT).show();
                    }
                    fill(currentDir);
                }else {
                    Toast.makeText(getApplicationContext(), "Please enter Folder name..", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(view);
        builder.show();
    }




    @Override
    public void onClickListener(int position, Item model, String element,ArrayList<Item> models) {
           ArrayList<Item> items = new ArrayList<Item>();

        com.innovativesolutions.finder.Item o = adapter.getItem(position);

        if(element == "Itemview"){

            for(int i=0;i<models.size();i++){
                if(models.get(i).isSelect()){
                    items.add(models.get(i));
                }
            }
            itemCountView.setText(  Integer.toString(items.size()) + " of " + Integer.toString(models.size() - 1));

            if (!(o.getImage() == "directory_up")) {
                try{
                    dir.set(position,model);
                }catch (Exception e){
                }
            }

        }else if(element == "image"){

            if (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up")) {
                currentDir = new File(o.getPath());
                itemCountView.setText( "0 of 0" );
                fill(currentDir);
            } else {
                File temp_file = new File(o.getPath());
                String mimeType = MediaFile.getMimeTypeForFile(temp_file.toString()); //getContentResolver().getType(Uri.parse("file://" + temp_file));


                String fileName = temp_file.getName();
                int dotposition = fileName.lastIndexOf(".");
                String file_Extension = "";
                if (dotposition != -1) {
                    String filename_Without_Ext = fileName.substring(0, dotposition);
                    file_Extension = fileName.substring(dotposition + 1);
                }


                if (mimeType == null) {
                    mimeType = MimeUtils.guessMimeTypeFromExtension(file_Extension);
                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",temp_file);
                intent.setDataAndType(uri, mimeType);
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

 public void deletedSelectedListItem(View view){
     itemCountView.setText( "0 of 0" );
     try {
         for(int i=0;i<dir.size();i++){
             if(dir.get(i).isSelect()){
                 deleteItems.add(dir.get(i));
             }
         }
         if(deleteItems.size() > 0){
             for(int i=0;i<dir.size();i++){
                 if(dir.get(i).isSelect()){
                     File file =  new File(dir.get(i).getPath());

                     if(file.isDirectory()){

                         File[] files = file.listFiles();

                         for(File f:files){

                             f.delete();
                             if(f.exists()){
                                 f.getCanonicalFile().delete();
                                 if(f.exists()){
                                     getApplicationContext().deleteFile(f.getName());
                                 }
                             }

                         }
                         FileUtils.deleteDirectory(file);

                     }else {
                         file.delete();
                         if(file.exists()){
                             file.getCanonicalFile().delete();
                             if(file.exists()){
                                 getApplicationContext().deleteFile(file.getName());
                             }
                         }
                     }
                 }
             }
             removedSelectedListItem();
             Toast.makeText(getApplicationContext(), "Deleted...", Toast.LENGTH_SHORT).show();

         }else {
             Toast.makeText(getApplicationContext(), "Selected Items : 0 ", Toast.LENGTH_SHORT).show();
         }
         deleteItems = new ArrayList<>();

     }
     catch (Exception e) {
         Log.e("tag", e.getMessage());
     }
 }


 public void copyListItems(View view){
     itemCountView.setText( "0 of 0" );
     for(int i=0;i<dir.size();i++){
         if(dir.get(i).isSelect()){
             copiedItems.add(dir.get(i));
         }
     }
     restoreList();
     if(copiedItems.size() > 0){
         Toast.makeText(getApplicationContext(), "Copied...", Toast.LENGTH_SHORT).show();
     }else {
         Toast.makeText(getApplicationContext(), "Selected Items : 0 ", Toast.LENGTH_SHORT).show();
     }

 }



    public void pasteListItems(View view) throws IOException {
        if(copiedItems.size() > 0){
            for(int i=0;i<copiedItems.size();i++){
                copyFileOrDirectory(copiedItems.get(i).getPath(),currentDir.getAbsolutePath());
            }
            copiedItems = new ArrayList<>();
            fill(currentDir);
            Toast.makeText(getApplicationContext(), "Pasted...", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(getApplicationContext(), "Selected Items : 0 ", Toast.LENGTH_SHORT).show();
        }

    }



    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {
                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

     //   if (!destFile.exists()) {
//           destFile.createNewFile();
    //    }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


// used in delete list item methiod
    private void removedSelectedListItem() {
        ArrayList<Item> temp=new ArrayList<>();
        try{
            for(int i=0;i<dir.size();i++){
                if(!dir.get(i).isSelect()){
                    temp.add(dir.get(i));
                }
            }

        }catch (Exception e){

        }
        dir=temp;
        if(dir.size()==0){
            recyclerView.setVisibility(View.GONE);
        }
        adapter.setModel(dir);
        adapter.notifyDataSetChanged();
    }


    private void restoreList(){
        try{
            for(int i=0;i<dir.size();i++){
                if(dir.get(i).isSelect()){
                    dir.get(i).setSelect(false);
                }
            }

        }catch (Exception e){

        }
        if(dir.size()==0){
            recyclerView.setVisibility(View.GONE);
        }
        adapter.setModel(dir);
        adapter.notifyDataSetChanged();
    }


    public void searchFilesByName(String fName) {

        String query = fName;
        final Pattern patternFullMatch = Pattern.compile(query, Pattern.MULTILINE);
        fill(currentDir);
        if (query.length() > 0) {

            ArrayList<Item> temp = new ArrayList<>();
            try {
                for (int i = 0; i < dir.size(); i++) {
                    Matcher matcherFullMatch = patternFullMatch.matcher(dir.get(i).getName().toLowerCase());
                    if (matcherFullMatch.find()) {
                        temp.add(dir.get(i));
                    }
                }
            } catch (Exception e) {

            }
            dir = temp;
            adapter.setModel(dir);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "Enter File Name...", Toast.LENGTH_SHORT).show();
            fill(currentDir);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String query = searchFilesEdit.getText().toString().toLowerCase();
        searchFilesByName(query);
    }

    public void clearSearchText(View view) {
        searchFilesEdit.setText("");
    }


    public void shareFiles(View view){

        ArrayList<Uri> uriList = new ArrayList<>();
        for(int i=0;i<dir.size();i++){
            if(dir.get(i).isSelect()){
                shareItems.add(dir.get(i));
            }
        }
        restoreList();

        if(shareItems.size() > 0){
            // prepare for share
            for (int i = 0; i < shareItems.size(); i++) {
                Item o = shareItems.get(i);
                if (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up")) {
                    File temp_file = new File(o.getPath());
                    if(temp_file.isDirectory()){
                        //Get all files in this particular location
                        File[] filesToSend = temp_file.listFiles();
                        ArrayList<Uri> files = new ArrayList<Uri>();
                        for (File file : filesToSend) {
                            //Uri uri = Uri.fromFile(file);
                            Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);
                            files.add(uri);
                        }
                        uriList.addAll(files);
                    }
                }else {
                    File temp_file = new File(o.getPath());
                    Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",temp_file);
                    uriList.add(uri);
                }
            }

            // intent
            shareItems = new ArrayList<>();
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            startActivity(Intent.createChooser(intent, "Share Files"));

        }else {
            Toast.makeText(getApplicationContext(), "Selected Items : 0 ", Toast.LENGTH_SHORT).show();
        }

        }

        public void onAllCheckboxClicked(View view){
            try{
                if(!selectAllBox.isChecked()){
                    for(int i=0;i<dir.size();i++){

                        Item o = dir.get(i);
                        if (!(o.getImage() == "directory_up")) {

                            if (dir.get(i).isSelect()) {
                                dir.get(i).setSelect(false);
                            }

                        }
                    }
                    itemCountView.setText( "0 of 0" );
                } else{
                    for(int i=0;i<dir.size();i++){

                        Item o = dir.get(i);
                        if (!(o.getImage() == "directory_up")) {
                            if (!dir.get(i).isSelect()) {
                                dir.get(i).setSelect(true);
                            }
                        }
                    }
                    itemCountView.setText(Integer.toString(dir.size() - 1) + " of " + Integer.toString(dir.size() - 1));
                }
            }catch (Exception e){
            }
            if(dir.size()==0){
                recyclerView.setVisibility(View.GONE);
            }
            adapter.setModel(dir);
            adapter.notifyDataSetChanged();
        }

}