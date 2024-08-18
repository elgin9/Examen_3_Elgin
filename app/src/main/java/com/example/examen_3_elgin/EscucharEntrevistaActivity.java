package com.example.examen_3_elgin;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class EscucharEntrevistaActivity extends AppCompatActivity {

    private TextView tvDescripcionEntrevista;
    private Button btnReproducirAudio;
    private MediaPlayer mediaPlayer;
    private Uri audioUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escuchar_entrevista);

        tvDescripcionEntrevista = findViewById(R.id.tvDescripcionEntrevista);
        btnReproducirAudio = findViewById(R.id.btnReproducirAudio);

        // Obtener la entrevista pasada como extra
        Entrevista entrevista = getIntent().getParcelableExtra("entrevista");
        if (entrevista != null) {
            tvDescripcionEntrevista.setText(entrevista.getDescripcion());
            audioUri = Uri.parse(entrevista.getAudioUri());
        }

        btnReproducirAudio.setOnClickListener(v -> reproducirAudio());
    }

    private void reproducirAudio() {
        // Mostrar un mensaje antes de reproducir el audio
        Toast.makeText(this, "Reproduciendo", Toast.LENGTH_SHORT).show();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, audioUri);
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Listener para mostrar un mensaje al finalizar la reproducción
            mediaPlayer.setOnCompletionListener(mp ->
                    Toast.makeText(EscucharEntrevistaActivity.this, "Fin de la reproducción del audio", Toast.LENGTH_SHORT).show()
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
