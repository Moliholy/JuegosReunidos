package molina.widget.cronometro;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

public class Cronometro extends Chronometer {
	private long milisegundos;

	public Cronometro(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Cronometro(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Cronometro(Context context) {
		super(context);
	}

	private void actualizarMilisegundos() {
		milisegundos = SystemClock.elapsedRealtime() - getBase();
	}

	private void establecerBase() {
		super.setBase(SystemClock.elapsedRealtime() - milisegundos);
	}

	@Override
	public void setBase(long diferenciaEnMilisegundos) {
		milisegundos = diferenciaEnMilisegundos;
		super.setBase(SystemClock.elapsedRealtime() - milisegundos);
	}

	@Override
	public void start() {
		establecerBase();
		super.start();
	}

	@Override
	public void stop() {
		actualizarMilisegundos();
		super.stop();
	}

}
