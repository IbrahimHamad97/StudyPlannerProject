package com.cmps312.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cmps312.studyplanner.model.Subtask;
import com.cmps312.studyplanner.model.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTasks extends AppCompatActivity {

    EditText titleEt;
    EditText subtaskEt;
    TextView dateEt;
    TextView timeEt;
    TextView headerTv;
    RadioButton lowRB;
    RadioButton mediumRB;
    RadioButton highRB;
    Button cancelBtn;
    Button resetBtn;
    Button submitBtn;
    Button setDateBtn;
    Button setTimeBtn;
    Button addSubBtn;
    Calendar calendar;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    String TAG = "ERROR";
    String mode;
    String id;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Subtask> subtaskList;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tasks);
        headerTv = findViewById(R.id.headerTv);
        titleEt = findViewById(R.id.titleEt);
        dateEt = findViewById(R.id.dateET);
        timeEt = findViewById(R.id.timeET);
        lowRB = findViewById(R.id.lowRadioBtn);
        mediumRB = findViewById(R.id.mediumRadioBtn);
        highRB = findViewById(R.id.highRadioBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        resetBtn = findViewById(R.id.resetBtn);
        submitBtn = findViewById(R.id.submitBtn);
        setDateBtn = findViewById(R.id.setDateBtn);
        setTimeBtn = findViewById(R.id.setTimeBtn);
        addSubBtn = findViewById(R.id.addSubBtn);
        subtaskEt = findViewById(R.id.subTaskEt);
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.subTaskRv);

        subtaskList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SubTaskAdapter(this,subtaskList);
        recyclerView.setAdapter(adapter);

        addSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subtask = subtaskEt.getText().toString();
                Subtask tempSubtask = new Subtask(subtask,false);
                subtaskList.add(tempSubtask);
                adapter.notifyDataSetChanged();
                subtaskEt.setText("");
            }
        });

        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar=Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                dpd = new DatePickerDialog(AddTasks.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        dateEt.setText(mDay+"/"+(mMonth+1)+"/"+mYear);
                    }
                },day,month,year);
                dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                dpd.show();
            }
        });

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar=Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                tpd = new TimePickerDialog(AddTasks.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int min) {
                        timeEt.setText(hourOfDay+":"+(min<10?"0"+min:min));
                    }
                },hour,minute,false);
                tpd.show();
            }
        });

        mode = getIntent().getStringExtra("mode");
        if(mode.equals("edit")){
            id = getIntent().getStringExtra("id");
            DocumentReference docRef = db.collection("tasks").document(id);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Task task = documentSnapshot.toObject(Task.class);
                    headerTv.setText("Edit Task");
                    titleEt.setText(task.getTitle());
                    if(task.getPriority().equals("Low"))
                        lowRB.setChecked(true);
                    else if(task.getPriority().equals("Medium"))
                        mediumRB.setChecked(true);
                    else
                        highRB.setChecked(true);
                    dateEt.setText(task.getDate());
                    timeEt.setText(task.getTime());
                    subtaskList = task.getSubTasks();
                    adapter = new SubTaskAdapter(AddTasks.this,subtaskList);
                    recyclerView.setAdapter(adapter);
                }
            });
        }


    }

    public void clearFields(View view) {
        titleEt.setText("");
        lowRB.setChecked(false);
        mediumRB.setChecked(false);
        highRB.setChecked(false);
        dateEt.setText("");
        timeEt.setText("");
        subtaskList.clear();
        adapter.notifyDataSetChanged();
    }

    public void cancel(View view) {
        Intent intent = new Intent(AddTasks.this,MainActivity.class);
        startActivity(intent);
    }

    public void submit(View view) {
        String title;
        boolean status=false;
        String priority;
        String date;
        String time;

        title = titleEt.getText().toString();

        if(lowRB.isChecked())
            priority="Low";
        else if(mediumRB.isChecked())
            priority="Medium";
        else if(highRB.isChecked())
            priority="High";
        else
            priority="Low";

        date=dateEt.getText().toString();
        time=timeEt.getText().toString();

        Task task = new Task(title,priority,date,time,status,subtaskList);
        if(mode.equals("add")) {
            db.collection("tasks")
                    .add(task)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            Intent intent = new Intent(AddTasks.this, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }else if(mode.equals("edit")){
            db.collection("tasks").document(id)
                    .set(task)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Intent intent = new Intent(AddTasks.this, MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }
    }
}
