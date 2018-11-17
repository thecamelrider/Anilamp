package gameobjects;

import gmaths.Vec3;

public abstract class SceneObject {
	public Vec3 position;
	
	public abstract Vec3 getPosition();
	public abstract void setPosition(float x, float y, float z);
	
}
