package molina.sudoku;

import molina.resources.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SudokuView extends View {
	private Sudoku sudoku;
	private float anchuraCasilla;
	private float alturaCasilla;
	private static final int ID = 42;
	private int X;
	private int Y;
	private final Rect rectangulo = new Rect();
	private final Paint fondoSudoku = new Paint();
	private final Paint negroSuave = new Paint();
	private final Paint blanco = new Paint();
	private final Paint paintNumeros = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint seleccion = new Paint();

	private void init(Context context) {
		sudoku = (Sudoku) context;

		// lineas a dibujar
		fondoSudoku.setColor(getResources().getColor(R.color.fondo_sudoku));
		fondoSudoku.setStyle(Style.FILL);
		negroSuave.setColor(getResources().getColor(R.color.negroSuave));
		blanco.setColor(getResources().getColor(R.color.blanco));
		seleccion.setColor(getResources().getColor(R.color.seleccion));

		// numeros a pintar
		paintNumeros.setColor(getResources().getColor(
				R.color.color_texto_sudoku));
		paintNumeros.setTextAlign(Paint.Align.CENTER);
		paintNumeros.setStyle(Style.FILL);

		// para que se puerda tocar la pantalla t�ctil
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	public SudokuView(Context context) {
		super(context);
		init(context);
	}

	public SudokuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SudokuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		anchuraCasilla = w / (float) Sudoku.TAM;
		alturaCasilla = h / (float) Sudoku.TAM;
		cambiarRectangulo();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// a ver, empezamos con esto...
		// 1�) Dibujar un rect�ngulo que ocupe toda la pantalla utillizando el
		// lienzo (Canvas) que nos viene como par�metro
		canvas.drawRect(0, 0, getWidth(), getHeight(), fondoSudoku);
		// 2�) Dibujamos las rectas divisorias
		//primero las grises
		for (int i = 0; i <= Sudoku.TAM; i++) {
			if (i % 3 != 0) {
				// horizontales
				canvas.drawLine(0, Math.max(i * alturaCasilla - 1, 0),
						getWidth(), Math.max(i * alturaCasilla - 1, 0),
						negroSuave);
				// verticales
				canvas.drawLine(Math.max(i * anchuraCasilla - 1, 0), 0,
						Math.max(i * anchuraCasilla - 1, 0), getHeight(),
						negroSuave);
			}
		}
		//y luego las blancas
		for (int i = 0; i <= Sudoku.TAM; i++) {
			if (i % 3 == 0) {
			canvas.drawLine(Math.max(i * anchuraCasilla - 1, 0), 0,
					Math.max(i * anchuraCasilla - 1, 0), getHeight(), blanco);
			canvas.drawLine(0, Math.max(i * alturaCasilla - 1, 0), getWidth(),
					Math.max(i * alturaCasilla - 1, 0), blanco);
			}
		}

		// Ya est� dibujado, ahora a dibujar los n�meros
		int[][] numeros = sudoku.getNumeros();
		paintNumeros.setTextSize(alturaCasilla * 0.75f);
		paintNumeros.setTextScaleX(anchuraCasilla / alturaCasilla);
		FontMetrics fm = paintNumeros.getFontMetrics();
		float posX = anchuraCasilla / 2;
		float posY = (alturaCasilla - (fm.ascent + fm.descent)) / 2;
		for (int i = 0; i < Sudoku.TAM; i++)
			for (int j = 0; j < Sudoku.TAM; j++)
				if (numeros[i][j] != 0)
					// Dibujamos si el n�mero es distinto de 0
					canvas.drawText(Integer.toString(numeros[i][j]), i
							* anchuraCasilla + posX, j * alturaCasilla + posY,
							paintNumeros);
		// dibujamos el rect�ngulo selecci�n
		canvas.drawRect(rectangulo, seleccion);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			// ESTO ES MUY IMPORTANTE, SINO ENTRA DOS VECES EN EL M�TODO Y LA
			// LIA
			return super.onTouchEvent(event);
		invalidate(rectangulo);
		int x = (int) event.getX();
		int y = (int) event.getY();
		X = (int) (x / anchuraCasilla);
		Y = (int) (y / alturaCasilla);
		cambiarRectangulo();
		invalidate(rectangulo);
		// mostramos el Dialog s�lo si la casilla no tiene un n�mero asignado
		if (!sudoku.casillaRellena(X, Y)) {
			IntroduccionNumero introduccionNumero = new IntroduccionNumero(
					getContext(), sudoku.getNumerosDisponibles(X, Y), sudoku,
					X, Y);
			introduccionNumero.show();
		}
		return true;
	}

	public int getPosicionCursor() {
		return X * Sudoku.TAM + Y;
	}

	public void cambiarRectangulo(int x, int y) {
		X = x;
		Y = y;
		cambiarRectangulo();
	}

	private void cambiarRectangulo() {
		rectangulo.set((int) (X * anchuraCasilla), (int) (Y * alturaCasilla),
				(int) (X * anchuraCasilla + anchuraCasilla), (int) (Y
						* alturaCasilla + alturaCasilla));
	}
}
