package gameobjects;

import com.jogamp.opengl.GL3;

import anilamp.Material;
import anilamp.Model;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;
import scenegraph.ModelNode;
import scenegraph.NameNode;
import scenegraph.SGNode;
import scenegraph.TransformNode;

public class Room extends SceneObject {
	float width;
	float length;
	float wallHeight;
	
	Model floorModel;
	Model wallModel;

	SGNode root;

	public Room(float width, float length, float wallHeight, Model floorModel, Model wallModel) {
		this.width = width;
		this.length = length;
		this.wallHeight = wallHeight;
		this.floorModel = floorModel;
		this.wallModel = wallModel;
		
		buildIndex();
	}
	
	void buildIndex() {
		//Build wallModel mesh
		root = new NameNode("Room");
		
		//Floor
		NameNode floor = new NameNode("Floor");
		ModelNode floorShape = new ModelNode("floorModel", floorModel);

		//Wall
		NameNode wall = new NameNode("Wall");
		//Start wall at end of floor
		Mat4 m = Mat4Transform.translate(new Vec3(0, -1, 0));
		TransformNode wallTranslate = new TransformNode("Wall Translate", m);
		
		//Create walls
		m = Mat4.multiply(Mat4Transform.scale(7f, 0, 2.4f), Mat4Transform.translate(0.5f, 0, 0));		//Bottom origin point
		TransformNode wallTransform1 = new TransformNode("transformNode", m);
		m = Mat4.multiply(Mat4Transform.scale(20f, 0, 0.8f), Mat4Transform.translate(0.5f, 0.3f, -1f));		//Bottom left origin point
		TransformNode wallTransform2 = new TransformNode("transformNode2", m);
		m = Mat4.multiply(Mat4Transform.scale(20f, 0, 0.8f), Mat4Transform.translate(0.5f, 0.2f, 1f));		//Bottom right origin point
		TransformNode wallTransform3 = new TransformNode("transformNode3", m);
		m = Mat4.multiply(Mat4Transform.scale(4f, 0, 2.4f), Mat4Transform.translate(0.5f, 0.1f, 0));		//Bottom origin point scale
		m = Mat4.multiply(Mat4Transform.translate(20, 0, 0), m);
		TransformNode wallTransform4 = new TransformNode("transformNode4", m);	
		
		ModelNode wallShape1 = new ModelNode("wallModel", wallModel);
		ModelNode wallShape2 = new ModelNode("wallModel2", wallModel);
		ModelNode wallShape3 = new ModelNode("wallModel2", wallModel);
		ModelNode wallShape4 = new ModelNode("wallModel2", wallModel);
		
		root.addChild(wall);
			wall.addChild(wallTranslate);
				wallTranslate.addChild(wallTransform1);
					wallTransform1.addChild(wallShape1);
				wallTranslate.addChild(wallTransform2);
					wallTransform2.addChild(wallShape2);
				wallTranslate.addChild(wallTransform3);
					wallTransform3.addChild(wallShape3);
				wallTranslate.addChild(wallTransform4);
					wallTransform4.addChild(wallShape4);

		root.addChild(floor);
			floor.addChild(floorShape);
			
		root.update();
	}

	@Override
	public Vec3 getPosition() {
		return new Vec3(0, 0, 0);
	}

	@Override
	public void setPosition(float x, float y, float z) {
		
	}

	@Override
	public SGNode getRootNode() {		
		return root;
	}
}
