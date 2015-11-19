package asf.medieval.view;

import asf.medieval.model.Token;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by daniel on 11/18/15.
 */
public interface SelectableView {

	/**
	 * @return -1 on no intersection,
	 * or when there is an intersection: the squared distance between the center of this
	 * object and the point on the ray closest to this object when there is intersection.
	 */
	public float intersects(Ray ray);

	public Token getToken();

	public void setSelected(boolean selected);

	public boolean isSelected();

	public Vector3 getTranslation();
}
