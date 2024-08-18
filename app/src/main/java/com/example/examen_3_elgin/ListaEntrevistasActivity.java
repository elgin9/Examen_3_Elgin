package com.example.examen_3_elgin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaEntrevistasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEntrevistas;
    private EntrevistaAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_entrevistas);

        recyclerViewEntrevistas = findViewById(R.id.recyclerViewEntrevistas);
        recyclerViewEntrevistas.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        adapter = new EntrevistaAdapter(new ArrayList<>(), this::onEntrevistaSelected);
        recyclerViewEntrevistas.setAdapter(adapter);

        cargarEntrevistas();
    }

    private void cargarEntrevistas() {
        db.collection("entrevistas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Entrevista> entrevistas = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Entrevista entrevista = document.toObject(Entrevista.class);
                    entrevista.setId(document.getId()); // Obtener el ID del documento
                    entrevistas.add(entrevista);
                }
                adapter.updateData(entrevistas);
            } else {
                // Manejar el error
            }
        });
    }


    private void onEntrevistaSelected(Entrevista entrevista) {
        Intent intent = new Intent(this, EscucharEntrevistaActivity.class);
        intent.putExtra("entrevista", entrevista);
        startActivity(intent);
    }
}
