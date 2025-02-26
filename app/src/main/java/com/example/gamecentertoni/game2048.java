package com.example.gamecentertoni;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class game2048 extends AppCompatActivity {

    private DatabaseHelper mDatabaseHelper;
    private SessionManager mSessionManager;
    private int idUsuarioActual;

    private GridLayout tableroJuego; // Tablero del 2048
    private TextView[][] celdas; // Celdas del tablero

    private GestureDetector detectorMovimientos;

    private int puntuacion = 0; // Puntuación inicial
    private TextView puntuacionTextView; // Texto donde sale la puntuación actual
    // Lista para almacenar las puntuaciones de cada partida.
    private List<Integer> almacenarPuntuacion = new ArrayList<>();
    private int puntuacionAnterior = 0; // Variable para almacenar la puntuación anterior.
    private int maximaPuntuacion; // Variable para almacenar la máxima puntuacion conseguida.
    private TextView maximaPuntacionTextView; // Texto para la puntuacion máxima

    // Botones para disfrutar del juego:
    private Button previousPlay; // Boton para volver a la jugada anterior.
    private Button restartGame; // Boton para reiniciar el juego.
    private Button exitGame;

    // Array bidimensional para almacenar la última jugada:
    private String[][] anteriorJugada = new String[4][4]; // Este array es 4x4, como el tablero

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game2048);

        mDatabaseHelper = new DatabaseHelper(this);
        mSessionManager = new SessionManager(this);


        tableroJuego = findViewById(R.id.tablero);
        puntuacionTextView = findViewById(R.id.puntuation);
        maximaPuntacionTextView = findViewById(R.id.maximum_score);

        Intent intentInfo = getIntent();
        String username = intentInfo.getStringExtra("USERNAME");
        idUsuarioActual = intentInfo.getIntExtra("USER_ID", -1);

        if (username == null || idUsuarioActual == -1) {
            Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hacer aparecer su máxima puntuación obtenida:
        int highScoreUser = mDatabaseHelper.getHighest2048Score(idUsuarioActual);

        if (highScoreUser > 0) { // Verifica si la puntuación obtenida es mayor que 0
            maximaPuntuacion = highScoreUser;
        } else {
            maximaPuntuacion = 0; // Si no hay puntuaciones registradas, empieza desde 0
        }

        maximaPuntacionTextView.setText("Best: " + maximaPuntuacion);


        // El tablero será un 4x4:
        celdas = new TextView[4][4];

        // Declaración del los botones para el juego:
        previousPlay = findViewById(R.id.return_previous_play); // Volver a la jugada anterior.
        restartGame = findViewById(R.id.button_reset_game); // Reiniciar el juego.
        exitGame = findViewById(R.id.button_exit_game);

        // Inicio el tablero:
        inicioTablero();

        // Coloco la ficha inicial:
        colocarFicha();

        // Actualizar los colores de las celdas.
        actualizarCeldaColores();

        // Detectar los movimientos al deslizar la pantalla:
        detectorMovimientos = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1,MotionEvent e2, float velocityX, float velocityY) {
                // Movimiento de la coordenada x
                float movimientoEjeX = e2.getX() - e1.getX();
                // Movimiento de la coordenada y
                float movimientoEjeY = e2.getY() - e1.getY();

                // Variable para comprobar si el movimiento ha sido correcto
                boolean movimientoRealizadoCorrectamente = false;

                // Si el movimiento "x" ha sido mayor que "y" entonces:
                if (Math.abs(movimientoEjeX) > Math.abs(movimientoEjeY)) {
                    // Movimiento hacia la derecha
                    if (movimientoEjeX > 0) {
                        // Guardo el estado actual del tablero:
                        estadoActualTablero();
                        movimientoRealizadoCorrectamente = movimientoDerecha();
                    } else { // Movimiento hacia la izquierda
                        // Guardo el estado actual del tablero:
                        estadoActualTablero();
                        movimientoRealizadoCorrectamente = movimientoIzquierda();
                    }
                } else { // Movimiento "y" ha sido mayor que el "x"
                    // Movimiento hacia abajo
                    if (movimientoEjeY > 0) {
                        // Guardo el estado actual del tablero:
                        estadoActualTablero();
                        movimientoRealizadoCorrectamente = movimientoAbajo();
                    } else { // Movimiento hacia arriba
                        // Guardo el estado actual del tablero:
                        estadoActualTablero();
                        movimientoRealizadoCorrectamente = movimientoArriba();
                    }
                }

                // Si el movimiento se ha realizado correctamente creamos una ficha.
                // La ficha se colocará en una celda aleatoria,
                if (movimientoRealizadoCorrectamente) {
                    crearFichaAleatoria();
                    actualizarCeldaColores();

                    if (!movimientosPosibles()) {
                        dialagoDerrota();
                    }
                }
                return true;
            }
        });

        // Boton para volver a la jugada anterior, añadiendo dialogo:
        previousPlay.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Previous Play").setMessage("You have only ONE OPPORTUNITY. Are you sure you want to go back to the previous play?").setPositiveButton("YES", (dialog, which) -> {
                // Volvemos a la última jugada
                ultimaJugada();

                // Quitamos el boton, porque solo tiene una oportunidad.
                previousPlay.setVisibility(View.GONE);
            }).setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        });

        // Boton para reiniciar el juego:
        restartGame.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Restart the game").setMessage("Are you sure you want to restart the game?").setPositiveButton("YES", (dialog, which) -> {
                // Reiniciar juego:
                reiniciarJuego();

                // Hago el boton "Previous Play" visible
                // en el caso que lo haya utilizado anteriormente.
                previousPlay.setVisibility(View.VISIBLE);
            }).setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        });

        exitGame.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit Game").setMessage("Do you want to exit the game?").setPositiveButton("YES", (dialog, which) -> {
                Intent intent = new Intent(game2048.this, InitialGame2048.class);
                startActivity(intent);

                finish();
            }).setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para guardar el estado del tablero, la puntuación y los botones
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Guardar las puntuaciones:
        outState.putInt("puntuacion", puntuacion);
        outState.putInt("puntuacionAnterior", puntuacionAnterior);
        outState.putInt("maximaPuntuacion", maximaPuntuacion);

        // Guardo el estado del boton "previousPlay":
        outState.putBoolean("previousPlayButton", previousPlay.getVisibility() == View.VISIBLE);

        // Guardar el estado del tablero
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                outState.putString("celda_" + i + "_" + j, celdas[i][j].getText().toString());
            }
        }
    }

    // Método para restaurar el estado guardado:
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Ponemos las puntuaciones guardadas en sus sitios correspondientes:
        puntuacion = savedInstanceState.getInt("puntuacion");
        puntuacionAnterior = savedInstanceState.getInt("puntuacionAnterior");
        maximaPuntuacion = savedInstanceState.getInt("maximaPuntuacion");

        puntuacionTextView.setText("Score: " + puntuacion);
        maximaPuntacionTextView.setText("Best: " + maximaPuntuacion);

        // Restauro el boton de "Previous Play"
        boolean previousButtonVisibility = savedInstanceState.getBoolean("previousPlayButton");

        // Si el boton "Previous Play" esta visible se pone visible:
        if (previousButtonVisibility) {
            previousPlay.setVisibility(View.VISIBLE);
        } else {
            // Sino el posam no visible:
            previousPlay.setVisibility(View.GONE);
        }
        // Restauramos el estado del tablero
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++) {
                String valorCeldas = savedInstanceState.getString("celda_" + i + "_" + j);
                celdas[i][j].setText(valorCeldas);
            }
        }

        // Actualizo los colores despues de restaurar las celdas:
        actualizarCeldaColores();
    }

    // Método para guardar las estadísticas del juego:
    public void saveGameStats(int userId, int score) {
        if (userId <= 0) {
            Log.e("DatabaseError", "Invalid user ID: " + userId);
            Toast.makeText(this, "Cannot save score: invalid user session", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String currentTime = getCurrentTime();

        try {
            long result = dbHelper.add2048Stats(userId, score, currentTime);
            if (result != -1) {
                Toast.makeText(this, "Puntuación guardada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar la puntuación", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("DatabaseError", "Error al guardar la puntuación: " + e.getMessage(), e);
            Toast.makeText(this, "Error al guardar la puntuación", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para obtener la hora actual como String
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Método para iniciar el tablero de juego
    private void inicioTablero() {
        // Tamaño fijo del tablero:
        int tamanyTablero = (int) getResources().getDisplayMetrics().density * 390;
        // Tamaño de cada celda:
        int tamanyCeldas = tamanyTablero / 4;

        // Recorro las filas del tablero, que son 4
        for (int i = 0; i < 4; i++) {
            // Recorro las columnas del tablero, que vuelve a ser 4
            for (int j = 0; j < 4; j++){
                // Crea una nueva celda como un TextView, para que se vean los numeros
                TextView celda = new TextView(this);
                // Tamaño del texto, es decir, de los numeros.
                celda.setTextSize(24);
                // Fondo de las celdas
                celda.setBackgroundResource(R.drawable.fondo_celda);
                // Centra el texto de la celda
                celda.setGravity(Gravity.CENTER);
                // El color del texto de la celda
                celda.setTextColor(getResources().getColor(android.R.color.black));
                // Al principio, las celdas estaran vacias
                celda.setText("");

                // Configuro el tamaño y los margenes de las celdas del tablero:
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = tamanyCeldas; // Ancho de la celda
                params.height = tamanyCeldas; // Altura de la celda
                params.setMargins(8 ,8, 8, 8); // Margenes de la cerda.
                // Aplicamos los parametros
                celda.setLayoutParams(params);

                // Añado las celdas al tablero
                tableroJuego.addView(celda);

                // Guardo las coordenadas de las fichas
                celdas[i][j] = celda;
            }
        }
    }

    private void vaciarTablero(){
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++) {
                celdas[i][j].setText("");
            }
        }
    }

    // Método para colocar las fichas en el tablero de juego
    private void colocarFicha() {
        // Como hay que colocar dos fichas, invoco la función dos veces:
        crearFichaAleatoria();
        crearFichaAleatoria();
    }

    // Métodos para crear las fichas de manera aleatoria
    private void crearFichaAleatoria(){
        // Lista para almacenar las coordenadas de las celdas vacias en el tablero
        List<int []> celdasVacias = new ArrayList<>();

        // Recorremos el tablero en busca de celdas vacias
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                // Si la celda está vacia, añadimos su coordenada a la lista:
                if (celdas[i][j].getText().toString().isEmpty()){
                    celdasVacias.add(new int[]{i, j});
                }
            }
        }

        // Si hay alguna celda vacia, colocamos la ficha al azar en una de ellas.
        if (!celdasVacias.isEmpty()){
            int[] celdaElegida = celdasVacias.get(new Random().nextInt(celdasVacias.size()));
            int fila = celdaElegida[0];
            int columna = celdaElegida[1];

            // Elegir el numero de la ficha:
            // PROBABILIDADES:
            // 90% de ser el NUMERO 2
            // 10% de ser el NUMERO 4
            int numeroFicha;
            if (Math.random() < 0.9){
                numeroFicha = 2;
            } else {
                numeroFicha = 4;
            }

            // Colomas el la ficha en la celda elegida.
            celdas[fila][columna].setText(String.valueOf(numeroFicha));
        }
    }

    // Método para actualizar los colores de las celdas:
    private void actualizarCeldaColores() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String valorCelda = celdas[i][j].getText().toString();

                // Recojo el número de la ficha:
                int valorNumerico = Integer.parseInt(valorCelda.isEmpty() ? "0" : valorCelda);

                // Si el valor numero es superior a 8 el texto será de color blanco
                if (valorNumerico > 8) {
                    celdas[i][j].setTextColor(getResources().getColor(R.color.white));
                } else { // Si el valor numero es inferior a 8 el texto será de color negro
                    celdas[i][j].setTextColor(getResources().getColor(R.color.black));
                }

                // Fondos de las celdas segun su número:
                switch (valorCelda) {
                    case "2":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_dos);
                        break;

                    case "4":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_cuatro);
                        break;

                    case "8":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_ocho);
                        break;

                    case "16":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_dieciseis);
                        break;

                    case "32":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_trenta_y_dos);
                        break;

                    case "64":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_sesenta_y_cuatro);
                        break;

                    case "128":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_cien);
                        break;

                    case "256":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_doscientos);
                        break;

                    case "512":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_quinientos);
                        break;

                    case "1024":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_mil);
                        break;

                    case "2048":
                        celdas[i][j].setBackgroundResource(R.drawable.celda_dosmil);
                        break;

                    default:
                        // Si el valor es superior que 2048, asigno un fondo especial:
                        if (valorNumerico > 2048) {
                            celdas[i][j].setBackgroundResource(R.drawable.celda_mayorvalor);
                        } else {
                            // Si la celda está vacia asigno un valor normal:
                            celdas[i][j].setBackgroundResource(R.drawable.fondo_celda);
                        }

                        break;
                }
            }
        }
    }

    // Método para comprobar si hay movimientos posibles:
    private boolean movimientosPosibles() {
        // Comprueba si hay celdas vacías:
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // Si hay una celda vacía devolvemos verdadero
                if (celdas[i][j].getText().toString().isEmpty()) {
                    return true;
                }

                int valorActual = Integer.parseInt(celdas[i][j].getText().toString());

                // Comprobamos si hay movimientos horizontales:
                if (j < 3) {
                    int valorDerecha = Integer.parseInt(celdas[i][j + 1].getText().toString().isEmpty() ? "0" : celdas[i][j + 1].getText().toString());

                    // Si hay movimientos devolvemos true.
                    if (valorActual == valorDerecha) {
                        return true;
                    }
                }

                // Comprobamos si hay movimientos verticales:
                if (i < 3) {
                    int valorAbajo = Integer.parseInt(celdas[i + 1][j].getText().toString().isEmpty() ? "0" : celdas[i + 1][j].getText().toString());

                    // Si hay movimientos devolvemos true.
                    if (valorActual == valorAbajo) {
                        return true;
                    }
                }
            }
        }
        return false; // Si no hay movimientos posibles ni celdas vacías
    }

    // Método para reiniciar el juego:
    private void reiniciarJuego() {
        // Método para vaciar tablero
        vaciarTablero();

        // Puntuación inicial:
        puntuacion = 0;
        puntuacionTextView.setText("Score: " + puntuacion);
        maximaPuntacionTextView.setText("Best: " + maximaPuntuacion);
        // Actualizar el texto de máxima puntuacion:
        // Coloca las fichas:
        colocarFicha();

        // Actualiza los colores de las celdas:
        actualizarCeldaColores();
    }

    // Método para conseguir la puntuación máxima de la lista:
    private int conseguirMaximaPuntuacion() {
        int puntuacionMax = 0;
        for (int puntuacion : almacenarPuntuacion) {
            if (puntuacion > puntuacionMax) {
                puntuacionMax = puntuacion;
            }
        }
        return puntuacionMax;
    }

    // **Método para llamar al diálogo de derrota**
    private void dialagoDerrota() {
        // **Guardar la puntuación en la base de datos**
        saveGameStats(idUsuarioActual, puntuacion);

        // **Actualizar la puntuación máxima**
        almacenarPuntuacion.add(puntuacion);
        maximaPuntuacion = conseguirMaximaPuntuacion();

        // **Actualizar la UI**
        maximaPuntacionTextView.setText("Best: " + maximaPuntuacion);

        // **Mostrar diálogo de derrota**
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You have lost!")
                .setMessage("Do you want to play again?")
                .setPositiveButton("OK", (dialog, which) -> reiniciarJuego())
                .setCancelable(false)
                .show();
    }

    // Método para almacenar el estado actual del tablero.
    // Este método es necesario para almacenar la última jugada.
    private void estadoActualTablero() {
        // Recorremos el array bidimensional celdas[][]
        // Almacenamos los valores del array celdas en el array anteriorJugada[][]
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                anteriorJugada[i][j] = celdas[i][j].getText().toString();
            }
        }

        puntuacionAnterior = puntuacion;
    }

    // Método para volver a la jugada anterior:
    private void ultimaJugada() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // Volvemos a la jugada anterior del tablero:
                celdas[i][j].setText(anteriorJugada[i][j]);
            }
        }

        // Ponemos la puntuación anterior.
        puntuacion = puntuacionAnterior;
        puntuacionTextView.setText("Score " + puntuacion);
        // Actualizar los colores de las celdas de nuevo:
        actualizarCeldaColores();
    }

    // Método para movimientos hacia la izquierda del tablero de juego.
    private boolean movimientoIzquierda() {
        // Inicializo en "false"
        boolean movimientoRealizadoCorrectamente = false;

        // Recorremos cada fila del tablero:
        for (int i = 0; i < 4; i++) {
            // Lista para almacenar las celdas vacias en el tablero
            boolean[] celdaCombinada = new boolean[4];

            // Recorro cada columna del tablero:
            for (int j = 1; j < 4; j++) {
                // Si la celda actual NO está vacia, intenta moverla hacia la izquierda
                if (!celdas[i][j].getText().toString().isEmpty()) {
                    int z = j; // z es igual a la columna

                    // Movemos la celda hacia la izquierda mientras haya espacios libres.
                    while (z > 0 && celdas[i][z - 1].getText().toString().isEmpty()) {
                        // Mueve el valor de la celda actual a la celda izquierda
                        celdas[i][z - 1].setText(celdas[i][z].getText());
                        // Borra el valor de la celda actual
                        celdas[i][z].setText("");
                        // Continúa desplazando hacia la izquierda
                        z--;
                        // El movimiento se ha realizado correctamente
                        movimientoRealizadoCorrectamente = true;
                    }

                    // Comprueba si se puede fusionar con la celda de la izquierda
                    if (z > 0 && !celdaCombinada[z - 1] && celdas[i][z - 1].getText().toString().equals(celdas[i][z].getText().toString())) {
                        // Si se puede multiplicamos por dosla celda con la otra
                        int nuevoValor = Integer.parseInt(celdas[i][z - 1].getText().toString()) * 2;
                        // Nueva celda con el nuevo valor
                        celdas[i][z - 1].setText(String.valueOf(nuevoValor));
                        // Borramos una celda
                        celdas[i][z].setText("");
                        // Se ha combinado la celda
                        celdaCombinada[z - 1] = true;
                        // Movimiento realizado correctamente
                        movimientoRealizadoCorrectamente = true;

                        // Actualizar la puntuación:
                        actualizarPuntuacion(nuevoValor);
                    }
                }
            }
        }
        return movimientoRealizadoCorrectamente;
    }

    // Método para movimientos hacia la derecha del tablero de juego.
    private boolean movimientoDerecha() {
        // Inicializo en "false"
        boolean movimientoRealizadoCorrectamente = false;

        // Recorremos cada fila del tablero:
        for (int i = 0; i < 4; i++) {
            // Lista para almacenar las celdas vacias en el tablero
            boolean[] celdaCombinada = new boolean[4];

            // Recorro cada columna de tablero
            for (int j = 2; j >= 0; j--) {
                // Si la celda actual NO está vacia, intenta moverla hacia la derecha
                if (!celdas[i][j].getText().toString().isEmpty()) {
                    int z = j; // Indice de la fila actual

                    // Movemos la celda hacia la izquierda mientras haya espacios libres.
                    while (z < 3 && celdas[i][z + 1].getText().toString().isEmpty()) {
                        // Mueve el valor de la celda actual a la celda de la derecha
                        celdas[i][z + 1].setText(celdas[i][z].getText());
                        // Borra el valor de la celda actual
                        celdas[i][z].setText("");
                        z++; // Continuamos el desplazamiento hacia la derecha.
                        movimientoRealizadoCorrectamente = true; // Movimiento hecho correctamente.
                    }

                    // Comprueba si se puede fusionar con la celda de la izquierda
                    if (z < 3 && !celdaCombinada[z + 1] && celdas[i][z + 1].getText().toString().equals(celdas[i][z].getText().toString())) {
                        // Si se puede, el nuevo valor lo multiplicamos por dos por la celda
                        int nuevoValor = Integer.parseInt(celdas[i][z + 1].getText().toString()) * 2;
                        // Implementa el nuevo valor a la celda de la derecha
                        celdas[i][z + 1].setText(String.valueOf(nuevoValor));
                        // Vaciamos la celda que está en la parte izquierda
                        celdas[i][z].setText("");
                        // Se ha combinado la celda de la derecha
                        celdaCombinada[z + 1] = true;
                        // El movimiento se ha realizado correctamente.
                        movimientoRealizadoCorrectamente = true;

                        // Actualizar la puntuación:
                        actualizarPuntuacion(nuevoValor);
                    }
                }
            }
        }
        return movimientoRealizadoCorrectamente;
    }

    // Método para movimientos hacia arriba del tablero de juego.
    private boolean movimientoArriba() {
        // Inicializo el movimiento correcto en "false"
        boolean movimientoRealizadoCorrectamente = false;

        // Lista para almacenar las celdas vacias en el tablero
        for (int j = 0; j < 4; j++) {
            // Arreglo para tener el control de las combinaciones de la columan actual
            boolean[] celdaCombinada = new boolean[4];
            // Recorre todas las filas del tablero de juego:
            for (int i = 1; i < 4; i++) {
                // Compruebo que la celda NO este vacia
                if (!celdas[i][j].getText().toString().isEmpty()) {
                    int z = i; // Indice de la fila actual

                    // Muevo hacia arriba las fichas si hay espacio en la parte superior
                    while (z > 0 && celdas[z - 1][j].getText().toString().isEmpty()) {
                        // Movemos la ficha a la parte superior
                        celdas[z - 1][j].setText(celdas[z][j].getText());
                        // Eliminamos / Borramos las fichas de abajo
                        celdas[z][j].setText("");
                        z--; // Continuamos moviendo las fichas hacia arriba
                        movimientoRealizadoCorrectamente = true; // Se ha realizado correcamente el movimiento
                    }

                    // Combinamos las fichas que tengan el mismo valor
                    if (z > 0 && !celdaCombinada[z - 1] && celdas[z - 1][j].getText().toString().equals(celdas[z][j].getText().toString())) {
                        // Multiplico la ficha por dos
                        int nuevoValor = Integer.parseInt(celdas[z - 1][j].getText().toString()) * 2;
                        // El resultado anterior será el nuevo valor de la ficha
                        celdas[z - 1][j].setText(String.valueOf(nuevoValor));
                        // Eliminamos la ficha que ha venido de mas abajo
                        celdas[z][j].setText("");
                        // Se ha combinado la celda
                        celdaCombinada[z - 1] = true;
                        movimientoRealizadoCorrectamente = true; // El movimiento ha sido correcto

                        // Actualizar la puntuación:
                        actualizarPuntuacion(nuevoValor);
                    }
                }
            }
        }
        return movimientoRealizadoCorrectamente;
    }

    // Método para movimientos hacia abajo del tablero de juego.
    private boolean movimientoAbajo() {
        // Inicializo el movimiento correcto en "false"
        boolean movimientoRealizadoCorrectamente = false;

        // Recorro todas las columnas del tablero de juego:
        for (int j = 0; j < 4; j++) {
            // Lista para almacenar las celdas vacias en el tablero
            boolean[] celdaCombinada = new boolean[4];

            // Recorre todas las filas del tablero de juego:
            for (int i = 2; i >= 0; i--) {
                // Compruebo que la celda NO este vacia
                if (!celdas[i][j].getText().toString().isEmpty()) {
                    // Indice de la fila actual
                    int z = i;

                    // Muevo hacia arriba las fichas si hay espacio en la parte superior
                    while (z < 3 && celdas[z + 1][j].getText().toString().isEmpty()) {
                        // Movemos la ficha a la parte superior
                        celdas[z + 1][j].setText(celdas[z][j].getText());
                        // Vaciamos las fichas de abajo
                        celdas[z][j].setText("");
                        z++; // Continuamos moviendo las fichas hacia abajo
                        movimientoRealizadoCorrectamente = true; // Movimiento hecho correctamente
                    }

                    // Combinamos las fichas que tengan el mismo valor
                    if (z < 3 && !celdaCombinada[z + 1] && celdas[z + 1][j].getText().toString().equals(celdas[z][j].getText().toString())) {
                        // Multiplico la ficha por dos
                        int nuevoValor = Integer.parseInt(celdas[z + 1][j].getText().toString()) * 2;
                        // El resultado anterior será el nuevo valor de la ficha
                        celdas[z + 1][j].setText(String.valueOf(nuevoValor));
                        // Vaciamos la ficha de mas arriba
                        celdas[z][j].setText("");
                        // Se ha combinado la celda
                        celdaCombinada[z + 1] = true;
                        movimientoRealizadoCorrectamente = true; // Movimiento correcto

                        // Actualizar la puntuación:
                        actualizarPuntuacion(nuevoValor);
                    }
                }
            }
        }
        return movimientoRealizadoCorrectamente;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detectorMovimientos.onTouchEvent(event) || super.onTouchEvent(event);
    }

    // Método para actualizar la puntuacion:
    private void actualizarPuntuacion(int puntosObtenidos) {
        puntuacion += puntosObtenidos;
        puntuacionTextView.setText("Score: " + puntuacion);
    }
}