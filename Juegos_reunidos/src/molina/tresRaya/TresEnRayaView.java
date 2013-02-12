package molina.tresRaya;

import molina.resources.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class TresEnRayaView extends View {
	private TresEnRaya tresEnRaya;
	private int anchuraCasilla;
	private int alturaCasilla;
	private int X;
	private int Y;
	private final Rect seleccion = new Rect();
	private final Paint negro = new Paint();
	private final Paint blanco = new Paint();
	private final Paint seleccionPaint = new Paint();
	private final Paint paintX = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint paintO = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static final int ID = 42;

	public TresEnRayaView(Context context) {
		super(context);
		tresEnRaya = (TresEnRaya) context;
		negro.setColor(getResources().getColor(R.color.negro));
		blanco.setColor(getResources().getColor(R.color.blanco));
		seleccionPaint.setColor(getResources().getColor(R.color.seleccion));

		paintX.setColor(Color.BLUE);
		paintX.setTextAlign(Paint.Align.CENTER);
		paintX.setStyle(Style.FILL);
		paintO.setColor(Color.RED);
		paintO.setTextAlign(Paint.Align.CENTER);
		paintO.setStyle(Style.FILL);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		anchuraCasilla = w / TresEnRaya.TAM;
		alturaCasilla = h / TresEnRaya.TAM;
		cambiarSeleccion();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawRect(0, 0, getWidth(), getHeight(), negro);
		for (int i = 0; i <= TresEnRaya.TAM; i++) {
			canvas.drawLine(i * anchuraCasilla, 0, i * anchuraCasilla,
					getHeight(), blanco);
			canvas.drawLine(0, i * alturaCasilla, getWidth(),
					i * alturaCasilla, blanco);
		}
		char[][] tablero = tresEnRaya.getMatriz();
		paintX.setTextSize(alturaCasilla * 0.75f);
		paintX.setTextScaleX(anchuraCasilla / alturaCasilla);
		paintO.setTextSize(alturaCasilla * 0.75f);
		paintO.setTextScaleX(anchuraCasilla / alturaCasilla);
		FontMetrics fm = paintX.getFontMetrics();
		float posX = anchuraCasilla / 2;
		float posY = (alturaCasilla - (fm.ascent + fm.descent)) / 2;
		for (int i = 0; i < tablero.length; i++)
			for (int j = 0; j < tablero[i].length; j++)
				if (tablero[i][j] == TresEnRaya.JUGADOR2)
					canvas.drawText(Character.toString(tablero[i][j]), i
							* anchuraCasilla + posX, j * alturaCasilla + posY,
							paintX);
				else if (tablero[i][j] == TresEnRaya.JUGADOR1)
					canvas.drawText(Character.toString(tablero[i][j]), i
							* anchuraCasilla + posX, j * alturaCasilla + posY,
							paintO);
		canvas.drawRect(seleccion, seleccionPaint);
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		invalidate(seleccion);
		int x = (int) event.getX();
		int y = (int) event.getY();
		X = (int) (x / anchuraCasilla);
		Y = (int) (y / alturaCasilla);
		cambiarSeleccion();
		invalidate(seleccion);
		char[][] tablero = tresEnRaya.getMatriz();
		if (tablero[X][Y] == TresEnRaya.VACIO
				&& tresEnRaya.getEstadoJuego() == EstadoTresEnRaya.SIN_CAMBIOS) {
			tresEnRaya.introducirFicha(X, Y);
			EstadoTresEnRaya estado = tresEnRaya.comprobarEstado();
			if (tresEnRaya.getNumJugadores() == 1)
				if (estado == EstadoTresEnRaya.SIN_CAMBIOS) {
					tresEnRaya.pensarJugada();
					estado = tresEnRaya.comprobarEstado();
					if (estado != EstadoTresEnRaya.SIN_CAMBIOS)
						tresEnRaya.juegoTerminado();
				} else
					tresEnRaya.juegoTerminado();
			else if (estado != EstadoTresEnRaya.SIN_CAMBIOS)
				tresEnRaya.juegoTerminado();
			invalidate();
		}
		return true;
	}

	private void cambiarSeleccion() {
		seleccion.set((int) (X * anchuraCasilla), (int) (Y * alturaCasilla),
				(int) (X * anchuraCasilla + anchuraCasilla), (int) (Y
						* alturaCasilla + alturaCasilla));
	}
}
