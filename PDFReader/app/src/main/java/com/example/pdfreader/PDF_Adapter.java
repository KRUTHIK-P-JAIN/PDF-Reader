package com.example.pdfreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// to handle images in list view
public class PDF_Adapter extends ArrayAdapter<File> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<File> al_pdf;

    public PDF_Adapter(@NonNull Context context,ArrayList<File> al_pdf) {
        super(context, R.layout.adapter_pdf, al_pdf);

        this.context = context;
        this.al_pdf = al_pdf;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //counter
    @Override
    public int getViewTypeCount() {
       if(al_pdf.size() > 0)
           return al_pdf.size();
       else
           return 1;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf,parent,false);
            viewHolder = new ViewHolder();


            viewHolder.fileName = convertView.findViewById(R.id.pdfName);
            convertView.setTag(viewHolder);

            /*viewHolder.more = convertView.findViewById(R.id.more);
            viewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.fileName.setText(al_pdf.get(position).getName());

        return convertView;
    }

    public class ViewHolder{
        //ImageView more;
        TextView fileName;
    }
}
