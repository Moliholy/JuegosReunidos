package molina.viewpager;

import molina.mensajes.instrucciones.Instrucciones;
import molina.resources.R;
import molina.sudoku.Sudoku;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class SudokuMainFragment extends Fragment implements OnClickListener {

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Activity act = getActivity();
		View continuar = act.findViewById(R.id.continuar_boton_sudoku);
		continuar.setOnClickListener(this);
		View juego_nuevo = act.findViewById(R.id.juego_nuevo_boton_sudoku);
		juego_nuevo.setOnClickListener(this);
		View instrucciones = act.findViewById(R.id.instrucciones_boton_sudoku);
		instrucciones.setOnClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sudoku_main, null);
	}

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.continuar_boton_sudoku:
			i = new Intent(getActivity(), Sudoku.class);
			i.putExtra(Sudoku.CONTINUAR, true);
			startActivity(i);
			break;
		case R.id.instrucciones_boton_sudoku:
			new Instrucciones(getActivity(), Sudoku.KEY).show();
			break;
		case R.id.juego_nuevo_boton_sudoku:
			elegirDificultad();
			break;
		}
	}

	private void comenzarJuego(int dif) {
		Intent i = new Intent(getActivity(), Sudoku.class);
		i.putExtra(Sudoku.CONTINUAR, false);
		i.putExtra(Sudoku.DIFICULTAD, dif);
		startActivity(i);
	}

	private void elegirDificultad() {
		new AlertDialog.Builder(getActivity())
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
