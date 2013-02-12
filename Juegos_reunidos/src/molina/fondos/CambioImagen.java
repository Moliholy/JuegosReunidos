package molina.fondos;

import java.util.EventListener;

import android.graphics.Bitmap;
import android.os.Handler;

public interface CambioImagen extends EventListener {
	public void setBitmap(Bitmap bm);
	public Handler getHandler();
}
