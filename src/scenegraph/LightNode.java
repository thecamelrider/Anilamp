package scenegraph;

import com.jogamp.opengl.GL3;

import gameobjects.LightObject;

public class LightNode extends SGNode {

	protected LightObject light;
	public LightNode(String name, LightObject light) {
		super(name);
		this.light = light;
	}
	
	@Override
	public void update() {
		//light.setPosition();
		super.update();
	}
}
