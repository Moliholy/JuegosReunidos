package molina.tresRaya;

import java.util.Random;

import molina.ia.tresEnRaya.JugadaTresEnRaya;
import molina.mensajes.juegoTerminado.JuegoTerminado;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

public class TresEnRaya extends Activity {
	private TresEnRayaView tablero;
	private char[][] matriz;
	private EstadoTresEnRaya estadoJuego;
	private int numJugadores;
	private char turno;
	private static int dificultad;
	public static final int TAM = 3;
	public static final char JUGADOR1 = 'O';
	public static final char JUGADOR2 = 'X';
	public static final char VACIO = '-';
	public static final String KEY = "TRES EN RAYA";
	public static final String GUARDAR = "tablero";
	public static final String NUM_JUGADORES = "numeroJugadores";
	public static final String TURNO = "turno";
	public static final String DIFICULTAD = "dificultad";

	public static int getDificultad() {
		return dificultad;
	}

	private boolean tableroLLeno() {
		for (int i = 0; i < TAM; i++)
			for (int j = 0; j < TAM; j++)
				if (matriz[i][j] == VACIO)
					return false;
		return true;
	}

	private EstadoTresEnRaya jugadorGanador(char jugador) {
		if (jugador == JUGADOR1)
			return EstadoTresEnRaya.JUGADOR1;
		else if (jugador == JUGADOR2)
			return EstadoTresEnRaya.JUGADOR2;
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean continuar = getIntent().getExtras().getBoolean(GUARDAR);
		numJugadores = getIntent().getExtras().getInt(NUM_JUGADORES);
		dificultad = getIntent().getExtras().getInt(DIFICULTAD);
		if (!continuar) {
			turno = JUGADOR1;
			Random random = new Random();
			estadoJuego = EstadoTresEnRaya.SIN_CAMBIOS;
			matriz = new char[TAM][TAM];
			for (int i = 0; i < TAM; i++)
				for (int j = 0; j < TAM; j++)
					matriz[i][j] = VACIO;
			if (random.nextBoolean() && numJugadores == 1) {
				turno = JUGADOR2;
				introducirFicha(1, 1);
			}
		} else {
			// madre mia la que hay que liar aqui para recuperar el turno, ya
			// podian poner un getChar() -.-'
			turno = getPreferences(MODE_PRIVATE).getString(TURNO,
					String.valueOf(JUGADOR1)).charAt(0);
			numJugadores = getPreferences(MODE_PRIVATE)
					.getInt(NUM_JUGADORES, 1);
			matriz = fromSavedString(getPreferences(MODE_PRIVATE).getString(
					GUARDAR, null));
			dificultad = getPreferences(MODE_PRIVATE).getInt(DIFICULTAD, 6);
			comprobarEstado();
		}
		tablero = new TresEnRayaView(this);
		setContentView(tablero);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		dificultad = savedInstanceState.getInt(DIFICULTAD);
		turno = savedInstanceState.getChar(TURNO);
		numJugadores = savedInstanceState.getInt(NUM_JUGADORES);
		matriz = fromSavedString(savedInstanceState.getString(GUARDAR));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(DIFICULTAD, dificultad);
		outState.putInt(NUM_JUGADORES, numJugadores);
		outState.putChar(TURNO, turno);
		outState.putString(GUARDAR, toSaveString());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferences(MODE_PRIVATE).edit().putInt(DIFICULTAD, dificultad);
		getPreferences(MODE_PRIVATE).edit().putString(TURNO,
				String.valueOf(turno));
		getPreferences(MODE_PRIVATE).edit().putInt(NUM_JUGADORES, numJugadores)
				.commit();
		getPreferences(MODE_PRIVATE).edit().putString(GUARDAR, toSaveString())
				.commit();
	}

	public char getTurno() {
		return turno;
	}

	public int getNumJugadores() {
		return numJugadores;
	}

	private void cambiarTurno() {
		if (turno == JUGADOR1)
			turno = JUGADOR2;
		else
			turno = JUGADOR1;
	}

	private String toSaveString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < TAM * TAM; i++)
			sb.append(matriz[i / TAM][i % TAM]);
		return sb.toString();
	}

	private char[][] fromSavedString(String string) {
		if (string.length() == TAM * TAM) {
			char[][] devolver = new char[TAM][TAM];
			for (int i = 0; i < string.length(); i++) {
				int fila = i / TAM;
				int columna = i % TAM;
				devolver[fila][columna] = string.charAt(i);
			}
			return devolver;
		}
		return null;
	}

	public EstadoTresEnRaya getEstadoJuego() {
		return estadoJuego;
	}

	public char[][] getMatriz() {
		return matriz;
	}

	public boolean casillaRellena(int x, int y) {
		return matriz[x][y] != VACIO;
	}

	public void introducirFicha(int x, int y) {
		matriz[x][y] = turno;
		cambiarTurno();
	}

	// lanza la IA del tres en raya
	public void pensarJugada() {
		Integer casilla = new JugadaTresEnRaya(matriz, 0).getJugadaOptima();
		introducirFicha(casilla / TresEnRaya.TAM, casilla % TresEnRaya.TAM);
	}

	public EstadoTresEnRaya comprobarEstado() {
		// comprobamos el estado del tablero y devolvemos la situacion
		// 1º) Comprobamos si alguien ha ganado
		for (int i = 0; i < TAM; i++) {
			char jugador = matriz[i][0];
			if (jugador != VACIO) {
				int contador = 1;
				for (int j = 1; j < TAM; j++)
					if (matriz[i][j] == jugador)
						contador++;
				if (contador == TAM)
					return estadoJuego = jugadorGanador(jugador);
			}
		}

		// 2º) Comprobamos las columnas
		for (int i = 0; i < TAM; i++) {
			char jugador = matriz[0][i];
			if (jugador != '-') {
				int contador = 1;
				for (int j = 1; j < TAM; j++)
					if (matriz[j][i] == jugador)
						contador++;
				if (contador == TAM)
					return estadoJuego = jugadorGanador(jugador);
			}
		}

		// 3º) Comprobamos las diagonales
		// diagonal de (0,0) a (2,2)
		char jugador = matriz[0][0];
		if (jugador != '-') {
			int contador = 1;
			for (int i = 1, j = 1; i < TAM && j < TAM; i++, j++)
				if (matriz[i][j] == jugador)
					contador++;
			if (contador == TAM)
				return estadoJuego = jugadorGanador(jugador);
		}
		// diagonal de (0,2) a (2,0)
		jugador = matriz[0][TAM - 1];
		if (jugador != '-') {
			int contador = 1;
			for (int i = 1, j = TAM - 2; i < TAM && j >= 0; i++, j--)
				if (matriz[i][j] == jugador)
					contador++;
			if (contador == TAM)
				return estadoJuego = jugadorGanador(jugador);
		}

		// Llegados a este punto, sabemos que no ha ganado nadie. Comprobamos
		// pués si es empate
		if (tableroLLeno())
			return estadoJuego = EstadoTresEnRaya.EMPATE;

		// si no era empate, entonces podemos seguir con el juego
		return estadoJuego = EstadoTresEnRaya.SIN_CAMBIOS;
	}

	public void juegoTerminado() {
		Dialog juegoTerminado = new JuegoTerminado(this, KEY);
		juegoTerminado.show();
	}
}
