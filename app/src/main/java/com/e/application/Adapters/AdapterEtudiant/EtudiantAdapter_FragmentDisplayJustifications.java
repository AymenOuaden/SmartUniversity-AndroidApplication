package com.e.application.Adapters.AdapterEtudiant;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.application.Control.Etudiant.EtudiantFragmentDisplayJustification;
import com.e.application.Model.Justification;
import com.e.application.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class EtudiantAdapter_FragmentDisplayJustifications extends RecyclerView.Adapter<EtudiantAdapter_FragmentDisplayJustifications.ViewHolder> {

    private ArrayList<Justification> mData;
    private LayoutInflater mInflater;
    private EtudiantAdapter_FragmentDisplayJustifications.ItemClickListener mClickListener;


    // data is passed into the constructor
    public EtudiantAdapter_FragmentDisplayJustifications(EtudiantFragmentDisplayJustification context, ArrayList<Justification> justifications) {
        this.mInflater = LayoutInflater.from(context.getActivity());
        this.mData = justifications;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.etudiant_recyclerview_display_justification, parent, false);
        return new ViewHolder(view);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date, etat;
        ImageView image;
        PhotoView wallpaper;
        ImageView zoomIN, zoomOUT, supprimer, download;

        ViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_justification);
            etat = itemView.findViewById(R.id.etat);
            image = itemView.findViewById(R.id.image);
            zoomIN = itemView.findViewById(R.id.zoom);
            zoomOUT = itemView.findViewById(R.id.zoomOUT);
            supprimer = itemView.findViewById(R.id.supprimer);
            download = itemView.findViewById(R.id.download);
            wallpaper = itemView.findViewById(R.id.wallpaper);

            zoomIN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomIN.setVisibility(View.GONE);
                    zoomOUT.setVisibility(View.VISIBLE);
                    wallpaper.setVisibility(View.VISIBLE);
                    image.setVisibility(View.GONE);
                    // garder la boutons au dessous avec une arrière plan 25% transparente
                    download.setImageAlpha(64);
                    supprimer.setImageAlpha(64);
                    //  zoom_in.getBackground().setAlpha(64);
                    zoomOUT.setImageAlpha(64);
                }
            });

            zoomOUT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomIN.setVisibility(View.VISIBLE);
                    zoomOUT.setVisibility(View.GONE);
                    wallpaper.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    // garder la boutons au dessous avec une arrière plan 25% transparente
                    download.setImageAlpha(255);
                    supprimer.setImageAlpha(255);
                    //  zoom_in.getBackground().setAlpha(64);
                    zoomOUT.setImageAlpha(255);
                }
            });

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.downloadJustification(getImageArray(getAdapterPosition()));
                }
            });

            supprimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.deleteJustification(getNumeroAbsence(getAdapterPosition()),
                            getNumeroJustification(getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View v) {
        }
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        StringBuilder date = new StringBuilder();
        for (int i = 0; i <= mData.get(position).getDate_justification().length() - 2; i++) {
            date.append(mData.get(position).getDate_justification().charAt(i));
        }
        holder.date.setText(" : " + date);
        String etat = mData.get(position).getEtat_justification().toString();
        String etat_afficher = etat;
        switch (etat) {
            case "valide":
                etat_afficher = mInflater.getContext().getResources().getString(R.string.valide);
                break;
            case "refuse":
                etat_afficher = mInflater.getContext().getResources().getString(R.string.refuse);
                break;
            case "nonTraite":
                etat_afficher = mInflater.getContext().getResources().getString(R.string.non_traite);
                break;
        }

        holder.etat.setText(" : " + etat_afficher);
        Bitmap bitmap = BitmapFactory.decodeByteArray(mData.get(position).getFichier(),
                0, mData.get(position).getFichier().length);
        holder.image.setImageBitmap(bitmap);
        holder.wallpaper.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        else
            return 0;
    }

    // allows clicks events to be caught
    public void setClickListener(EtudiantAdapter_FragmentDisplayJustifications.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }


    private byte[] getImageArray(int position) {
        return mData.get(position).getFichier();
    }

    private int getNumeroAbsence(int position) {
        return mData.get(position).getNumero_absence();
    }

    private int getNumeroJustification(int position) {
        return mData.get(position).getNumero_justification();
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {

        void deleteJustification(int numero_absence, int numero_justification);

        void downloadJustification(byte[] image);
    }

}
