package asf.medieval.model;

import java.util.Random;

/**
 * Created by Daniel Strong on 11/12/2015.
 */
public class ScenarioRand {
	public final Random random;

	public ScenarioRand(Random random) {
		if(random == null){
			this.random = new Random();
		}else{
			this.random = random;
		}
	}

	public <T extends Object> T choose(T... choices){
		return choices[random.nextInt(choices.length)];
	}

	public boolean bool(float chance){
		return random.nextFloat() < chance;
	}

	/** Returns a rand number between start (inclusive) and end (inclusive). */
	public int range(int start, int end) {
		return start + random.nextInt(end - start + 1);
	}

	/** Returns a rand number between start (inclusive) and end (exclusive). */
	public float range(float start, float end){
		return start + random.nextFloat() * (end - start);
	}

	public int sign(){ return 1 | (random.nextInt() >> 31); }
}
