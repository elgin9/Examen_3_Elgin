package com.example.examen_3_elgin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ModificarEntrevistaActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEntrevistas;
    private EntrevistaAdapter adapter;
    private FirebaseFirestore db;
    private EditText etDescripcion;
    private Button btnModificar;
    private Button btnEliminar;
    private Entrevista entrevistaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_entrevista);

        recyclerViewEntrevistas = findViewById(R.id.recyclerViewEntrevistas);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);

        recyclerViewEntrevistas.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        adapter = new EntrevistaAdapter(new ArrayList<>(), this::onEntrevistaSelected);
        recyclerViewEntrevistas.setAdapter(adapter);

        cargarEntrevistas();

        btnModificar.setOnClickListener(v -> modificarEntrevista());
        btnEliminar.setOnClickListener(v -> eliminarEntrevista());
    }

    private void cargarEntrevistas() {
        db.collection("entrevistas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Entrevista> entrevistas = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Entrevista entrevista = document.toObject(Entrevista.class);
                    entrevista.setId(document.getId()); // Asegúrate de establecer el ID del documento
                    entrevistas.add(entrevista);
                }
                adapter.updateData(entrevistas);
            } else {
                // Manejar el error
                Toast.makeText(this, "Error al cargar entrevistas", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onEntrevistaSelected(Entrevista entrevista) {
        entrevistaSeleccionada = entrevista;
        etDescripcion.setText(entrevista.getDescripcion());
    }

    private void modificarEntrevista() {
        if (entrevistaSeleccionada != null) {
            String nuevaDescripcion = etDescripcion.getText().toString();
            entrevistaSeleccionada.setDescripcion(nuevaDescripcion);

            db.collection("entrevistas").document(entrevistaSeleccionada.getId())
                    .set(entrevistaSeleccionada)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Entrevista modificada", Toast.LENGTH_SHORT).show();
                        etDescripcion.setText(""); // Limpiar el campo de descripción
                        entrevistaSeleccionada = null; // Deseleccionar la entrevista
                        cargarEntrevistas(); // Recargar las entrevistas
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al modificar entrevista", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Selecciona una entrevista para modificar", Toast.LENGTH_SHORT).show();
        }
    }


    private void eliminarEntrevista() {
        if (entrevistaSeleccionada != null) {
            db.collection("entrevistas").document(entrevistaSeleccionada.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Entrevista eliminada", Toast.LENGTH_SHORT).show();
                        cargarEntrevistas();
                        etDescripcion.setText("");
                        entrevistaSeleccionada = null;
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar entrevista", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Selecciona una entrevista para eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}
