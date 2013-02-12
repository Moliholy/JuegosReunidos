package molina.main;

import molina.mensajes.instrucciones.Instrucciones;
import molina.resources.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
	public static final String ACERCA_DE = "AcercaDe";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		View sudoku = findViewById(R.id.sudoku_boton);
		sudoku.setOnClickListener(this);
		View tres_en_raya = findViewById(R.id.tres_en_raya_boton);
		tres_en_raya.setOnClickListener(this);
		View buscaminas = findViewById(R.id.buscaminas_boton);
		buscaminas.setOnClickListener(this);
		View acerca_de = findViewById(R.id.acerca_de_boton);
		acerca_de.setOnClickListener(this);
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.sudoku_boton:
			startActivity(new Intent(this, SudokuMainActivity.class));
			break;
		case R.id.tres_en_raya_boton:
			startActivity(new Intent(this, TresEnRayaMainActivity.class));
			break;
		case R.id.buscaminas_boton:
			startActivity(new Intent(this, BuscaminasMainActivity.class));
			break;
		case R.id.acerca_de_boton:
			new Instrucciones(this, ACERCA_DE).show();
			break;
		}
	}
}
