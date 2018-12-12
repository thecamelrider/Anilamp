package gameobjects;

import com.jogamp.opengl.GL3;

import anilamp.Light;
import anilamp.Model;
import anilamp.Utils;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import scenegraph.LightNode;
import scenegraph.ModelNode;
import scenegraph.NameNode;
import scenegraph.SGNode;
import scenegraph.TransformNode;

/* I declare that this code is my own work */ 
/* Author Husain Ahmed huss54@gmail.com */

public class Lamp extends SceneObject{
	//Controllable transforms
	public TransformNode lampWorldTransform;
	public TransformNode lowerArmRotate;
	public TransformNode upperArmRotate;
	public TransformNode headRotate;
	
	//Lamp params
	private Model lampLegCube;
	private Model lampHeadCube;
	
	private float legThickness;
	float legHeights;
	float headLength;
    float baseHeight = 0.5f;

	//Lamp position
	//private Vec3 worldPos;
	
	//Rendered subobjects
	private NameNode lampRoot;
	private Light spotlight;
	
	public Lamp(float legHeights, float legThickness, float headLength, Model lampLegCube, Model lampHeadCube, Light spotlight) {
		this.legHeights = legHeights;
		this.legThickness = legThickness;
		this.headLength = headLength;
		
		this.lampLegCube = lampLegCube;
		this.lampHeadCube = lampHeadCube;
		
		this.spotlight = spotlight;
		
		init();
	}
	
	public void toggleLight () {
		spotlight.active = !spotlight.active;
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
	    
	    //Lamp builder
	    lampRoot = new NameNode("root");
	    
	    //World lamp position, with origin at bottom
	    lampWorldTransform = new TransformNode("PlayerTransform", Mat4Transform.translate(0f, 6.7f, 0));
	    position = new Vec3(0, 6.7f, 0);
	    
	    //Base
	    NameNode base = new NameNode("base");
		Mat4 a = Mat4Transform.translate(0, 0.5f, 0);	//Bottom origin point
		a = Mat4.multiply(Mat4Transform.scale(3, baseHeight, 2.25f), a);	//Scale
			TransformNode baseTransform = new TransformNode("base transform", a);
			ModelNode baseModel = new ModelNode("Cube(base)", lampLegCube);
		
		//Transform coord to to lower arm root
	    TransformNode translateLower = new TransformNode("Translate lower Arm",
	    		Mat4Transform.translate(0, baseHeight, 0));
	    lowerArmRotate = new TransformNode("Rotate lower arm",
	    		Mat4Transform.rotateAroundZ(-20));
	    
	    //Lower arm transform
	    NameNode lowerArm = new NameNode("lower arm");
		a = Mat4Transform.translate(0, 0.5f, 0);			//Bottom origin point
		a = Mat4.multiply(Mat4Transform.scale(legThickness, legHeights, legThickness), a);		//Scale
		
		TransformNode lowerArmTransform = new TransformNode("lower arm transform", a);
			ModelNode lowerArmModel = new ModelNode("Cube(lower arm)", lampLegCube);
			
		//Transform coords to upper arm position/rotation
		TransformNode translateUpper = new TransformNode("Translate upper", 
				Mat4Transform.translate(0, legHeights, 0));		
		upperArmRotate = new TransformNode("Rotate upper",
				Mat4Transform.rotateAroundZ(-20));
		
		//Upper arm
	    NameNode upperArm = new NameNode("upper arm");
		a = new Mat4(1);
		a = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), a);		//Bottom origin point
		a = Mat4.multiply(Mat4Transform.scale(legThickness, legHeights, legThickness), a);		//Scale
			TransformNode upperArmTransform = new TransformNode("upper arm transform", a);
			ModelNode upperArmModel = new ModelNode("Cube(upper arm)", lampLegCube);
		
		//Translate head
		TransformNode translateHead = new TransformNode("Translate head", 
				Mat4Transform.translate(0, legHeights, 0));
		
		//Head
	    NameNode head = new NameNode("head");
		a = new Mat4(1);
		a = Mat4.multiply(Mat4Transform.translate(0.2f, 0.5f, 0), a);		//Move model to around bottom left origin point
		a = Mat4.multiply(Mat4Transform.scale(headLength, headLength * 0.7f, headLength * 0.7f), a);				//Scale
			TransformNode headTransform = new TransformNode("head transform", a);
			ModelNode headModel = new ModelNode("Cube(head)", lampLegCube);
		
		//Move origin to front of head
		a = Mat4Transform.translate(headLength, 0, 0);
		TransformNode lightTranslate = new TransformNode("light transform", a);
		
		//Spotlight
		NameNode spotlightNode = new NameNode("Spotlight");
		LightNode lightNode = new LightNode("LightNode", spotlight);
				
	    //Build index
	    lampRoot.addChild(lampWorldTransform);
	    	lampWorldTransform.addChild(base);
	    		base.addChild(baseTransform);
	    			baseTransform.addChild(baseModel);
	    		base.addChild(translateLower);
    			translateLower.addChild(lowerArmRotate);
	    			//lowerArmRotate.addChild(translateLower);
	    			//translateLower.addChild(lowerArm);
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
	    					translateHead.addChild(lightTranslate);
	    						lightTranslate.addChild(spotlightNode);
	    							spotlightNode.addChild(lightNode);
	    //Lamp
	    lampRoot.update();
	    lampRoot.print(0, false);
	}
	
	@Override
	public Vec3 getPosition() {
		return this.position;
	}

	@Override
	public void setPosition(float x, float y, float z) {
		
	}

	@Override
	public SGNode getRootNode() {
		// TODO Auto-generated method stub
		return lampRoot;
	}

	public void setPosition(Vec3 targetPos) {
		
		lampWorldTransform.setTransform(Mat4Transform.translate(targetPos));
		
	}
}
