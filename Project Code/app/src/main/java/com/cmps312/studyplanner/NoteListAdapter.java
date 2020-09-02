package com.cmps312.studyplanner;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmps312.studyplanner.NoteListFragment.OnListFragmentInteractionListener;
import com.cmps312.studyplanner.model.Note;

import java.util.ArrayList;


public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {

    private final ArrayList<Note> mNotes;
    private final OnListFragmentInteractionListener mListener;

    public NoteListAdapter(ArrayList<Note> items, OnListFragmentInteractionListener listener) {
        mNotes = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mNoteItem = mNotes.get(position);
        holder.mDateView.setText(mNotes.get(position).getDate());
        holder.mTitleView.setText(mNotes.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.getNoteItem(holder.mNoteItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDateView;
        public final TextView mTitleView;
        public Note mNoteItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDateView = (TextView) view.findViewById(R.id.note_item_date);
            mTitleView = (TextView) view.findViewById(R.id.note_item_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
