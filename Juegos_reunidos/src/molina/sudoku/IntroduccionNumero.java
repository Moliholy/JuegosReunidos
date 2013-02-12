package molina.sudoku;

import android.os.Bundle;
import android.view.View;
import android.app.Dialog;
import android.content.Context;
import molina.ia.generadorSudokus.*;
import molina.resources.R;

public class IntroduccionNumero extends Dialog {
	private boolean[] valoresDisponibles;
	private Sudoku sudoku;
	private int fila;
	private int columna;
	private final View[] botones = new View[9];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.introducir_numero_titulo);
		setContentView(R.layout.introduccion_numero);
		establecerBotones();
		establecerListeners();
	}

	private void establecerBotones() {
		botones[0] = findViewById(R.id.boton_1);
		botones[1] = findViewById(R.id.boton_2);
		botones[2] = findViewById(R.id.boton_3);
		botones[3] = findViewById(R.id.boton_4);
		botones[4] = findViewById(R.id.boton_5);
		botones[5] = findViewById(R.id.boton_6);
		botones[6] = findViewById(R.id.boton_7);
		botones[7] = findViewById(R.id.boton_8);
		botones[8] = findViewById(R.id.boton_9);
	}

	private void establecerListeners() {
		for (int i = 0; i < botones.length; i++)
			// si el valor en cuestion puede ser insertado, y por tanto debe
			// estar disponible su selección
			if (!valoresDisponibles[i]
					|| sudoku.getDificultad() != Dificultad.FACIL) {
				final int numero = i + 1;
				botones[i].setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						introducirNumero(numero);
					}
				});
				// volvemos invisibles los números que no puedan ser
				// introducidos si estamos en dificultad FACIL
			} else if (sudoku.getDificultad() == Dificultad.FACIL)
				botones[i].setVisibility(View.INVISIBLE);
	}

	private void introducirNumero(int numero) {
		sudoku.introducirNumero(fila, columna, numero);
		dismiss();
	}

	public IntroduccionNumero(Context context, boolean[] valoresDisponibles,
			Sudoku sudoku, int fila, int columna) {
		super(context);
		this.valoresDisponibles = valoresDisponibles;
		this.sudoku = sudoku;
		this.fila = fila;
		this.columna = columna;
	}
}
