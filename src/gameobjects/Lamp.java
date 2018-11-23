package gameobjects;

import com.jogamp.opengl.GL3;

import anilamp.Model;
import anilamp.Utils;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import scenegraph.LightNode;
import scenegraph.ModelNode;
import scenegraph.NameNode;
import scenegraph.TransformNode;

public class Lamp extends SceneObject{
	//Transforms to fuck with
	public TransformNode lampWorldTransform;
	public TransformNode lowerArmRotate;
	public TransformNode upperArmRotate;
	public TransformNode headRotate;
	
	//Lamp params
	private Model lampLegCube;
	private Model lampHeadCube;
	
	float legHeights;
	float headLength;
	
	//Lamp position
	//private Vec3 worldPos;
	
	//Rendered subobjects
	private NameNode lampRoot;
	private LightObject spotlight;
	
	public Lamp(float legHeights, float headLength, Model lampLegCube, Model lampHeadCube, LightObject spotlight) {
		this.legHeights = legHeights;
		this.headLength = headLength;
		
		this.lampLegCube = lampLegCube;
		this.lampHeadCube = lampHeadCube;
		
		this.spotlight = spotlight;
		
		init();
	}
	
	public void toggleLight () {
		
	}
	
	//Animation access
	//Some sort of scene graph specific object transformation function
	public void setHeadRotation(float yRotation, float zRotation) {
		float yRotate = Utils.clamp(yRotation, -50f, 20f);
		float zRotate = Utils.clamp(zRotation, -75f, 60f);
	}

	public void setLowerArmRotation(float xRotation, float zRotation) {
		float xRotate = Utils.clamp(xRotation, -50f, 20f);
		float zRotate = Utils.clamp(zRotation, -70f, 70f);
	}
	
	public void setUpperArmRotation(float xRotation, float zRotation) {
		float xRotate = Utils.clamp(xRotation, -50f, 20f);
		float zRotate = Utils.clamp(zRotation, -140f, 140);
	}
	
	//Build index function
	public void init() {
	    // Lamp Dimensions
	    float baseHeight = 0.5f;
	    float armLength = 6;
	    
	    //Lamp builder
	    lampRoot = new NameNode("root");
	    lampWorldTransform = new TransformNode("PlayerTransform", Mat4Transform.translate(5f, 0, 0));
	    TransformNode lampTranslate = new TransformNode("lamp transform", Mat4Transform.translate(0.5f, 0, 0));
	    
	    NameNode base = new NameNode("base");
		Mat4 a = Mat4Transform.translate(0, 0.5f, 0);	//Bottom origin point
		a = Mat4.multiply(Mat4Transform.scale(3, baseHeight, 2.25f), a);	//Scale
			TransformNode baseTransform = new TransformNode("base transform", a);
			ModelNode baseModel = new ModelNode("Cube(base)", lampLegCube);
		
		//Lower arm
		//Translate lower arm
		//Rotate upper arm
	    TransformNode translateLower = new TransformNode("Translate lower Arm",
	    		Mat4Transform.translate(0, baseHeight, 0));
	    lowerArmRotate = new TransformNode("Rotate lower arm",
	    		Mat4Transform.rotateAroundZ(-20));
	    
	    NameNode lowerArm = new NameNode("lower arm");
		a = new Mat4(1);
		a = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), a);		//Bottom origin point
		a = Mat4.multiply(Mat4Transform.scale(1, armLength, 1), a);		//Scale
		//a = Mat4.multiply(Mat4Transform.rotateAroundZ(20), a);		//Local rotation
		
		TransformNode lowerArmTransform = new TransformNode("lower arm transform", a);
			ModelNode lowerArmModel = new ModelNode("Cube(lower arm)", lampLegCube);
			
		//Translate translate upper arm
		TransformNode translateUpper = new TransformNode("Translate upper", 
				Mat4Transform.translate(0, armLength, 0));
		
		upperArmRotate = new TransformNode("Rotate upper",
				Mat4Transform.rotateAroundZ(-20));
		
		//Upper arm
	    NameNode upperArm = new NameNode("upper arm");
		a = new Mat4(1);
		a = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), a);		//Bottom origin point
		a = Mat4.multiply(Mat4Transform.scale(1, armLength, 1), a);		//Scale
		//a = Mat4.multiply(Mat4Transform.rotateAroundZ(20), a);		//Local Rotation
			TransformNode upperArmTransform = new TransformNode("upper arm transform", a);
			ModelNode upperArmModel = new ModelNode("Cube(upper arm)", lampLegCube);
		
		//Translate head
		TransformNode translateHead = new TransformNode("Translate head", 
				Mat4Transform.translate(0, armLength, 0));
		
		//Head
	    NameNode head = new NameNode("head");
		a = new Mat4(1);
		a = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), a);		//Bottom origin point
		a = Mat4.multiply(Mat4Transform.scale(4, 2, 2), a);				//Scale
			TransformNode headTransform = new TransformNode("head transform", a);
			ModelNode headModel = new ModelNode("Cube(head)", lampLegCube);
		
		//Spotlight
		LightNode lightNode = new LightNode("spotlight", spotlight);
		
	    //Build index
	    lampRoot.addChild(lampWorldTransform);
	    	lampWorldTransform.addChild(lampTranslate);
	    		lampTranslate.addChild(base);
	    		base.addChild(baseTransform);
	    			baseTransform.addChild(baseModel);
	    		base.addChild(translateLower);
	    			//lowerArmRotate.addChild(translateLower);
	    			//translateLower.addChild(lowerArm);
	    			translateLower.addChild(lowerArmRotate);
	    			lowerArmRotate.addChild(lowerArm);
		    			lowerArm.addChild(lowerArmTransform);
		    			lowerArmTransform.addChild(lowerArmModel);
		    			lowerArm.addChild(translateUpper);
		    			translateUpper.addChild(upperArmRotate);
		    			upperArmRotate.addChild(upperArm);
	    					upperArm.addChild(upperArmTransform);
	    					upperArmTransform.addChild(upperArmModel);
	    					upperArm.addChild(translateHead);
	    					translateHead.addChild(head);
	    						head.addChild(headTransform);
	    						headTransform.addChild(headModel);
	    					translateHead.addChild(lightNode);
	    //Lamp
	    				
	    lampRoot.update();
	    lampRoot.print(0, false);

	}
	
	@Override
	public Vec3 getPosition() {
		Vec3 positionk;
		return this.position;
	}

	@Override
	public void setPosition(float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}
}
