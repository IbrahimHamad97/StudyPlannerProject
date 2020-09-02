package com.cmps312.studyplanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmps312.studyplanner.model.Subtask;

import java.util.ArrayList;
import java.util.Calendar;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Subtask> subtaskList;


    public SubTaskAdapter(Context context, ArrayList<Subtask> subtaskList) {
        this.context = context;
        this.subtaskList = subtaskList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private CheckBox subtaskCB;
        private ImageView deleteIv;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subtaskCB = itemView.findViewById(R.id.subtaskCB);
            deleteIv = itemView.findViewById(R.id.deleteIv);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subtask_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Boolean status = subtaskList.get(position).getStatus();
        holder.subtaskCB.setText(subtaskList.get(position).getTitle());
        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtaskList.remove(position);
                notifyDataSetChanged();
            }
        });
        if(status){
            holder.subtaskCB.setChecked(true);
            holder.itemView.setBackgroundColor(Color.GRAY);

        }
        else{
            holder.subtaskCB.setChecked(false);
            holder.itemView.setBackgroundColor(Color.WHITE);

        }
        holder.subtaskCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()) {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                    subtaskList.get(position).setStatus(true);

                }
                else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                    subtaskList.get(position).setStatus(false);

                }
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return subtaskList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }


}
