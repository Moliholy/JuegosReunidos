package molina.viewpager;

import molina.buscaminas.Buscaminas;
import molina.mensajes.instrucciones.Instrucciones;
import molina.resources.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BuscaminasMainFragment extends Fragment implements OnClickListener {
	private Button continuar;
	public static final String PARTIDA_JUGABLE = "PARTIDA_JUGABLE";
	public static final String KEY = BuscaminasMainFragment.class.getName();

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		View continuar = getActivity().findViewById(
				R.id.continuar_boton_buscaminas);
		this.continuar = (Button) continuar;
		continuar.setOnClickListener(this);
		View juego_nuevo = getActivity().findViewById(
				R.id.juego_nuevo_boton_buscaminas);
		juego_nuevo.setOnClickListener(this);
		View instrucciones = getActivity().findViewById(
				R.id.instrucciones_boton_buscaminas);
		instrucciones.setOnClickListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.buscaminas_main, null);
	}

	@Override
	public void onResume() {
		super.onResume();
		comprobarContinuar();
	}

	public void onClick(View v) {
		Intent i;
		int id = v.getId();
		switch (id) {
		case R.id.continuar_boton_buscaminas:
			i = new Intent(getActivity(), Buscaminas.class);
			i.putExtra(Buscaminas.CONTINUAR, true);
			startActivity(i);
			break;
		case R.id.instrucciones_boton_buscaminas:
			new Instrucciones(getActivity(), Buscaminas.KEY).show();
			break;
		case R.id.juego_nuevo_boton_buscaminas:
			elegirDificultad();
			break;
		}
	}

	public void comprobarContinuar() {
		getActivity();
		boolean c = getActivity().getSharedPreferences(KEY,
				Context.MODE_PRIVATE).getBoolean(PARTIDA_JUGABLE, false);
		continuar.setEnabled(c);
	}

	private void comenzarJuego(int dif) {
		Intent i = new Intent(getActivity(), Buscaminas.class);
		i.putExtra(Buscaminas.CONTINUAR, false);
		i.putExtra(Buscaminas.DIFICULTAD, dif);
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
