package com.example.examen_3_elgin;

import android.os.Parcel;
import android.os.Parcelable;

public class Entrevista implements Parcelable {
    private String id;
    private String descripcion;
    private String audioUri;
    private String fecha;  // Nuevo campo para la fecha
    private String imagenUri;  // Nuevo campo para la imagen

    // Constructor vacío necesario para Firebase
    public Entrevista() {}

    public Entrevista(String id, String descripcion, String audioUri, String fecha, String imagenUri) {
        this.id = id;
        this.descripcion = descripcion;
        this.audioUri = audioUri;
        this.fecha = fecha;
        this.imagenUri = imagenUri;
    }

    protected Entrevista(Parcel in) {
        id = in.readString();
        descripcion = in.readString();
        audioUri = in.readString();
        fecha = in.readString();  // Lee el campo fecha
        imagenUri = in.readString();  // Lee el campo imagenUri
    }

    public static final Creator<Entrevista> CREATOR = new Creator<Entrevista>() {
        @Override
        public Entrevista createFromParcel(Parcel in) {
            return new Entrevista(in);
        }

        @Override
        public Entrevista[] newArray(int size) {
            return new Entrevista[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(String audioUri) {
        this.audioUri = audioUri;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(descripcion);
        parcel.writeString(audioUri);
        parcel.writeString(fecha);  // Escribe el campo fecha
        parcel.writeString(imagenUri);  // Escribe el campo imagenUri
    }
}