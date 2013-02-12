package molina.fondos;

import java.util.Random;

import molina.resources.R;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class CambioFondo extends Thread {
	private boolean finalizar;
	private TypedArray jpgs;
	private Bitmap fondo;
	private View vista;
	private Handler receptor;
	public static final int TICK = 2;

	private static final long TIEMPO_ESPERA = 10000L;

	public CambioFondo(View a) {
		vista = a;
		finalizar = false;
		jpgs = vista.getResources().obtainTypedArray(R.array.fondos);
		receptor = ((CambioImagen) vista).getHandler();
	}

	private synchronized void cambiarFondo() {
		try {
			Random random = new Random();
			int num = random.nextInt(jpgs.length());
			int pos = jpgs.getResourceId(num, R.drawable.fondo1);
			fondo = BitmapFactory.decodeResource(vista.getResources(), pos);
			((CambioImagen) vista).setBitmap(fondo);
			// ahora se lo tenemos que enviar por paso de mensajes
			Message.obtain(receptor).sendToTarget();
			// boolean enviado = emisor.sendMessage(receptor); //INVALIDO! SOLO
			// SE RECIBE UNA VEZ Y NO SE POR QUÉ
			wait(TIEMPO_ESPERA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void setFinalizar(boolean valor) {
		finalizar = valor;
	}

	public void run() {
		while (!finalizar)
			cambiarFondo();
	}

}
