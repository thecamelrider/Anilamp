package scenegraph;

import com.jogamp.opengl.GL3;

import gameobjects.Light;

public class LightNode extends SGNode {

	Light light;
	public LightNode(String name, Light light) {
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
