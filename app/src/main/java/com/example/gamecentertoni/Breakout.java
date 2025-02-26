package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Breakout extends AppCompatActivity {

    // CONSTANTES PARA EL DIALOGO DE 'RESTART GAME':
    private static final String TITLE_RESTART_GAME = "Restart Game"; // Título del dialogo
    private static final String MESSAGE_RESTART_GAME = "Are you sure you want to restart the game?"; // Mensaje del dialogo
    private static final String RESTART_POSITIVE_BUTTON = "YES"; // Botón positivo del dialogo.
    private static final String RESTART_NEGATIVE_BUTTON = "NO"; // Botoón negativo del dialogo.

    // Base de datos:
    private DatabaseHelper mDatabaseHelper;
    private SessionManager mSessionManager;

    // Variables base de datos:
    private int idUsuarioActual;
    private int puntuacionActual = 0;

    // CONSTANTES PARA ASIGNARLE A VARIABLES:
    private VistaJuego plasmarJuego;
    private FrameLayout lugarColocacionJuego;

    private TextView tiempoPartida;
    private int minutos = 0;
    private int segundos = 0;
    private Handler mHandler;
    private boolean cronometroCorriendo = false;

    private ImageButton restartButton;

    private TextView puntuacionPartida;

    private ImageButton playPauseButton;
    private boolean juegoPausado = false; // Variable para saber el estado que se encuentra el juego
    private TextView pauseInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_breakout);

        mDatabaseHelper = new DatabaseHelper(this);
        mSessionManager = new SessionManager(this);

        // Comprobar que el usuario está loggeado:
        if (mSessionManager.isLoggedIn()) {
            idUsuarioActual = mSessionManager.getUserId();
        } else {
            // Si no está logeado, se manda a la actividad SignIn:
            Toast.makeText(this, "Please login to play game", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Breakout.this, SignIn.class);
            startActivity(intent);

            // Finalizar actividad actual:
            finish();

            return;
        }

        // Creo una nueva instancia de VistaJuego.java:
        plasmarJuego = new VistaJuego(this);

        // Agrego la instancia al activity_main.xml:
        lugarColocacionJuego = findViewById(R.id.game);
        lugarColocacionJuego.addView(plasmarJuego);

        // Hilo principal
        mHandler = new Handler();
        tiempoPartida = findViewById(R.id.time);

        restartButton = findViewById(R.id.restart_button);

        puntuacionPartida = findViewById(R.id.score);

        playPauseButton = findViewById(R.id.play_pause_button);

        pauseInformation = findViewById(R.id.pause_information);

        // Dialogo para cuando el usuario pulse el boton de reiniciar el juego:
        restartButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(TITLE_RESTART_GAME).setMessage(MESSAGE_RESTART_GAME).setPositiveButton(RESTART_POSITIVE_BUTTON, (dialog, which) -> {
                // Declaro el método para reiniciar la partida:
                reiniciarPartida();
            }).setNegativeButton(RESTART_NEGATIVE_BUTTON, (dialog, which) -> {
                dialog.dismiss();
            });

            // Mostrar el dialogo:
            builder.show();
        });

        // Lógica para cuando el usuario pulse el boton de 'Pause y Play':
        playPauseButton.setOnClickListener(view -> {
            // Volver a jugar:
            if (juegoPausado) {
                plasmarJuego.resume();
                iniciarCronometro();

                // Cambiar la imagen del boton
                playPauseButton.setImageResource(R.drawable.play);

                // Mensaje de pausa del juego:
                pauseInformation.setVisibility(View.GONE);

            } else {
                // Pausar el juego:
                plasmarJuego.pause();
                detenerCronometro();

                // Cambiar la imagen del boton
                playPauseButton.setImageResource(R.drawable.pause);

                // Mensaje de pausa del juego:
                pauseInformation.setVisibility(View.VISIBLE);
            }

            // Altener el estado del juego:
            juegoPausado =!juegoPausado;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para guardar las estadísticas del juego:
    public void saveGameStats() {
        // Obtener el tiempo actual:
        String timeSpent = getTimeCurrently();

        // Guardar la puntuación y el tiempo en la base de datos:
        mDatabaseHelper.addBreakoutStats(idUsuarioActual, puntuacionActual, timeSpent);

        Toast.makeText(this, "Game saved!", Toast.LENGTH_SHORT).show();
    }

    // Método para actualizar la puntuación:
    public void actualizarPuntuacion(int puntuacionActualizada) {
        this.puntuacionActual = puntuacionActualizada;

        runOnUiThread(() -> {
            puntuacionPartida.setText(String.valueOf(puntuacionActualizada));
        });
    }

    // Método para iniciar el cronometro:
    private void iniciarCronometro() {
        // Aseguro que el cronometro está funcionando.
        cronometroCorriendo = true;
        mHandler.postDelayed(cronometroRunnable, 1000);
    }

    // Método para detener el cronometro:
    public void detenerCronometro() {
        // Aseguro que el cronometro está parado.
        cronometroCorriendo = false;
        mHandler.removeCallbacks(cronometroRunnable);
    }

    // Runnable para poder manejar el cronómetro:
    private Runnable cronometroRunnable = new Runnable() {
        @Override
        public void run() {
            if (cronometroCorriendo) {
                // Aumentamos los segundos:
                segundos++;

                // Si los segundos llegan a 60, aumentamos un minuto y ponemos los segundos a 0:
                if (segundos == 60) {
                    minutos++;
                    segundos = 0;
                }

                // Actualizo el TextView tiempoPartida, el formato es mm:ss
                String tiempo = String.format("%02d:%02d", minutos, segundos);
                tiempoPartida.setText(tiempo);

                // Vuelvo a ejecutar el Runnable despés de 1 segundo.
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    // Método para reiniciar la partida:
    public void reiniciarPartida() {
        // Pauso el juego actual y detengo el hilo de la instancia antigua:
        plasmarJuego.pause();

        // Aseguro que el hilo se ha parado correctamente:
        plasmarJuego = null;

        // Reinicio la puntuación interna del juego.
        // La reinicio antes de crear la nueva instancia.
        actualizarPuntuacion(0);

        // Creo una nueva instancia para el juego:
        plasmarJuego = new VistaJuego(this);

        // Eliminar la vista actual del juego:
        lugarColocacionJuego.removeAllViews();

        // Coloco la nueva vista del juego:
        lugarColocacionJuego.addView(plasmarJuego);

        // Reiniciar el cronómetro a 0:
        minutos = 0;
        segundos = 0;
        tiempoPartida.setText(String.format("%02d:%02d", minutos, segundos));

        // Mostramos el juego reiniciado:
        plasmarJuego.resume();

        // Inicio el cronometro
        iniciarCronometro();
    }

    // Getter de tiempo:
    public String getTimeCurrently() {
        return String.format("%02d:%02d", minutos, segundos);
    }

    // Método para reanudar el juego:
    @Override
    protected void onResume() {
        super.onResume();
        plasmarJuego.resume();
        cronometroCorriendo = true;
        iniciarCronometro();
    }

    // Método para cuando finaliza la partida
    public void endGame() {
        // Parar el juego:
        plasmarJuego.pause();
        detenerCronometro();

        // Guardar estadísticas:
        saveGameStats();

        // SI piedes sale un dialogo:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over").setMessage("Your score: " + puntuacionActual)
                .setPositiveButton("Play Again", (dialog, which) -> {
                    reiniciarPartida();
                })
                .setNegativeButton("Return to Menu", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();

    }

    // Método para pausar el juego:
    @Override
    protected void onPause() {
        super.onPause();
        plasmarJuego.pause();
        detenerCronometro();
    }
}