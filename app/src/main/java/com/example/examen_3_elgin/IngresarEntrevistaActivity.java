package com.example.examen_3_elgin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class IngresarEntrevistaActivity extends AppCompatActivity {

    private EditText etDescripcion, etPeriodista, etFecha;
    private ImageView imgEntrevistado;
    private Button btnTomarFoto, btnGrabarAudio, btnGuardarEntrevista, btnReproducirAudio;
    private Uri imagenUri, audioUri;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_AUDIO_PERMISSION_CODE = 200;
    private final int REQUEST_WRITE_STORAGE_PERMISSION_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_entrevista);

        etDescripcion = findViewById(R.id.etDescripcion);
        etPeriodista = findViewById(R.id.etPeriodista);
        etFecha = findViewById(R.id.etFecha);
        imgEntrevistado = findViewById(R.id.imgEntrevistado);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGrabarAudio = findViewById(R.id.btnGrabarAudio);
        btnGuardarEntrevista = findViewById(R.id.btnGuardarEntrevista);
        btnReproducirAudio = findViewById(R.id.btnReproducirAudio);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Asegurarse de que el botón de grabar audio esté habilitado
        btnGrabarAudio.setEnabled(true);

        btnTomarFoto.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.CAMERA)) {
                dispatchTakePictureIntent();
            } else {
                requestPermission(Manifest.permission.CAMERA, REQUEST_IMAGE_CAPTURE);
            }
        });

        btnGrabarAudio.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.RECORD_AUDIO)) {
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    startRecording();
                } else {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_STORAGE_PERMISSION_CODE);
                }
            } else {
                requestPermission(Manifest.permission.RECORD_AUDIO, REQUEST_AUDIO_PERMISSION_CODE);
            }
        });

        btnGuardarEntrevista.setOnClickListener(v -> guardarEntrevista());

        btnReproducirAudio.setOnClickListener(v -> reproducirAudio());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Asegurarse de que el botón de grabar audio esté habilitado al regresar a la pantalla
        btnGrabarAudio.setEnabled(true);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgEntrevistado.setImageBitmap(imageBitmap);

            // Convertir la imagen a bytes y subir a Firebase Storage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataImage = baos.toByteArray();

            StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putBytes(dataImage).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imagenUri = uri;
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener URL de la imagen", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void startRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }

        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio_" + System.currentTimeMillis() + ".3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Grabando audio", Toast.LENGTH_SHORT).show();
            btnGrabarAudio.setText("Detener Grabación");
            btnGrabarAudio.setOnClickListener(v -> stopRecording());

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar la grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            // Subir el archivo de audio a Firebase Storage
            Uri audioUri = Uri.fromFile(new File(audioFilePath));
            StorageReference audioRef = storageRef.child("audio/" + System.currentTimeMillis() + ".3gp");
            audioRef.putFile(audioUri).addOnSuccessListener(taskSnapshot -> {
                audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    this.audioUri = uri;
                    Toast.makeText(this, "Audio grabado", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener URL del audio", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al subir audio", Toast.LENGTH_SHORT).show();
            });

        } catch (RuntimeException e) {
            Toast.makeText(this, "Error al detener la grabación", Toast.LENGTH_SHORT).show();
        }

        btnGrabarAudio.setText("Grabar Audio");
        btnGrabarAudio.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.RECORD_AUDIO)) {
                startRecording();
            }
        });
    }

    private void guardarEntrevista() {
        String descripcion = etDescripcion.getText().toString();
        String periodista = etPeriodista.getText().toString();
        String fecha = etFecha.getText().toString();

        if (descripcion.isEmpty() || periodista.isEmpty() || fecha.isEmpty() || imagenUri == null) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar la entrevista en Firestore
        Interview interview = new Interview(descripcion, periodista, fecha, imagenUri.toString(), audioUri != null ? audioUri.toString() : null);
        db.collection("entrevistas").add(interview).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Entrevista guardada", Toast.LENGTH_SHORT).show();
            clearFields();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al guardar la entrevista", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearFields() {
        etDescripcion.setText("");
        etPeriodista.setText("");
        etFecha.setText("");
        imgEntrevistado.setImageDrawable(null);
        audioUri = null;
    }

    private void reproducirAudio() {
        if (audioUri != null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(this, "Reproduciendo audio", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No hay audio para reproducir", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        super.onDestroy();
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Permiso de grabación denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
