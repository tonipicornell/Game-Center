package com.example.gamecentertoni;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;

public class VistaJuego extends SurfaceView implements Runnable {

    // CONSTANTES PUNTUACIONES LADRILLOS:
    private static final int PUNTUATION_GOLD_COLOR = 100;
    private static final int PUNTUATION_YELLOW_COLOR = 90;
    private static final int PUNTUATION_RED_COLOR = 75;
    private static final int PUNTUATION_ORANGE_COLOR = 70;
    private static final int PUNTUATION_CORAL_COLOR = 65;
    private static final int PUNTUATION_PINK_COLOR = 60;
    private static final int PUNTUATION_BLUE_COLOR = 50;
    private static final int PUNTUATION_BLUE_VIOLET_COLOR = 40;
    private static final int PUNTUATION_VIOLET_COLOR = 35;
    private static final int PUNTUATION_TURQUOISE_COLOR = 25;
    private static final int PUNTUATION_DARK_GREEN_COLOR = 10;
    private static final int PUNTUATION_GREEN_COLOR = 5;

    // CONSTANTES CREACIÓN DE LARILLOS:
    private static final int NUMERO_FILAS_LADRILLOS = 12;
    private static final int NUMERO_COLUMNAS_LADRILLOS = 10;
    private static final int MARGEN_POR_CADA_LADRILLO = 10;

    // CONSTANTES PARA ASIGNARLE A VARIABLES:
    private Thread hiloJuego;
    private boolean estaJugando;
    private SurfaceHolder mSurfaceHolder;
    private Paint dibujar;

    // Declaramos la pelota:
    private Pelota pelotaJuego;

    // Declaramos la barra de rebote:
    private BarraRebote barraReboteJuego;
    private boolean juegoComenzado = false;

    // Variables de los ladrillos:
    private Ladrillo[][] listaLadrillos;
    private int filas = NUMERO_FILAS_LADRILLOS; // Número de filas de los ladrillos:
    private int columnas = NUMERO_COLUMNAS_LADRILLOS; // Número de columnas para los ladrillos:
    private int[] coloresLadrillosFilas;

    private int puntuacionLadrillo = 0;

    // Variable para comprobar si ha habido victoria
    private boolean victoria = false;

    public VistaJuego(Context context) {
        super(context);
        mSurfaceHolder = getHolder();

        dibujar = new Paint();
        dibujar.setColor(Color.WHITE);

        // Dibujamos la pelota en el juego:
        pelotaJuego = new Pelota(getContext());

        // Colores de cada ladrillo:
        coloresLadrillosFilas = new int[] {
                ContextCompat.getColor(context, R.color.gold),
                ContextCompat.getColor(context, R.color.yellow),
                ContextCompat.getColor(context, R.color.red),
                ContextCompat.getColor(context, R.color.orange),
                ContextCompat.getColor(context, R.color.coral),
                ContextCompat.getColor(context, R.color.pink),
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.blue_violet),
                ContextCompat.getColor(context, R.color.violet),
                ContextCompat.getColor(context, R.color.turquoise),
                ContextCompat.getColor(context, R.color.dark_green),
                ContextCompat.getColor(context, R.color.green),
        };
    }

    // Método para declarar los ladrillos:
    private void declararLadrillos(int anchoPantalla, int altoPantalla) {
        // Si las dimensiones de la pantalla NO son válidas no se declaran los ladrillos
        if (anchoPantalla <= 0 || altoPantalla <= 0) {
            return;
        }

        // Margen entre cada ladrillo:
        int margenLadrillo = MARGEN_POR_CADA_LADRILLO;

        // Calculo el ancho de cada ladrillo:
        int anchoLadrillo = (anchoPantalla - (margenLadrillo * (columnas + 1))) / columnas;

        // Calculo el alto de cada ladrillo:
        int altoLadrillo = ((altoPantalla / 3 - (margenLadrillo * (filas + 1))) / filas) + 7;

        listaLadrillos = new Ladrillo[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                // Calculo la posición de cada ladrillo, incluyendo sus margenes:
                float x = j * (anchoLadrillo + margenLadrillo) + margenLadrillo;
                float y = i * (altoLadrillo + margenLadrillo) + margenLadrillo;

                listaLadrillos[i][j] = new Ladrillo(x, y, anchoLadrillo, altoLadrillo, coloresLadrillosFilas[i % coloresLadrillosFilas.length]);
            }
        }
    }

    // Método que se encarga de ajustar los objetos del juego segun el tamaño de la pantalla:
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Declaramos la barra de rebote cuando las dimensiones de la pantalla esten disponibles.
        if (barraReboteJuego == null) {
            barraReboteJuego = new BarraRebote(w, h);
        }

        if (barraReboteJuego != null) {
            pelotaJuego.centrarEnBarra(barraReboteJuego);
        }

        // Declaro los ladrillos:
        if (listaLadrillos == null) {
            declararLadrillos(w, h);
        }
    }

    // Método que se encarga de actualizar la lógica del juego en cada ciclo:
    private void update() {
        int anchoPantalla = getWidth();
        int largoPantalla = getHeight();

        // Si hay victoria, no se actualiza nada:
        if (victoria) {
            return;
        }

        // Actualizo la posición de la pelota en la pantalla:
        pelotaJuego.update(anchoPantalla, largoPantalla, juegoComenzado);

        // Solo verificamos las colisiones y la victoria si el juego ha comenzado
        if (juegoComenzado) {
            // Comprobar que la barra ha sido declarada antes de hacer la detección de colisiones.
            if (barraReboteJuego != null) {
                if (pelotaJuego.getBottom() >= barraReboteJuego.getArriba() &&
                        pelotaJuego.getArriba() <= barraReboteJuego.getBottom() &&
                        pelotaJuego.getDerecha() >= barraReboteJuego.getIzquierda() &&
                        pelotaJuego.getIzquierda() <= barraReboteJuego.getDerecha()) {
                    pelotaJuego.cambiarDireccionY();
                }
            }

            // Comprobar colisiones con los ladrillos:
            if (listaLadrillos != null) {
                boolean todosLadrillosDestruidos = true;  // Flag para verificar victoria

                for (int i = 0; i < filas; i++) {
                    for (int j = 0; j < columnas; j++) {
                        Ladrillo ladrillo = listaLadrillos[i][j];
                        if (ladrillo != null) {
                            // Si hay algún ladrillo visible, no hemos ganado aún
                            if (ladrillo.estaVisibleLadrillo()) {
                                todosLadrillosDestruidos = false;

                                // Verificar colisión con este ladrillo
                                if (pelotaJuego.getBottom() >= ladrillo.getArriba() &&
                                        pelotaJuego.getArriba() <= ladrillo.getAbajo() &&
                                        pelotaJuego.getDerecha() >= ladrillo.getIzquierda() &&
                                        pelotaJuego.getIzquierda() <= ladrillo.getDerecha()) {
                                    ladrillo.romper();
                                    pelotaJuego.cambiarDireccionY();

                                    int colorLadrillo = ladrillo.getColor();

                                    // Aumento la puntuación según el color destruido
                                    if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.gold)) {
                                        puntuacionLadrillo += PUNTUATION_GOLD_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.yellow)) {
                                        puntuacionLadrillo += PUNTUATION_YELLOW_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.red)) {
                                        puntuacionLadrillo += PUNTUATION_RED_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.orange)) {
                                        puntuacionLadrillo += PUNTUATION_ORANGE_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.coral)) {
                                        puntuacionLadrillo += PUNTUATION_CORAL_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.pink)) {
                                        puntuacionLadrillo += PUNTUATION_PINK_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.blue)) {
                                        puntuacionLadrillo += PUNTUATION_BLUE_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.blue_violet)) {
                                        puntuacionLadrillo += PUNTUATION_BLUE_VIOLET_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.violet)) {
                                        puntuacionLadrillo += PUNTUATION_VIOLET_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.turquoise)) {
                                        puntuacionLadrillo += PUNTUATION_TURQUOISE_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.dark_green)) {
                                        puntuacionLadrillo += PUNTUATION_DARK_GREEN_COLOR;
                                    } else if (colorLadrillo == ContextCompat.getColor(getContext(), R.color.green)) {
                                        puntuacionLadrillo += PUNTUATION_GREEN_COLOR;
                                    }

                                    // Actualizar el TextView de puntuación:
                                    ((Breakout) getContext()).actualizarPuntuacion(puntuacionLadrillo);
                                }
                            }
                        }
                    }
                }

                // Verificar victoria
                if (todosLadrillosDestruidos && juegoComenzado) {
                    victoria = true;
                    handleVictoria();
                }
            }
        }
    }

    // Método para dibujar los objetos del juego y la vista:
    private void draw() {
        if (mSurfaceHolder.getSurface().isValid()) {
            // Dibujamos el fondo del juego:
            Canvas canvas = mSurfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            // Dibujamos la pelota:
            pelotaJuego.dibujar(canvas, dibujar);

            // Dibujamos la barra de rebote:
            if (barraReboteJuego != null) {
                barraReboteJuego.dibujar(canvas);
            }

            // Dibujo los ladrillos en la pantalla:
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    if (listaLadrillos[i][j] != null) {
                        listaLadrillos[i][j].dibujar(canvas);
                    }
                }
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    // Método para la lógica de la victoria:
    private void handleVictoria() {
        // Asegurar que se ejecute en el hilo principal
        post(() -> {
            // Pausar el juego:
            estaJugando = false;

            // Detener el cronómetro:
            ((Breakout) getContext()).detenerCronometro();

            // Obtener el tiempo final
            String tiempoFinal = ((Breakout) getContext()).getTimeCurrently();

            // Crear y lanzar el intent para VistaWin
            Context context = getContext();
            Intent intent = new Intent(context, VistaWin.class);
            intent.putExtra("puntuacion", puntuacionLadrillo);
            intent.putExtra("tiempo", tiempoFinal);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            // Finalizar la actividad actual
            ((Activity) context).finish();
        });
    }

    // Método para controlar la velocidad del juego:
    private void control() {
        try {
            Thread.sleep(17); // Aproximadamente unos 60 FPS (Frames Por Segundo)
        } catch (InterruptedException exceptionControl) {
            exceptionControl.printStackTrace();
        }
    }

    // Método que se encarga de mantener el ciclo continuo del juego:
    public void run() {
        while (estaJugando) {
            update();
            draw();
            control();
        }
    }

    // Método para reanudar el juego:
    public void resume() {
        estaJugando = true;
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }

    // Método para pausar el juego:
    public void pause() {
        estaJugando = false;

        if (hiloJuego != null) {
            try {
                // Espera a que el hilo termine
                hiloJuego.join();
            } catch (InterruptedException exceptionPause) {
                exceptionPause.printStackTrace();
            }
            // Elimino la referencia al hilo para evitar basura.
            hiloJuego = null;
        }
    }

    // Método para los eventos táctiles, se encarga de mover la barra:
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            // Se ejecuta cuando el usuario toca la pantalla:
            case MotionEvent.ACTION_DOWN:
                // Si el juego ha comenzado
                // Iniciamos el movimiento de la pelota
                if (!juegoComenzado) {
                    pelotaJuego.comenzarMovimientoPelota();
                    juegoComenzado = true;
                }

                break;

            // Se ejecuta cuando el usuario arrastra el dedo por la pantalla:
            case MotionEvent.ACTION_MOVE:
                float toqueX = event.getX();

                // Mover la posición de la barra:
                if (barraReboteJuego != null) {
                    barraReboteJuego.movimientoBarra(toqueX);

                    if (!juegoComenzado) {
                        // Mantener la pelota en la barra de rebote
                        // antes de que se inicie el juego:
                        pelotaJuego.centrarEnBarra(barraReboteJuego);
                    }
                }

                break;
        }

        return true;
    }
}