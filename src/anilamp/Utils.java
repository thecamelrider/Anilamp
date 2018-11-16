package anilamp;

public class Utils {

	//Clamps between two numbers
	public static float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
	
}
