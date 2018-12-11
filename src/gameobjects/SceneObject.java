package gameobjects;

import gmaths.Vec3;
import scenegraph.SGNode;

public abstract class SceneObject {
	public Vec3 position;
	
	public abstract Vec3 getPosition();
	public abstract void setPosition(float x, float y, float z);
	public abstract SGNode getRootNode();
}
