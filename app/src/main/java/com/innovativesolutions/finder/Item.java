package com.innovativesolutions.finder;

public class Item implements Comparable<Item>{
	private String name;
	private String data;
	private String date;
	private String path;
	private String image;
	private String fileExt;

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public boolean isSelect() {
		return isSelect;
	}

	private boolean isSelect;



	public void setName(String name) {
		this.name = name;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}





	public Item(String n, String d, String dt, String p, String img, String ext)
	{
		name = n;
		data = d;
		date = dt;
		path = p; 
		image = img;
		fileExt = ext;
		
	}
	public String getName()
	{
		return name;
	}
	public String getData()
	{
		return data;
	}
	public String getDate()
	{
		return date;
	}
	public String getPath()
	{
		return path;
	}
	public String getImage() {
		return image;
	}




	
	public int compareTo(com.innovativesolutions.finder.Item o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase()); 
		else 
			throw new IllegalArgumentException();
	}


}
