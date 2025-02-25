package com.example.gamecentertoni;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BarraRebote {

    // CONSTANTE PARA EL LARGO DE LA BARRA:
    private static final int TAMANY_LARGO_BARRA = 30;

    // CONSTANTES PARA ASIGNARLE A VARIABLES:
    private float x, y;
    private float ancho, largo;
    private Paint mPaint;

    private float anchoPantalla;

    public BarraRebote(float anchoPantalla, float largoPantalla) {
        this.anchoPantalla = anchoPantalla; // Inicializar el ancho de la pantalla correctamente

        // Tamaño de la barra
        this.ancho = anchoPantalla / 10;

        this.largo = TAMANY_LARGO_BARRA; // Altura fija de la barra

        // Centrar horizontalmente
        this.x = (anchoPantalla - ancho) / 2;

        // Colocar la barra cerca de la parte inferior de la pantalla:
        this.y = largoPantalla - 100; // Ajusta la posición vertical para que sea visible

        // Configurar el color y estilo de la barra
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
    }

    // Método para dibujar la barra de rebote:
    public void dibujar(Canvas canvas) {
        // Dibujar la barra como un rectángulo
        canvas.drawRect(x, y, x + ancho, y + largo, mPaint);
    }

    // Método para mover la barra dentro de X:
    public void movimientoBarra(float movimientoX) {
        // Calcular la nueva posición de la barra
        x = movimientoX - (ancho / 2);

        // Limitar el movimiento de la barra dentro de los bordes de la pantalla
        if (x < 0) {
            x = 0;
        }
        if (x + ancho > anchoPantalla) {
            x = anchoPantalla - ancho;
        }
    }

    // Métodos para obtener los límites de la barra
    public float getIzquierda() {
        return x;
    }

    public float getDerecha() {
        return x + ancho;
    }

    public float getArriba() {
        return y;
    }

    public float getBottom() {
        return y + largo;
    }
}