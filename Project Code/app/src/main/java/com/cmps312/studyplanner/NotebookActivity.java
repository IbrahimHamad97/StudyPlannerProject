package com.cmps312.studyplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmps312.studyplanner.db.StudyContract;
import com.cmps312.studyplanner.db.StudyDao;
import com.cmps312.studyplanner.model.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotebookActivity extends AppCompatActivity implements NoteListFragment.OnListFragmentInteractionListener {

    final String NOTES_CHANNEL = "Notes";
    final int CHANNEL_ID = 2;

    private static final String LAST_NOTE_KEY = "lastnote";
    private Note note;
    private EditText titleEt, contentEt;
    private TextView dateTv;
    private FrameLayout fragmentContainer;
    private StudyDao studyDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        studyDao = new StudyDao(this);
        titleEt = findViewById(R.id.note_title_et);
        contentEt = findViewById(R.id.note_content_et);
        dateTv = findViewById(R.id.note_date_tv);
        fragmentContainer = findViewById(R.id.fragment_container);

        openLastNote();
    }

    private void sendNoteList() {
        NoteListFragment nlf = NoteListFragment.newInstance(studyDao.getNotes());
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_container, nlf).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notebook_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_add_note:
                createNote();
                break;
            case R.id.item_delete_note:
                deleteNote();
                break;
            case R.id.item_open_note_list:
                toggleNoteList();
                break;
            case R.id.item_save_note:
                updateNote();
                Toast.makeText(this, getString(R.string.toast_note_save),
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void populateNotePage() {
        if (note.getDate() != null)
            dateTv.setText(note.getDate());
        if (note.getTitle() != null)
            titleEt.setText(note.getTitle());
        if (note.getContent() != null)
            contentEt.setText(note.getContent());
    }

    private void setLastNote(int id) {
        SharedPreferences.Editor spe = getSharedPreferences(LAST_NOTE_KEY, MODE_PRIVATE).edit();
        spe.putInt(StudyContract.NotesTable.COL_ID, id);
        spe.apply();
    }

    private void openLastNote() {
        int id = getSharedPreferences(LAST_NOTE_KEY, MODE_PRIVATE)
                .getInt(StudyContract.NotesTable.COL_ID, -1);
        if (id != -1) {
            note = studyDao.getNote(id);
            populateNotePage();
        }
    }

    private void createNote() {
        if (note != null)
            updateNote();

        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        note = new Note("untitled", dateFormat.format(cal.getTime()), "");
        note.setId((int) studyDao.addNote(note));
        if (fragmentContainer.getVisibility() == View.VISIBLE)
            sendNoteList();
        populateNotePage();
    }


    public void deleteNote() {
        updateNote();
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title_delete_note))
                .setMessage(getString(R.string.delete_note_msg, note.getTitle()))
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        studyDao.deleteNote(note.getId());
                        note = studyDao.getLatestNote();
                        if (note != null) {
                            populateNotePage();
                            setLastNote(note.getId());
                            if (fragmentContainer.getVisibility() == View.VISIBLE)
                                sendNoteList();
                        } else {
                            titleEt.setText("");
                            dateTv.setText("");
                            contentEt.setText("");
                        }
                        Toast.makeText(NotebookActivity.this,
                                getString(R.string.Toast_delete_confirm),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCEL", null).show();

    }

    public void toggleNoteList() {
        if (fragmentContainer.getVisibility() == View.GONE) {
            updateNote();
            sendNoteList();
            fragmentContainer.setVisibility(View.VISIBLE);
        } else
            fragmentContainer.setVisibility(View.GONE);
    }

    //TODO Save note to db on pause
    @Override
    protected void onPause() {
        updateNote();
        super.onPause();
    }

    private void updateNote() {
        if (note != null) {
            note.setTitle(titleEt.getText().toString());
            note.setContent(contentEt.getText().toString());
            studyDao.updateNote(note);
            getNotification(note);
        }
        if (fragmentContainer.getVisibility() == View.VISIBLE)
            sendNoteList();
    }

    @Override
    protected void onDestroy() {
        if (note != null)
            setLastNote(note.getId());
        else
            Toast.makeText(this, "there are no notes", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void getNoteItem(Note note) {
        if (this.note != null)
            updateNote();
        this.note = note;
        setLastNote(note.getId());
        populateNotePage();
    }

    private void getNotification(Note note) {
        createChannel();
        NotificationCompat.Builder n = new
                NotificationCompat.Builder(this, NOTES_CHANNEL);
        n.setSmallIcon(R.drawable.ic_launcher_foreground);
        n.setContentTitle(note.getTitle() + " note has been saved");
        n.setContentText(note.getContent());
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel
                    (NOTES_CHANNEL, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(nc);
        }
    }
}
