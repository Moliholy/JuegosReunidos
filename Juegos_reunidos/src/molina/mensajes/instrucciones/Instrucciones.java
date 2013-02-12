package molina.mensajes.instrucciones;

import molina.buscaminas.Buscaminas;
import molina.main.MainActivity;
import molina.resources.*;
import molina.sudoku.Sudoku;
import molina.tresRaya.TresEnRaya;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class Instrucciones extends Dialog {
	private String key;

	public Instrucciones(Context context, String key) {
		super(context);
		this.key = key;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instrucciones);
		if (key != MainActivity.ACERCA_DE)
			setTitle(R.string.instrucciones);
		else
			setTitle(R.string.acerca_de);

		TextView texto = (TextView) findViewById(R.id.texto_instrucciones);
		if (key == Sudoku.KEY) {
			texto.setText(R.string.instrucciones_sudoku_text);
		} else if (key == TresEnRaya.KEY) {
			texto.setText(R.string.instrucciones_tres_en_raya_text);
		} else if (key == Buscaminas.KEY) {
			texto.setText(R.string.instrucciones_buscaminas);
		} else if (key == MainActivity.ACERCA_DE) {
			texto.setText(R.string.acerca_de_text);
		}
	}
}
