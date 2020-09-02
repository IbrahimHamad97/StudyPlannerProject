package com.cmps312.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cmps312.studyplanner.model.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    final String TASKS_CHANNEL = "Tasks";
    final int CHANNEL_ID = 1;

    FirebaseFirestore db;

    final Calendar calendar = Calendar.getInstance();

    AlertDialog dialog;
    View myCustomLayoutView;

    EditText titleEdt;
    EditText shortDescEdt;
    EditText subTasksEdt;

    FloatingActionButton fab_main;
    FloatingActionButton fab_addTask;
    FloatingActionButton fab_addNote;
    FloatingActionButton fab_options;
    Animation fab_open, fab_close, fab_clock, fab_anticlock;
    Boolean isOpen = false;

    Button chooseDateBtn;
    Button chooseTimeBtn;
    Button cancelBtn;
    Button submitBtn;



    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Task> tasksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myCustomLayoutView = getLayoutInflater()
                .inflate(R.layout.custom_dialog
                , null, false);

        db=FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.taskRecyclerView);
        recyclerView.setHasFixedSize(true);
        tasksList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TaskAdapter(this,tasksList);
        recyclerView.setAdapter(adapter);

        dialog = new AlertDialog.Builder(this)
                .setView(myCustomLayoutView).create();

        titleEdt = myCustomLayoutView.findViewById(R.id.titleEdt);
        shortDescEdt = myCustomLayoutView.findViewById(R.id.shortDescMLEdt);
        subTasksEdt = myCustomLayoutView.findViewById(R.id.subTasksEdt);

        fab_main = findViewById(R.id.floatingActionButton1);
        fab_addTask = findViewById(R.id.floatingActionButton2);
        fab_addNote = findViewById(R.id.floatingActionButton3);
        fab_options = findViewById(R.id.floatingActionButton4);

        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anti_clockwise);

        chooseDateBtn = myCustomLayoutView.findViewById(R.id.dateBtn);
        chooseTimeBtn = myCustomLayoutView.findViewById(R.id.timeBtn);
        cancelBtn = myCustomLayoutView.findViewById(R.id.cancelBtn);
        submitBtn = myCustomLayoutView.findViewById(R.id.submitBtn);

        fab_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Open Menu", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        fab_addTask.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Add Task", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        fab_addNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Add Note", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        fab_options.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Options", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    fab_addTask.startAnimation(fab_close);
                    fab_addNote.startAnimation(fab_close);
                    fab_options.startAnimation(fab_close);
                    fab_main.startAnimation(fab_anticlock);
                    fab_addTask.setClickable(false);
                    fab_addNote.setClickable(false);
                    fab_options.setClickable(false);
                    isOpen = false;
                } else {
                    fab_addTask.startAnimation(fab_open);
                    fab_addNote.startAnimation(fab_open);
                    fab_options.startAnimation(fab_open);
                    fab_main.startAnimation(fab_clock);
                    fab_addTask.setClickable(true);
                    fab_addNote.setClickable(true);
                    fab_options.setClickable(true);
                    isOpen = true;
                }
            }
        });

        fab_addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStudyTask();
                Toast.makeText(MainActivity.this, "Adding Task Started", Toast.LENGTH_SHORT).show();
            }
        });

        fab_addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNoteBook();
                Toast.makeText(MainActivity.this, "Adding Notes Started", Toast.LENGTH_SHORT).show();
            }
        });

        fab_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptions();
                Toast.makeText(MainActivity.this, "Opening Options", Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        titleEdt.setText("");

        db.collection("tasks")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            tasksList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Task currTask = document.toObject(Task.class);
                                currTask.setId(document.getId());
                                tasksList.add(currTask);
                            }
                            adapter = new TaskAdapter(MainActivity.this,tasksList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.studyTasksItem:
                openStudyTask();
                return true;
            case R.id.notebookItem:
                openNoteBook();
                return true;
            case R.id.optionsItem:
                openOptions();
                return true;
            case R.id.exitItem:
                exitApplication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openStudyTask() {
        Intent intent = new Intent(MainActivity.this,AddTasks.class);
        intent.putExtra("mode","add");
        startActivity(intent);
    }

    private void openNoteBook() {
        Intent intent = new Intent(this, NotebookActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Going to Notebook",
                Toast.LENGTH_SHORT).show();
    }

    private void exitApplication() {
        Toast.makeText(this, "Thank you !",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        db.collection("tasks")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            tasksList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Task currTask = document.toObject(Task.class);
                                currTask.setId(document.getId());
                                tasksList.add(currTask);
                                getNotification(currTask.getTitle());
                            }
                            adapter = new TaskAdapter(MainActivity.this,tasksList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void openOptions() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Going to Options",
                Toast.LENGTH_SHORT).show();
    }

    private void addTask() {
        Toast.makeText(this, "task Added !", Toast.LENGTH_SHORT).show();
    }



    private void getNotification(String data) {
        createChannel();
        NotificationCompat.Builder n = new
                NotificationCompat.Builder(this, TASKS_CHANNEL);
        n.setSmallIcon(R.drawable.ic_launcher_foreground);
        n.setContentTitle("Task Added !");
        n.setContentText(data);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        n.addAction(R.drawable.ic_launcher_foreground, "Go to App", pendingIntent);
        n.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(CHANNEL_ID, n.build());
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nc = new NotificationChannel
                    (TASKS_CHANNEL, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(nc);
        }
    }

}
