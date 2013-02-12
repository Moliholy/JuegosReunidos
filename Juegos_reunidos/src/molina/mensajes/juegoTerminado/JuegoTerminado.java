package molina.mensajes.juegoTerminado;

import molina.buscaminas.Buscaminas;
import molina.resources.R;
import molina.sudoku.Sudoku;
import molina.tresRaya.TresEnRaya;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class JuegoTerminado extends Dialog implements
		android.view.View.OnClickListener {
	private String key;
	private Context context;

	public JuegoTerminado(Context context, String key) {
		super(context);
		this.key = key;
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.juego_terminado);
		setTitle(R.string.title_juego_terminado);
		TextView text = (TextView) findViewById(R.id.textoFinal);
		Button share = (Button) findViewById(R.id.share_boton);
		share.setOnClickListener(this);
		if (key == Sudoku.KEY)
			text.setText(R.string.juego_terminado);
		else if (key == TresEnRaya.KEY) {
			Button boton = (Button) findViewById(R.id.jugar_de_nuevo_boton);
			boton.setVisibility(View.VISIBLE);
			boton.setOnClickListener(this);
			TresEnRaya tresEnRaya = (TresEnRaya) context;
			switch (tresEnRaya.getEstadoJuego()) {
			case EMPATE:
				text.setText(R.string.resultado_empate);
				break;
			case JUGADOR1:
				text.setText(R.string.resultado_jugador1);
				break;
			case JUGADOR2:
				text.setText(R.string.resultado_jugador2);
				break;
			default:
				break;
			}
		} else if (key == Buscaminas.KEY) {
			Button boton = (Button) findViewById(R.id.jugar_de_nuevo_boton);
			boton.setVisibility(View.VISIBLE);
			boton.setOnClickListener(this);
			Buscaminas buscaminas = (Buscaminas) context;
			switch (buscaminas.getEstadoJuego()) {
			case JUEGO_COMPLETADO:
				text.setText(R.string.resultado_juego_completado);
				break;
			case MINA_PISADA:
				text.setText(R.string.resultado_mina_pisada);
				break;
			default:
				break;

			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jugar_de_nuevo_boton:
			if (key == TresEnRaya.KEY) {
				TresEnRaya act = (TresEnRaya) context;
				Intent i = new Intent(act, TresEnRaya.class);
				i.putExtra(TresEnRaya.GUARDAR, false);
				i.putExtra(TresEnRaya.NUM_JUGADORES, act.getNumJugadores());
				i.putExtra(TresEnRaya.DIFICULTAD, TresEnRaya.getDificultad());
				act.startActivity(i);
				act.finish();
			} else if (key == Buscaminas.KEY) {
				Buscaminas act = (Buscaminas) context;
				Intent i = new Intent(act, Buscaminas.class);
				i.putExtra(Buscaminas.CONTINUAR, false);
				i.putExtra(Buscaminas.DIFICULTAD, act.getDificultad().ordinal());
				act.startActivity(i);
				act.finish();
			}
			break;
		case R.id.share_boton:
			Activity act = (Activity) context;
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_SUBJECT, key);
			i.putExtra(Intent.EXTRA_TEXT,
					act.getResources().getString(R.string.share_mensaje));
			act.startActivity(Intent.createChooser(i, act.getResources()
					.getString(R.string.compartir)));
			break;
		}
	}
}
