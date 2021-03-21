package com.innovativesolutions.finder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {



    public interface OnClickListener{
        void onClickListener(int position,Item model,String element);
    }



     ArrayList<Item> models;
     Context mContext;
    OnClickListener onClickListener;


    public Adapter(ArrayList<Item> models, Context mContext ,OnClickListener onClickListener) {
        this.models = models;
        this.mContext = mContext;
        this.onClickListener = onClickListener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_view,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {



        Item model=models.get(i);

        if(model!=null){

            System.out.println("model.getFileExt() "+ model.getFileExt());

            viewHolder.text01.setText(model.getName());
            viewHolder.text02.setText(model.getData());
            viewHolder.text03.setText(model.getDate());
            viewHolder.textExt.setText(model.getFileExt());
            viewHolder.position=i;


            if (model.getFileExt().toLowerCase().contains("jpg") || model.getFileExt().toLowerCase().contains("png")) {
                Glide.with(mContext)
                        .load(new File(model.getPath()))
                        .centerCrop()
                        .into(viewHolder.image);

            } else if (model.getFileExt().toLowerCase().contains("mp3")) {

                String uri = "drawable/ic_mp3";
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable image = mContext.getDrawable(imageResource);
                viewHolder.image.setImageDrawable(image);

            } else if (model.getFileExt().toLowerCase().contains("mp4")) {

                String uri = "drawable/ic_mp4";
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable image = mContext.getDrawable(imageResource);
                viewHolder.image.setImageDrawable(image);

            } else if (model.getFileExt().toLowerCase().contains("pdf")) {

                String uri = "drawable/ic_pdf1";
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable image = mContext.getDrawable(imageResource);
                viewHolder.image.setImageDrawable(image);

            } else if (model.getFileExt().toLowerCase().contains("apk")) {
                String uri = "drawable/ic_apk";
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable image = mContext.getDrawable(imageResource);
                viewHolder.image.setImageDrawable(image);

            } else {
                String uri = "drawable/" + model.getImage();
                int imageResource = mContext.getResources().getIdentifier(uri, null, mContext.getPackageName());
                Drawable image = mContext.getDrawable(imageResource);
                viewHolder.image.setImageDrawable(image);
            }



            if(model.isSelect()){
                viewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.purple_200));
            }
            else{
                viewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }


        }
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item model1=models.get(i);

                System.out.println("models.get(i).getImage() :"+ models.get(i).getImage() +":");

                if(!(models.get(i).getImage() == "directory_up")){
                    if(model1.isSelect()){
                        model1.setSelect(false);
                    }else{
                        model1.setSelect(true);
                    }
                    models.set(viewHolder.position,model1);
                    if(onClickListener!=null){
                        onClickListener.onClickListener(viewHolder.position,model1,"Itemview");
                    }
                    notifyItemChanged(viewHolder.position);
                }

            }

        });


        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item model1=models.get(i);

                models.set(viewHolder.position,model1);
                if(onClickListener!=null){
                    onClickListener.onClickListener(viewHolder.position,model1,"image");
                }
                notifyItemChanged(viewHolder.position);


            }
        });





    }

    private void onClickLogic(){



    }


    public void setModel(ArrayList<Item> models){
        this.models=models;
    }

    @Override
    public int getItemCount() {
        if(models!=null){
            return models.size();
        }
        return 0;
    }


    public Item getItem(int i) {
        return models.get(i);
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text01;
        public TextView text02;
        public TextView text03;
        public TextView textExt;
        public View view;
        public int position;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            image=itemView.findViewById(R.id.fd_Icon1);
            text01=itemView.findViewById(R.id.TextView01);
            text02=itemView.findViewById(R.id.TextView02);
            text03=itemView.findViewById(R.id.TextViewDate);
            textExt=itemView.findViewById(R.id.TextViewExt);
        }



    }
}
