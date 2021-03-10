package com.innovativesolutions.finder;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileChooser extends ListActivity implements  AdapterView.OnItemClickListener {

	GridView grid;
	private File currentDir;

    private com.innovativesolutions.finder.FileArrayAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		grid= (GridView) findViewById(R.id.grid);
        currentDir = new File("/sdcard/");
        fill(currentDir); 
    }
    private void fill(File f)
    {
    	File[]dirs = f.listFiles(); 
		 this.setTitle("Current Dir: "+f.getAbsolutePath());
		 List<com.innovativesolutions.finder.Item>dir = new ArrayList<com.innovativesolutions.finder.Item>();
		 List<com.innovativesolutions.finder.Item>fls = new ArrayList<com.innovativesolutions.finder.Item>();
		 try{
			 for(File ff: dirs)
			 { 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(ff.isDirectory()){
					
					
					File[] fbuf = ff.listFiles(); 
					int buf = 0;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0; 
					String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items";
					
					//String formated = lastModDate.toString();
					dir.add(new com.innovativesolutions.finder.Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon"));
				}
				else
				{
					
					fls.add(new com.innovativesolutions.finder.Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
				}
			 }
		 }catch(Exception e)
		 {    
			 
		 }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new com.innovativesolutions.finder.Item("..","Parent Directory","",f.getParent(),"directory_up"));
		 adapter = new com.innovativesolutions.finder.FileArrayAdapter(com.innovativesolutions.finder.FileChooser.this,R.layout.file_view,dir);


		grid.setAdapter(adapter);
    }





    private void onFileClick(com.innovativesolutions.finder.Item o)
    {
    	//Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        //setResult(RESULT_OK, intent);
        finish();
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


		com.innovativesolutions.finder.Item o = adapter.getItem(position);
		if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
			currentDir = new File(o.getPath());
			fill(currentDir);
		}
		else
		{
			onFileClick(o);
		}

	}



}
