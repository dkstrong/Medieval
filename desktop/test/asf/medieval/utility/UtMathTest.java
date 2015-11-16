package asf.medieval.utility;

import asf.medieval.net.GameClient;
import com.badlogic.gdx.math.Vector3;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by daniel on 11/15/15.
 */
public class UtMathTest {

	@Test
	public void testIsEven() throws Exception {
		assertTrue("isEven", UtMath.isEven(2));
		assertTrue("isOdd", !UtMath.isEven(3));
	}

	@Test
	public void testScaleAdd() throws Exception {

	}

	@Test
	public void testTruncate() throws Exception {

	}

	@Test
	public void testTruncateAlt() throws Exception {

	}

	@Test
	public void testIsPointInQuadrilateral() throws Exception {

	}
}