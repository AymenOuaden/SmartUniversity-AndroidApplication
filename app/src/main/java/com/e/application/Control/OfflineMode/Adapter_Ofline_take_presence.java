package com.e.application.Control.OfflineMode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.R;

import java.util.ArrayList;

public class Adapter_Ofline_take_presence extends RecyclerView.Adapter<Adapter_Ofline_take_presence.ViewHolder> {

    private ArrayList<AbsenceSQL> ids;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public Adapter_Ofline_take_presence(Context context, ArrayList<AbsenceSQL> data) {
        this.mInflater = LayoutInflater.from(context);
        this.ids = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_ofline_take_presences, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.num.setText(String.valueOf(position + 1));
        holder.id.setText(String.valueOf(ids.get(position).getId_etudiant()));
        holder.seance.setText(ids.get(position).getJour_seance() + " " + ids.get(position).getHeure_seance());
        holder.date_absence.setText(ids.get(position).getDate_absence());
        holder.supprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mClickListener.supprimer(v, position);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return ids.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView num, id, date_absence, seance;
        ImageView supprimer;

        ViewHolder(View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.num);
            id = itemView.findViewById(R.id.id);
            seance = itemView.findViewById(R.id.seance);
            date_absence = itemView.findViewById(R.id.date_absence);
            supprimer = itemView.findViewById(R.id.supprimer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

    // convenience method for getting data at click position
    private int getItem(int id) {
        return ids.get(id).getId_etudiant();
    }

    // allows clicks events to be caught
    public void setClickListener(MainHorsLigne itemClickListener) {
        this.mClickListener = (ItemClickListener) itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void supprimer(View view, int position);
    }
}
