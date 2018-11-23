package scenegraph;

import com.jogamp.opengl.GL3;

import gameobjects.LightObject;

public class LightNode extends SGNode {

	LightObject light;
	public LightNode(String name, LightObject light) {
		super(name);
		this.light = light;
	}
	
	public void draw(GL3 gl) {
		light.render(gl);
		for (int i=0; i<children.size(); i++) {
		    children.get(i).draw(gl);
		}
	}
}
