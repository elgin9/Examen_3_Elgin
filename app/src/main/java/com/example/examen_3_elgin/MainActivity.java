    package com.example.examen_3_elgin;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import androidx.appcompat.app.AppCompatActivity;

    public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }

        public void onIngresarEntrevistaClick(View view) {
            startActivity(new Intent(this, IngresarEntrevistaActivity.class));
        }

        public void onListaEntrevistasClick(View view) {
            startActivity(new Intent(this, ListaEntrevistasActivity.class));
        }

        public void onModificarEntrevistaClick(View view) {
            startActivity(new Intent(this, ModificarEntrevistaActivity.class));
        }

    }
