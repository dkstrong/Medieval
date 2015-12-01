package asf.medieval.painter;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daniel on 11/30/15.
 */
public interface PainterModel extends Disposable{

	public void setPainter(PixmapPainter painter);
	public void output();

	public int getWidth();
	public int getHeight();

}
