package server.protocol;

import java.util.Random;

public class Sleep {
	int time;

	public Sleep(int time) {
		this.time = time;
	}

	public void go() {
		Random rand = new Random();

		int n = rand.nextInt(time + 1);
		try {
			Thread.sleep(n);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
