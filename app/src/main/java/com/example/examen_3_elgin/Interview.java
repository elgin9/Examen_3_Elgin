package com.example.examen_3_elgin;

public class Interview {
    private String descripcion;
    private String periodista;
    private String fecha;
    private String imagenUri;
    private String audioUri;

    // Constructor vacío requerido por Firestore
    public Interview() {}

    public Interview(String descripcion, String periodista, String fecha, String imagenUri, String audioUri) {
        this.descripcion = descripcion;
        this.periodista = periodista;
        this.fecha = fecha;
        this.imagenUri = imagenUri;
        this.audioUri = audioUri;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPeriodista() {
        return periodista;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
    }
}
