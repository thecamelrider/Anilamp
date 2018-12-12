package scenegraph;

import com.jogamp.opengl.GL3;

import anilamp.Light;
import gameobjects.LightObject;
import gmaths.Mat4;
import gmaths.Vec3;

public class LightNode extends SGNode {

	protected Light light;
	public LightNode(String name, Light spotlight) {
		super(name);
		this.light = spotlight;
	}
	
	@Override
	protected void update(Mat4 t) {
		// TODO Auto-generated method stub
		super.update(t);
		
		//Get parent position
		float[] parentPos = this.parent.parent.worldTransform.toFloatArrayForGLSL();
		float[] thisPos = this.worldTransform.toFloatArrayForGLSL();
		
		//Update light position and direction
		Vec3 pos = new Vec3(thisPos[12], thisPos[13], thisPos[14]);
		Vec3 pPos = new Vec3(parentPos[12], parentPos[13], parentPos[14]);

		light.position = pos;
		light.direction = Vec3.subtract(pos, pPos);
				
	}
}
