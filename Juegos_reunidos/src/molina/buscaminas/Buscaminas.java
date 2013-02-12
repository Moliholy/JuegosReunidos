package molina.buscaminas;

import java.util.Random;

import molina.ia.generadorSudokus.Dificultad;
import molina.main.BuscaminasMainActivity;
import molina.mensajes.juegoTerminado.JuegoTerminado;
import molina.resources.R;
import molina.widget.cronometro.Cronometro;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Buscaminas extends Activity {
	private BuscaminasView tablero;
	private int[][] matriz;
	private int[][] solucion;
	private EstadoBuscaminas estadoJuego;
	private Dificultad dificultad;
	public static final int TAM = 9;
	private Cronometro crono;
	private long baseCrono;
	public static final String KEY = "BUSCAMINAS";
	public static final String CONTINUAR = "CONTINUAR";
	public static final String DIFICULTAD = "DIFICULTAD";
	public static final String MATRIZ = "MATRIZ";
	public static final String SOLUCION = "SOLUCION";
	public static final String TIEMPO = "TIEMPO";
	public static final String PARTIDA_JUGABLE = "PARTIDA JUGABLE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buscaminas_composicion);
		crono = (Cronometro) (findViewById(R.id.crono));
		Button generar = (Button) findViewById(R.id.generar_buscaminas);
		generar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = getIntent();
				i.removeExtra(CONTINUAR);
				i.putExtra(CONTINUAR, false);
				i.putExtra(DIFICULTAD, dificultad.ordinal());
				startActivity(getIntent());
				finish();
			}
		});
		tablero = (BuscaminasView) findViewById(R.id.BuscaminasView);
		boolean continuar = getIntent().getExtras().getBoolean(CONTINUAR);
		if (!continuar) {
			estadoJuego = EstadoBuscaminas.SIN_CAMBIOS;
			dificultad = establecerDificultad(getIntent().getExtras().getInt(
					DIFICULTAD));
			// generamos la solucion
			solucion = generarBuscaminas();
			// y a continuación el tablero para interactuar a -2 (casilla negra)
			matriz = new int[Buscaminas.TAM][Buscaminas.TAM];
			for (int i = 0; i < Buscaminas.TAM; i++)
				for (int j = 0; j < Buscaminas.TAM; j++)
					matriz[i][j] = -2;
		} else {
			crono.setBase(getPreferences(MODE_PRIVATE).getLong(TIEMPO, 0L));
			matriz = fromSavedString(getPreferences(MODE_PRIVATE).getString(
					MATRIZ, null));
			solucion = fromSavedString(getPreferences(MODE_PRIVATE).getString(
					SOLUCION, null));
			dificultad = establecerDificultad(getPreferences(MODE_PRIVATE)
					.getInt(DIFICULTAD, 0));
			if (matriz == null || solucion == null)
				finish();
			else {
				estadoJuego = comprobarEstado();
				if (estadoJuego != EstadoBuscaminas.SIN_CAMBIOS)
					finish();
			}
		}
		TextView text = (TextView) findViewById(R.id.numero_minas);
		text.setText(Integer.toString(getNumeroMinas()));
	}

	public int getNumeroMinas() {
		return TAM + dificultad.ordinal() * 3;
	}

	// hace de generador del buscaminas
	private int[][] generarBuscaminas() {
		Random random = new Random();
		int[][] matriz = new int[TAM][TAM];
		int numMinas = getNumeroMinas();
		for (int i = 0; i < numMinas; i++) {
			int fila;
			int columna;
			do {
				fila = random.nextInt(TAM);
				columna = random.nextInt(TAM);
			} while (matriz[fila][columna] == -1);
			matriz[fila][columna] = -1;
			for (int j = fila - 1; j <= fila + 1; j++)
				for (int k = columna - 1; k <= columna + 1; k++)
					if (j >= 0 && k >= 0 && j < TAM && k < TAM
							&& matriz[j][k] != -1)
						matriz[j][k]++;
		}
		return matriz;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		crono.stop();
		baseCrono = SystemClock.elapsedRealtime() - crono.getBase();
		outState.putLong(TIEMPO, baseCrono);
		outState.putInt(DIFICULTAD, dificultad.ordinal());
		outState.putIntArray(SOLUCION, toSave(solucion));
		outState.putIntArray(MATRIZ, toSave(matriz));
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (estadoJuego == EstadoBuscaminas.SIN_CAMBIOS)
			crono.start();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		crono.setBase(SystemClock.elapsedRealtime()
				- savedInstanceState.getLong(TIEMPO));
		dificultad = establecerDificultad(savedInstanceState.getInt(DIFICULTAD));
		matriz = fromSaved(savedInstanceState.getIntArray(MATRIZ));
		solucion = fromSaved(savedInstanceState.getIntArray(SOLUCION));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (estadoJuego == EstadoBuscaminas.SIN_CAMBIOS)
			crono.stop();
		getSharedPreferences(BuscaminasMainActivity.KEY, MODE_PRIVATE)
				.edit()
				.putBoolean(BuscaminasMainActivity.PARTIDA_JUGABLE,
						estadoJugable()).commit();

		Editor edit = getPreferences(MODE_PRIVATE).edit();
		edit.putLong(TIEMPO, SystemClock.elapsedRealtime() - crono.getBase())
				.commit();
		edit.putInt(DIFICULTAD, dificultad.ordinal()).commit();
		edit.putString(MATRIZ, toSaveString(matriz)).commit();
		edit.putString(SOLUCION, toSaveString(solucion)).commit();
		tablero.stopCambioFondos();
	}

	private boolean estadoJugable() {
		return estadoJuego == EstadoBuscaminas.SIN_CAMBIOS;
	}

	public static boolean getJugable(Activity act) {
		return act.getPreferences(MODE_PRIVATE).getBoolean(
				Buscaminas.PARTIDA_JUGABLE, false);
	}

	public int[][] getSolucion() {
		return solucion;
	}

	private int[][] fromSavedString(String string) {
		if (string != null) {
			int contador = 0;
			int[][] devolver = new int[TAM][TAM];
			for (int i = 0; i < TAM; i++)
				for (int j = 0; j < TAM; j++) {
					int num;
					String resultado = Character.toString(string
							.charAt(contador++));
					if (resultado.equals("-")) {
						num = Integer
								.parseInt(resultado
										+ Character.toString(string
												.charAt(contador++)));
					} else
						num = Integer.parseInt(resultado);
					devolver[i][j] = num;
				}
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
				sb.append(String.valueOf(matriz[i][j]));
		return sb.toString();
	}

	public Dificultad getDificultad() {
		return dificultad;
	}

	public int[][] getMatriz() {
		return matriz;
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

	public void ejecutarJugada(int x, int y) {
		if (solucion[x][y] == 0) {
			destaparCasilla(x, y);
		} else
			matriz[x][y] = solucion[x][y];
	}

	private void destaparCasilla(int x, int y) {
		if (x >= 0 && y >= 0 && x < TAM && y < TAM && matriz[x][y] == -2) {
			matriz[x][y] = solucion[x][y];
			if (matriz[x][y] == 0) {
				destaparCasilla(x - 1, y);
				destaparCasilla(x, y - 1);
				destaparCasilla(x + 1, y);
				destaparCasilla(x, y + 1);

				destaparCasilla(x - 1, y + 1);
				destaparCasilla(x + 1, y + 1);
				destaparCasilla(x - 1, y - 1);
				destaparCasilla(x + 1, y - 1);
			}
		}
	}

	public EstadoBuscaminas comprobarEstado() {
		int contador = 0;
		for (int i = 0; i < Buscaminas.TAM; i++)
			for (int j = 0; j < Buscaminas.TAM; j++) {
				if (matriz[i][j] == -1)
					return estadoJuego = EstadoBuscaminas.MINA_PISADA;
				else if (matriz[i][j] == -2)
					contador++;
			}
		if (contador == Buscaminas.TAM + 3 * dificultad.ordinal())
			return estadoJuego = EstadoBuscaminas.JUEGO_COMPLETADO;
		return estadoJuego = EstadoBuscaminas.SIN_CAMBIOS;
	}

	public EstadoBuscaminas getEstadoJuego() {
		return estadoJuego;
	}

	public void juegoTerminado() {
		crono.stop();
		matriz = solucion;
		tablero.invalidate();
		new JuegoTerminado(this, KEY).show();
	}
}
