package anilamp;

import gmaths.Vec3;

public class Light {
	//Used by some of the types
	public Vec3 position;
	public Vec3 direction;
	
	//Light color
    public Vec3 ambient;
    public Vec3 diffuse;
    public Vec3 specular;

	//Attenuation
	public float constant = 0.00000000000001f;
    public float linear = 0.0000000000000001f;
    public float quadratic = 0.00000000000001f;
	 
    //Spotlight params
    public float cutoff = (float) Math.cos(Math.toRadians(60.0));
    public float outercutoff = (float) Math.cos(Math.toRadians(60.0));
	
    //Light type not saved in instance since it isnt needed in rendering since
	//Each type in its own array in renderer
	public enum Types{
		DIRECTION,
		SPOTLIGHT,
		POINTLIGHT;
	}
}
