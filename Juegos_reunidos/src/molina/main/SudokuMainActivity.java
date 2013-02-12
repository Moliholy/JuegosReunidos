package molina.main;

import molina.mensajes.instrucciones.Instrucciones;
import molina.resources.R;
import molina.sudoku.Sudoku;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SudokuMainActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// hacemos que se muestre la vista definida en el xml
		setContentView(R.layout.sudoku_main);
		// cargamos los tres botones y les ponemos a cada uno un listener para
		// enterarnos de cuando se pulsan. Es por ello que la clase tiene que
		// implementar la interfaz OnClickListener
		View continuar = findViewById(R.id.continuar_boton_sudoku);
		continuar.setOnClickListener(this);
		View juego_nuevo = findViewById(R.id.juego_nuevo_boton_sudoku);
		juego_nuevo.setOnClickListener(this);
		View instrucciones = findViewById(R.id.instrucciones_boton_sudoku);
		instrucciones.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.continuar_boton_sudoku:
			i = new Intent(this, Sudoku.class);
			i.putExtra(Sudoku.CONTINUAR, true);
			startActivity(i);
			break;
		case R.id.instrucciones_boton_sudoku:
			new Instrucciones(this, Sudoku.KEY).show();
			break;
		case R.id.juego_nuevo_boton_sudoku:
			elegirDificultad();
			break;
		}
	}

	private void comenzarJuego(int dif) {
		Intent i = new Intent(this, Sudoku.class);
		i.putExtra(Sudoku.CONTINUAR, false);
		i.putExtra(Sudoku.DIFICULTAD, dif);
		startActivity(i);
	}

	private void elegirDificultad() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.elegir_dificultad)
				.setItems(R.array.dificultad,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								comenzarJuego(which);
							}
						}).show();
	}
}
