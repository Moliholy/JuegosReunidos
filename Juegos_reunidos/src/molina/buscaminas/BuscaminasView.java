package molina.buscaminas;

import molina.fondos.CambioFondo;
import molina.fondos.CambioImagen;
import molina.resources.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("HandlerLeak")
public class BuscaminasView extends View implements CambioImagen {
	private Buscaminas buscaminas;
	private int tamCasilla;
	private float ladoTablero;
	private Rect[] rectangulos;
	private static final int ID = 43;
	private int X;
	private int Y;
	private final Paint paintImagenes = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint fondoBuscaminas = new Paint();
	private final Paint transparente = new Paint();
	private final Paint blanco = new Paint();
	private final Paint paintNumeros = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Bitmap boom;
	private Bitmap imagenes;
	private Rect fondo;
	private CambioFondo cambioFondo;

	private final Handler receptor = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			invalidate();
		}
	};

	public BuscaminasView(Context context) {
		super(context);
		init(context);
	}

	public BuscaminasView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public BuscaminasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		boom = BitmapFactory.decodeResource(getResources(),
				R.drawable.explosion);
		imagenes = BitmapFactory.decodeResource(getResources(),
				R.drawable.fondo2);
		buscaminas = (Buscaminas) context;

		cambioFondo = new CambioFondo(this);
		cambioFondo.start();

		rectangulos = new Rect[Buscaminas.TAM * Buscaminas.TAM];

		// retocamos los Paint
		fondoBuscaminas.setColor(getResources().getColor(
				R.color.fondo_buscaminas));
		fondoBuscaminas.setStyle(Style.FILL);
		transparente.setColor(getResources().getColor(R.color.transparente));
		blanco.setColor(getResources().getColor(R.color.blanco));

		// numeros a pintar
		paintNumeros.setColor(getResources().getColor(
				R.color.color_texto_buscaminas));
		paintNumeros.setTextAlign(Paint.Align.CENTER);
		paintNumeros.setStyle(Style.FILL);

		// para que se puerda tocar la pantalla táctil
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(imagenes, null, fondo, paintImagenes);// FONDO JPG
		int[][] numeros = buscaminas.getMatriz();
		paintNumeros.setTextSize(tamCasilla * 0.75f);
		paintNumeros.setTextScaleX(tamCasilla / tamCasilla);
		paintNumeros.setShadowLayer(12, 12, 12, Color.BLACK);
		FontMetrics fm = paintNumeros.getFontMetrics();
		float posX = tamCasilla / 2;
		float posY = (tamCasilla - (fm.ascent + fm.descent)) / 2;
		for (int i = 0; i < Buscaminas.TAM; i++)
			for (int j = 0; j < Buscaminas.TAM; j++)
				switch (numeros[i][j]) {
				case -2:
					canvas.drawRect(rectangulos[i * Buscaminas.TAM + j],
							fondoBuscaminas);
					break;
				case -1:
					// cargar la imagen de la explosion (Bitmap class)
					canvas.drawBitmap(boom, null, rectangulos[i
							* Buscaminas.TAM + j], paintImagenes);
					break;
				case 0:
					canvas.drawRect(rectangulos[i * Buscaminas.TAM + j],
							transparente);
					break;
				default:
					canvas.drawRect(rectangulos[i * Buscaminas.TAM + j],
							transparente);
					canvas.drawText(Integer.toString(numeros[i][j]), i
							* tamCasilla + posX, j * tamCasilla + posY,
							paintNumeros);
					break;
				}
		for (int i = 0; i <= Buscaminas.TAM; i++) {
			// horizontales
			canvas.drawLine(0, i * tamCasilla, ladoTablero, i * tamCasilla,
					blanco);
			// verticales
			canvas.drawLine(i * tamCasilla, 0, i * tamCasilla, Buscaminas.TAM
					* tamCasilla, blanco);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		tamCasilla = (w / Buscaminas.TAM) + 1;
		ladoTablero = w;
		fondo = new Rect(0, 0, w, w);
		for (int i = 0; i < rectangulos.length; i++) {
			int fila = i / Buscaminas.TAM;
			int columna = i % Buscaminas.TAM;
			rectangulos[i] = new Rect(fila * tamCasilla, columna * tamCasilla,
					fila * tamCasilla + tamCasilla, columna * tamCasilla
							+ tamCasilla);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		// tengo que ponerle un poco más porque sino no se ve la linea blanca de
		// abajo
		setMeasuredDimension(parentWidth, (int) (parentWidth * 1.02));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN
				|| event.getY() > ladoTablero
				|| buscaminas.getEstadoJuego() != EstadoBuscaminas.SIN_CAMBIOS) {
			return super.onTouchEvent(event);
		}
		int x = (int) event.getX();
		int y = (int) event.getY();
		X = (x / tamCasilla);
		Y = (y / tamCasilla);
		// y ahora falta ejecutar la jugada
		if (buscaminas.getMatriz()[X][Y] == -2) {
			ejecutarJugada();
			invalidate();
			buscaminas.comprobarEstado();
			if (buscaminas.getEstadoJuego() != EstadoBuscaminas.SIN_CAMBIOS)
				buscaminas.juegoTerminado();
		}
		return true;
	}

	private void ejecutarJugada() {
		buscaminas.ejecutarJugada(X, Y);
	}

	public int getPosicionCursor() {
		return X * Buscaminas.TAM + Y;
	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public Handler getHandler() {
		return receptor;
	}

	public void setBitmap(Bitmap bitmap) {
		imagenes = bitmap;
	}

	public void stopCambioFondos() {
		cambioFondo.setFinalizar(true);
	}
}
