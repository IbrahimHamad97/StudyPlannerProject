package com.cmps312.studyplanner;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmps312.studyplanner.model.Subtask;
import com.cmps312.studyplanner.model.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyTaskViewHolder> {
    private Context context;
    private ArrayList<Task> tasks;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    public static class MyTaskViewHolder extends RecyclerView.ViewHolder{

        private TextView titleTv;
        private TextView dateTv;
        private TextView timeTv;
        private CheckBox checkBox;
        private TextView priorityTv;
        private TextView noSubtasksTv;
        private TextView showMoreTv;
        private ImageView syncIv;
        private ImageView deleteIv;

        public MyTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.titleTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            checkBox = itemView.findViewById(R.id.checkBox);
            noSubtasksTv = itemView.findViewById(R.id.noSubtaskTv);
            priorityTv = itemView.findViewById(R.id.priorityTv);
            showMoreTv = itemView.findViewById(R.id.showMoreTv);
            syncIv = itemView.findViewById(R.id.syncImg);
            deleteIv = itemView.findViewById(R.id.deleteIv);
        }
    }


    @NonNull
    @Override
    public MyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks_list_item,parent,false);
        return new MyTaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyTaskViewHolder holder, final int position) {

        final String title = tasks.get(position).getTitle();
        final Boolean status = tasks.get(position).getStatus();
        final String priority = tasks.get(position).getPriority();
        final String date = tasks.get(position).getDate();
        final String time = tasks.get(position).getTime();
        final ArrayList<Subtask> subtasks = tasks.get(position).getSubTasks();

        holder.titleTv.setText(title);
        holder.priorityTv.setText(priority);
        holder.dateTv.setText(date);
        holder.timeTv.setText(time);
        holder.noSubtasksTv.setText(subtasks.size()+"");

        if(priority.equals("High"))
            holder.priorityTv.setTextColor(Color.RED);
        else if(priority.equals("Medium"))
            holder.priorityTv.setTextColor(Color.parseColor("#ffa500"));
        else
            holder.priorityTv.setTextColor(Color.GREEN);

        if(status){
            holder.checkBox.setChecked(true);
            holder.itemView.setBackgroundColor(Color.GRAY);
            holder.titleTv.setPaintFlags(holder.titleTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.checkBox.setChecked(false);
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.titleTv.setPaintFlags(holder.titleTv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()) {
                    holder.itemView.setBackgroundColor(Color.GRAY);
                    tasks.get(position).setStatus(true);
                    db.collection("tasks").document(tasks.get(position).getId()).update("status",true);
                    holder.titleTv.setPaintFlags(holder.titleTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                }
                else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                    tasks.get(position).setStatus(false);
                    db.collection("tasks").document(tasks.get(position).getId()).update("status",false);
                    holder.titleTv.setPaintFlags(holder.titleTv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                }
            }
        });
        holder.syncIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE,title);

                String[] subDate = date.split("/");
                String[] subTime = time.split(":");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(subDate[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(subDate[1])-1);
                cal.set(Calendar.YEAR, Integer.parseInt(subDate[2]));
                cal.set(Calendar.HOUR_OF_DAY, 00);
                cal.set(Calendar.MINUTE, 00);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTime().getTime());
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(subTime[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(subTime[1]));
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTime().getTime());
                context.startActivity(intent);
            }
        });
        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("tasks").document(tasks.get(position).getId()).delete();
                                tasks.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        holder.showMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AddTasks.class);
                intent.putExtra("mode","edit");
                intent.putExtra("id",tasks.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }




}
