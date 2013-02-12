package molina.sudoku;

import molina.ia.generadorSudokus.*;
import molina.mensajes.juegoTerminado.*;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

// esta actividad controla la lógica del sudoku, y almacena todo el proceso de
// juego (pero no genera el sudoku)
// la View asociada viene dada por una clase aparte, llamada SudokuView
public class Sudoku extends Activity {
	public static final String KEY = "SUDOKU";
	private SudokuView tablero;
	private int[][] numeros;
	private int[][] solucion;
	private Dificultad dificultad;
	public static final String CONTINUAR = "CONTINUAR";
	public static final String DIFICULTAD = "DIFICULTAD";
	public static final String NUMEROS = "numeros";
	public static final String SOLUCION = "solucion";
	public static final String CURSOR = "cursor";
	public static final int TAM = 9;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tablero = new SudokuView(this);
		boolean continuar = getIntent().getExtras().getBoolean(CONTINUAR);
		if (!continuar) {
			dificultad = establecerDificultad(getIntent().getExtras().getInt(
					DIFICULTAD));
			GeneradorSudoku generador = new GeneradorSudoku();
			numeros = generador.generarSolucionParcial(dificultad);
			solucion = generador.getMatriz();
		} else {
			// cargamos los archivos almacenados en memoria
			numeros = fromSavedString(getPreferences(MODE_PRIVATE).getString(
					NUMEROS, null));
			dificultad = establecerDificultad(getPreferences(MODE_PRIVATE)
					.getInt(DIFICULTAD, 0));
			solucion = fromSavedString(getPreferences(MODE_PRIVATE).getString(
					SOLUCION, null));
			int cursor = getPreferences(MODE_PRIVATE).getInt(CURSOR, 0);
			tablero.cambiarRectangulo(cursor / TAM, cursor % TAM);
			if (numeros == null || solucion == null)
				finish();
		}
		setContentView(tablero);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(CURSOR, tablero.getPosicionCursor());
		outState.putIntArray(SOLUCION, toSave(solucion));
		outState.putIntArray(NUMEROS, toSave(numeros));
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		int cursor = savedInstanceState.getInt(CURSOR);
		tablero.cambiarRectangulo(cursor / TAM, cursor % TAM);
		numeros = fromSaved(savedInstanceState.getIntArray(NUMEROS));
		solucion = fromSaved(savedInstanceState.getIntArray(SOLUCION));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Editor edit = getPreferences(MODE_PRIVATE).edit();
		edit.putInt(DIFICULTAD, dificultad.ordinal());
		edit.putInt(CURSOR, tablero.getPosicionCursor());
		edit.putString(NUMEROS, toSaveString(numeros)).commit();
		edit.putString(SOLUCION, toSaveString(solucion)).commit();
	}

	private int[][] fromSavedString(String string) {
		if (string != null) {
			int[][] devolver = new int[TAM][TAM];
			for (int i = 0; i < TAM; i++)
				for (int j = 0; j < TAM; j++)
					devolver[i][j] = string.charAt(i * TAM + j) - '0';
			// le tenemos que quitar el valor ASCII del 0, que es 48, sino no
			// funciona
			return devolver;
		}
		return null;
	}

	private int[][] fromSaved(int[] intArray) {
		int[][] devolver = new int[TAM][TAM];
		for (int i = 0; i < TAM; i++)
			for (int j = 0; j < TAM; j++)
				devolver[i][j] = intArray[i * TAM + j];
		return devolver;
	}

	private int[] toSave(int[][] solucion) {
		int[] devolver = new int[TAM * TAM];
		for (int i = 0; i < TAM; i++)
			for (int j = 0; j < TAM; j++)
				devolver[TAM * i + j] = solucion[i][j];
		return devolver;
	}

	private String toSaveString(int[][] matriz) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < TAM; i++)
			for (int j = 0; j < TAM; j++)
				sb.append(matriz[i][j]);
		return sb.toString();
	}

	private boolean sudokuCompletado() {
		for (int i = 0; i < TAM; i++)
			for (int j = 0; j < TAM; j++)
				if (numeros[i][j] == 0)
					return false;
		return true;
	}

	public Dificultad getDificultad() {
		return dificultad;
	}

	public int[][] getSolucion() {
		return solucion;
	}

	public int[][] getNumeros() {
		return numeros;
	}

	public boolean[] getNumerosDisponibles(int fila, int columna) {
		boolean[] devolver = new boolean[TAM];
		// comprobamos los valores de la sección
		int I = (fila / 3) * 3;
		int J = (columna / 3) * 3;
		for (int i = I; i < I + 3; i++)
			for (int j = J; j < J + 3; j++)
				if (numeros[i][j] != 0)
					devolver[numeros[i][j] - 1] = true;
		// comprobamos la fila y columna a la que pertenece la casilla
		for (int i = 0; i < TAM; i++) {
			// tenemos que restarle uno, ya que los números van de 1 a TAM, y
			// las
			// posiciones del vector de 0 a 8
			if (numeros[fila][i] != 0)
				devolver[numeros[fila][i] - 1] = true;
			if (numeros[i][columna] != 0)
				devolver[numeros[i][columna] - 1] = true;
		}
		return devolver;
	}

	public boolean casillaRellena(int fila, int columna) {
		return numeros[fila][columna] != 0;
	}

	public static Dificultad establecerDificultad(int num) {
		switch (num) {
		case 0:
			return Dificultad.FACIL;
		case 1:
			return Dificultad.NORMAL;
		case 2:
			return Dificultad.DIFICIL;
		default:
			return null;
		}
	}

	public void introducirNumero(int fila, int columna, int numero) {
		if (solucion[fila][columna] == numero && numeros[fila][columna] == 0) {
			numeros[fila][columna] = numero;
			tablero.invalidate();
			if (sudokuCompletado()) {
				// si hemos terminado mostramos un mensaje final y concluimos la
				// actividad
				Dialog juegoTerminado = new JuegoTerminado(this, KEY);
				juegoTerminado.show();
			}
		}
	}
}