package com.example.examen_3_elgin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EntrevistaAdapter extends RecyclerView.Adapter<EntrevistaAdapter.EntrevistaViewHolder> {

    private List<Entrevista> entrevistas;
    private final OnEntrevistaClickListener listener;

    public EntrevistaAdapter(List<Entrevista> entrevistas, OnEntrevistaClickListener listener) {
        this.entrevistas = entrevistas;
        this.listener = listener;
    }

    public void updateData(List<Entrevista> nuevasEntrevistas) {
        this.entrevistas = nuevasEntrevistas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntrevistaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrevista, parent, false);
        return new EntrevistaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrevistaViewHolder holder, int position) {
        Entrevista entrevista = entrevistas.get(position);
        holder.bind(entrevista, listener);
    }

    @Override
    public int getItemCount() {
        return entrevistas.size();
    }

    public interface OnEntrevistaClickListener {
        void onEntrevistaClick(Entrevista entrevista);
    }

    public static class EntrevistaViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDescripcion;
        private final TextView tvFecha;
        private final ImageView imgEntrevista;

        public EntrevistaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            imgEntrevista = itemView.findViewById(R.id.imgEntrevista);
        }

        public void bind(final Entrevista entrevista, final OnEntrevistaClickListener listener) {
            tvDescripcion.setText(entrevista.getDescripcion());
            tvFecha.setText(entrevista.getFecha());  // Asegúrate de tener el campo fecha en Entrevista

            // Carga la imagen con Glide o Picasso
            Glide.with(itemView.getContext())
                    .load(entrevista.getImagenUri())  // Asegúrate de tener el campo imagenUri en Entrevista
                    .placeholder(R.drawable.ic_default_image)
                    .into(imgEntrevista);

            itemView.setOnClickListener(v -> listener.onEntrevistaClick(entrevista));
        }
    }
}

