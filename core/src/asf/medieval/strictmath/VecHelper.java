package asf.medieval.strictmath;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * Converts between different types of vectors used in medieval
 *
 *
 *
 * Created by daniel on 12/13/15.
 */
public strictfp class VecHelper {

	public static StrictVec2 toVec2(StrictVec3 vec3, StrictVec2 store){
		store.x.set(vec3.x);
		store.y.set(vec3.z);
		return store;
	}

	public static StrictVec2 toVec2(Vector2 vector2, StrictVec2 store){
		store.x.set(String.valueOf(vector2.x));
		store.y.set(String.valueOf(vector2.y));
		return store;
	}

	public static StrictVec2 toVec2(Vector3 vector3, StrictVec2 store){
		store.x.set(String.valueOf(vector3.x));
		store.y.set(String.valueOf(vector3.z));
		return store;
	}

	public static StrictVec3 toVec3(StrictVec2 vec2, StrictVec3 store){
		store.x.set(vec2.x);
		store.y.set(StrictPoint.ZERO);
		store.z.set(vec2.y);
		return store;
	}

	public static StrictVec3 toVec3(Vector2 vector2, StrictVec3 store){
		store.x.set(String.valueOf(vector2.x));
		store.y.set(StrictPoint.ZERO);
		store.z.set(String.valueOf(vector2.y));
		return store;
	}

	public static StrictVec3 toVec3(Vector3 vector3, StrictVec3 store){
		store.x.set(String.valueOf(vector3.x));
		store.y.set(String.valueOf(vector3.y));
		store.z.set(String.valueOf(vector3.z));
		return store;
	}

	public static Vector2 toVector2(StrictVec2 vec2, Vector2 store){
		store.x = vec2.x.toFloat();
		store.y = vec2.y.toFloat();
		return store;
	}

	public static Vector2 toVector2(StrictVec3 vec3, Vector2 store){
		store.x = vec3.x.toFloat();
		store.y = vec3.z.toFloat();
		return store;
	}

	public static Vector2 toVector2(Vector3 vector3, Vector2 store){
		store.x = vector3.x;
		store.y = vector3.z;
		return store;
	}

	public static Vector3 toVector3(StrictVec2 vec2, Vector3 store){
		store.x = vec2.x.toFloat();
		store.y = 0f;
		store.z = vec2.y.toFloat();
		return store;
	}

	public static Vector3 toVector3(StrictVec2 vec2, StrictPoint y, Vector3 store){
		store.x = vec2.x.toFloat();
		store.y = y.toFloat();
		store.z = vec2.y.toFloat();
		return store;
	}

	public static Vector3 toVector3(StrictVec3 vec3, Vector3 store){
		store.x = vec3.x.toFloat();
		store.y = vec3.y.toFloat();
		store.z = vec3.z.toFloat();
		return store;
	}

	public static Vector3 toVector3(Vector2 vector2, Vector3 store){
		store.x = vector2.x;
		store.y = 0f;
		store.z = vector2.y;
		return store;
	}

	private VecHelper(){

	}
}
