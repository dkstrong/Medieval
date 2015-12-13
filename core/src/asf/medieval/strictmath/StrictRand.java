package asf.medieval.strictmath;

import java.util.Random;

/**
 * Created by daniel on 12/12/15.
 */
public strictfp class StrictRand {
	private final Random random;

	public StrictRand(long seed) {
		this.random = new Random(seed);
	}

	public <T extends Object> T choose(T... choices){
		return choices[random.nextInt(choices.length)];
	}

	public boolean bool(StrictPoint chance){
		return random.nextFloat() < chance.val;
	}

	/** Returns a rand number between start (inclusive) and end (inclusive). */
	public int range(int start, int end) {
		return start + random.nextInt(end - start + 1);
	}

	/** Returns a rand number between start (inclusive) and end (exclusive). */
	public StrictPoint range(StrictPoint start, StrictPoint end, StrictPoint store){
		store.val =start.val + random.nextFloat() * (end.val - start.val);
		return store;
	}

	public int sign(){ return 1 | (random.nextInt() >> 31); }
}
