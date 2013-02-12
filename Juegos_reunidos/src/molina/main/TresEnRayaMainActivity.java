package molina.main;

import molina.mensajes.instrucciones.Instrucciones;
import molina.resources.R;
import molina.tresRaya.TresEnRaya;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TresEnRayaMainActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tres_en_raya_main);
		View continuar = findViewById(R.id.continuar_boton_tres_en_raya);
		continuar.setOnClickListener(this);
		View juego_nuevo = findViewById(R.id.juego_nuevo_boton_tres_en_raya);
		juego_nuevo.setOnClickListener(this);
		View instrucciones = findViewById(R.id.instrucciones_boton_tres_en_raya);
		instrucciones.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.continuar_boton_tres_en_raya:
			i = new Intent(this, TresEnRaya.class);
			i.putExtra(TresEnRaya.GUARDAR, true);
			startActivity(i);
			break;
		case R.id.juego_nuevo_boton_tres_en_raya:
			elegirNumJugadores();
			break;
		case R.id.instrucciones_boton_tres_en_raya:
			new Instrucciones(this, TresEnRaya.KEY).show();
			break;
		}
	}

	private void comenzarJuego(int numJugadores, int dificultad) {
		Intent i = new Intent(this, TresEnRaya.class);
		i.putExtra(TresEnRaya.GUARDAR, false);
		i.putExtra(TresEnRaya.NUM_JUGADORES, numJugadores);
		i.putExtra(TresEnRaya.DIFICULTAD, dificultad);
		startActivity(i);
	}

	private void elegirNumJugadores() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.elegir_num_jugadores)
				.setItems(R.array.num_jugadores,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int num) {
								if (num == 1)
									comenzarJuego(num + 1, 0);
								else
									elegirDificultad();
							}
						}).show();
	}

	private void elegirDificultad() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.elegir_dificultad)
				.setItems(R.array.dificultad_tres_en_raya,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								comenzarJuego(1, which + 1);
							}
						}).show();
	}
}
