package asf.medieval.utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Danny on 11/13/2015.
 */
public class UtDebugPrint {


	public interface Debuggable{
		public List<String> toDebugInfo();
	}

	public static void print(Object val)
	{
		if(val instanceof List)
		{
			List out = (List) val;
			for (Object o : out) {
				System.out.println(String.valueOf(0));
			}
		}
		else if(val instanceof Object[])
		{
			Object[] objects = (Object[]) val;
			for (Object o : objects) {
				System.out.println(String.valueOf(o));
			}
		}
		else if(val instanceof Color){
			Color c = (Color) val;
			System.out.println("Color("+UtMath.round(c.r, 2)+", "+UtMath.round(c.g, 2)+", "+UtMath.round(c.b, 2)+", "+UtMath.round(c.a, 2)+")");
		}
		else if(val instanceof Vector3)
		{
			System.out.println(UtMath.round((Vector3) val, 2));
		}
		else if(val instanceof Float)
		{
			Float f = (Float) val;
			if(!f.isNaN()){
				System.out.println(UtMath.round(f, 2));
			}else{
				System.out.println(f);
			}

		}
		else
		{
			print(getDebugInfo(val));
		}
	}


	private static void format(List<String> out, String s, Object... vals) {
		for (int i = 0; i < vals.length; i++) {
			if (vals[i] instanceof Vector3) {
				vals[i] = UtMath.round((Vector3) vals[i], 2);
			}else if(vals[i] instanceof Float){
				Float f = (Float) vals[i];
				if(!Float.isNaN(f))
					vals[i] = UtMath.round(f, 2);
			}
		}

		out.add(String.format(s, vals));

	}

	private static List<String> out() {
		return new LinkedList<String>();
	}

	private static List<String> getDebugInfo(Object o) {
		if(o instanceof Debuggable){
			Debuggable dbg = (Debuggable) o;
			return dbg.toDebugInfo();
		}
		return object(o);
	}

	private static List<String> object(Object o){
		List<String> out = out();

		format(out, o.getClass().getSimpleName());

		try {
			Field[] fields = o.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				format(out, "%-25s %25s %s",field.getType().getSimpleName(),field.getName()+":", field.get(o));
			}
		}catch (IllegalAccessException e) {
			format(out, e.getMessage());
		}

		return out;
	}
}
