package com.example.gamecentertoni;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.appcompat.app.AlertDialog;

public class Pelota {

    // 6250 PUNTUACION FINAL.
    // CONSTANTES VALORES PELOTA:
    private static final float TAMANY_PELOTA_X = 400;
    private static final float TAMANY_PELOTA_Y = 600;
    private static final float RADIO_PELOTA = 20;
    private static final float VELOCIDAD_X = 10;
    private static final float VELOCIDAD_Y = 10;

    // CONSTANTES DIALOGO 'GAME OVER':
    private static final String TITULO_GAME_OVER = "Game Over";
    private static final String MENSAJE_GAME_OVER = "You have lost! The ball has touches the ground, do you want to start another game?";
    private static final String POSITIVE_BUTTON_GAME_OVER = "Yes";
    private static final String NEGATIVE_BUTTON_GAME_OVER = "No";

    // CONSTANTES PARA ASIGNARLE A VARIABLES:
    private float x, y;
    private float radioPelota = TAMANY_PELOTA_X;
    private float velocidadX;
    private float velocidadY;
    private Context context;  // Variable para almacenar el contexto

    // Esta constante sirve para evitar múltiples mensajes de detección de tocar el suelo
    private boolean pelotaSuelo;

    public Pelota(Context context) {
        this.context = context;  // Asignar el contexto
        // Tamaño de la pelota:
        this.x = TAMANY_PELOTA_X;
        this.y = TAMANY_PELOTA_Y;
        this.radioPelota = RADIO_PELOTA;

        // Velocidad de la pelota
        this.velocidadX = 0;
        this.velocidadY = 0;

        // De primeras, la pelota no toca el suelo:
        this.pelotaSuelo = false;
    }

    public void update(int anchoPantalla, int largoPantalla, boolean juegoIniciado) {

        // Si el juego, no ha sido iniciado, la posición de la pelota no se actualiza:
        if (!juegoIniciado) {
            return;
        }

        // Actualiza la posición de la pelota.
        x += velocidadX;
        y += velocidadY;

        // Rebote en los bordes de la izquierda y derecha de la pantalla:
        if (x <= radioPelota || x >= anchoPantalla - radioPelota) {
            velocidadX = -velocidadX;
        }

        // Rebote en el alto de la pantalla:
        if (y <= radioPelota) {
            velocidadY = -velocidadY;
        }

        // Manejo del borde inferior: mostrar mensaje de derrota
        if (y >= largoPantalla - radioPelota) {
            // Si la pelota toca el suelo:
            if (!pelotaSuelo) {
                pelotaSuelo = true;

                ((MainActivity) context).runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(TITULO_GAME_OVER).setMessage(MENSAJE_GAME_OVER).setPositiveButton(POSITIVE_BUTTON_GAME_OVER, (dialog, which) -> {
                        ((Breakout) context).reiniciarPartida();
                    }).setNegativeButton(NEGATIVE_BUTTON_GAME_OVER, (dialog, which) -> dialog.dismiss()).show();
                });
            }

            // Si la pelota toca el suelo, la velocidad es 0 para que no varie la puntuación:
            velocidadX = 0;
            velocidadY = 0;

            // Paramos el cronometro:
            ((Breakout) context).detenerCronometro();
        } else {
            pelotaSuelo = false;
        }
    }

    // Método para iniciar el movimiento de la pelota:
    public void comenzarMovimientoPelota() {
        velocidadX = VELOCIDAD_X; // Velocidad en X.
        velocidadY = -VELOCIDAD_Y; // Velocidad en Y.
        pelotaSuelo = false;
    }

    // Método para centrar la pelota en la barra de rebote:
    public void centrarEnBarra(BarraRebote barraRebote) {
        this.x = barraRebote.getIzquierda() + (barraRebote.getDerecha() - barraRebote.getIzquierda()) / 2;
        this.y = barraRebote.getArriba() - radioPelota;
        pelotaSuelo = false;
    }

    // Método para dibujar la pelota:
    public void dibujar(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, radioPelota, paint);
    }

    // Métodos para obtener los límites de la pantalla:
    public float getIzquierda() {
        return x - radioPelota;
    }

    public float getDerecha() {
        return x + radioPelota;
    }

    public float getArriba() {
        return y - radioPelota;
    }

    public float getBottom() {
        return y + radioPelota;
    }

    public void cambiarDireccionY() {
        velocidadY = -velocidadY;
    }
}