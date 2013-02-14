package molina.main;

import molina.buscaminas.Buscaminas;
import molina.mensajes.instrucciones.Instrucciones;
import molina.resources.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BuscaminasMainActivity extends Activity implements OnClickListener {
	private Button continuar;
	public static final String PARTIDA_JUGABLE = "PARTIDA_JUGABLE";
	public static final String KEY = BuscaminasMainActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buscaminas_main);
		View continuar = findViewById(R.id.continuar_boton_buscaminas);
		this.continuar = (Button) continuar;
		continuar.setOnClickListener(this);
		View juego_nuevo = findViewById(R.id.juego_nuevo_boton_buscaminas);
		juego_nuevo.setOnClickListener(this);
		View instrucciones = findViewById(R.id.instrucciones_boton_buscaminas);
		instrucciones.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		comprobarContinuar();
	}

	public void onClick(View v) {
		Intent i;
		int id = v.getId();
		switch (id) {
		case R.id.continuar_boton_buscaminas:
			i = new Intent(this, Buscaminas.class);
			i.putExtra(Buscaminas.CONTINUAR, true);
			startActivity(i);
			break;
		case R.id.instrucciones_boton_buscaminas:
			new Instrucciones(this, Buscaminas.KEY).show();
			break;
		case R.id.juego_nuevo_boton_buscaminas:
			elegirDificultad();
			break;
		}
	}

	public void comprobarContinuar() {
		boolean c = getSharedPreferences(KEY, MODE_PRIVATE).getBoolean(
				PARTIDA_JUGABLE, false);
		continuar.setEnabled(c);
	}

	private void comenzarJuego(int dif) {
		Intent i = new Intent(this, Buscaminas.class);
		i.putExtra(Buscaminas.CONTINUAR, false);
		i.putExtra(Buscaminas.DIFICULTAD, dif);
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
