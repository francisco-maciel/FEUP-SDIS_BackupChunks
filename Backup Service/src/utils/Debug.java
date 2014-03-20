package utils;

public class Debug {
	public static final boolean on = true;

	public static void debug(String output) {
		if (on)
			System.out.println(output);
	}
}
