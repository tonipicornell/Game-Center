package com.example.gamecentertoni;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ladrillo {
    // Posición de los ladrillos:
    private float x, y;

    // Tamaño de los ladrillos:
    private float ancho, alto;

    // Color:
    private Paint colorLadrillo;

    // Visibilidad:
    private boolean ladrilloVisible;

    public Ladrillo(float x, float y, float ancho, float alto, int color) {
        this.x = x;
        this.y = y;

        this.ancho = ancho;
        this.alto = alto;

        this.ladrilloVisible = true;

        colorLadrillo = new Paint();
        colorLadrillo.setColor(color);
    }

    // Método para dibujar los ladrillos:
    public void dibujar(Canvas canvas) {
        if (ladrilloVisible) {
            canvas.drawRect(x, y , x + ancho, y + alto, colorLadrillo);
        }
    }

    // Método para saber si el ladrillo está visible:
    public boolean estaVisibleLadrillo() {
        return ladrilloVisible;
    }

    // Método para ocultar el ladrillo al chocar la pelota:
    public  void romper() {
        ladrilloVisible = false;
    }

    // Métodos para tener los límites de los ladrillos:
    public float getIzquierda() {
        return x;
    }

    public float getDerecha() {
        return x + ancho;
    }

    public float getArriba() {
        return y;
    }

    public float getAbajo() {
        return y + alto;
    }

    // Devuelve el color del ladrillo
    public int getColor() {
        return colorLadrillo.getColor();
    }
}