package gameobjects;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;

import anilamp.*;
import gmaths.*;
import scenegraph.ModelNode;
import scenegraph.NameNode;
import scenegraph.TransformNode;

public class Table extends SceneObject {
	public float width;
	public float length;
	public float legHt;
	public TransformNode tableWorldPosition;
	
	//Scene graph accessors
	float posX = 0;
	float posY = 0;
	float posZ = 0;
	
	//Rendering
	NameNode tableRoot;
	public Table(float width, float length, float legHeight, Model cubeModel) {
		this.width = width;
		this.length = length;
		this.legHt = legHeight;
		buildIndex(cubeModel);
	}
	
	public Vec3 getPosition() {
		return new Vec3(posX, posY, posZ);
	}
	
	public void setPosition(float x, float y, float z) {
		//Update position of transform
		tableWorldPosition.setTransform(Mat4Transform.translate(x, y, y));
		tableWorldPosition.update();
		
		posX = x;
		posY = y;
		posZ = z;
	}
	
	public void buildIndex(Model cubeModel) {
		//Build table index from 4 cubes and a surface
		tableRoot = new NameNode("Table Root");
		
		//Global transform
		tableWorldPosition = new TransformNode("World position", Mat4Transform.translate(0, 0, 0));
		
		//Tabletop transform
		Mat4 m = Mat4Transform.translate(0, legHt, 0);
		TransformNode  tableTopTranslate = new TransformNode("Tabletop Translate", m);
		
			m = Mat4Transform.translate(0.5f, 0, 0.5f);			//origin point top left
			m = Mat4.multiply(Mat4Transform.scale(width, 0.5f, length), m);	 //Scale size up
			TransformNode tableTopTransform = new TransformNode("TableTop Transform", m);	//Local trans
			ModelNode tableTopShape = new ModelNode("Table top shape", cubeModel);
//			NameNode legsRoot;
		
			//Legs
			NameNode legsRoot = new NameNode("Legs");
				TransformNode legTranslate = new TransformNode("Leg translate", Mat4Transform.translate(0, 0, 0));
				TransformNode legTranslate2 = new TransformNode("Leg translate 2", Mat4Transform.translate(width, 0, 0));
				TransformNode legTranslate3 = new TransformNode("Leg translate 3", Mat4Transform.translate(width, 0, length));
				TransformNode legTranslate4 = new TransformNode("Leg translate 4", Mat4Transform.translate(0, 0, length));

				m = Mat4Transform.translate(0.5f, -0.5f, 0.5f);		//origin point top left
				m = Mat4.multiply(Mat4Transform.scale(1, legHt, 1), m);
				TransformNode legTransform = new TransformNode("Leg transform", m);
				
				m = Mat4Transform.translate(-0.5f, -0.5f, 0.5f);		//origin point top right
				m = Mat4.multiply(Mat4Transform.scale(1, legHt, 1), m);
				TransformNode legTransform2 = new TransformNode("Leg transform 2", m);
				
				m = Mat4Transform.translate(-0.5f, -0.5f, -0.5f);		//origin point bottom right
				m = Mat4.multiply(Mat4Transform.scale(1, legHt, 1), m);
				TransformNode legTransform3 = new TransformNode("Leg transform 3", m);

				m = Mat4Transform.translate(0.5f,-0.5f, -0.5f);		//origin point bottom left
				m = Mat4.multiply(Mat4Transform.scale(1, legHt, 1), m);
				TransformNode legTransform4 = new TransformNode("Leg transform 4", m);
				
				ModelNode legShape = new ModelNode("Leg shape", cubeModel);
				ModelNode legShape2 = new ModelNode("Leg shape2", cubeModel);
				ModelNode legShape3 = new ModelNode("Leg shape3", cubeModel);
				ModelNode legShape4 = new ModelNode("Leg shape4", cubeModel);

		//legTransform.addChild(legShape);
				
		//Join
		tableRoot.addChild(tableWorldPosition);
		tableWorldPosition.addChild(tableTopTranslate);
			tableTopTranslate.addChild(tableTopTransform);
				tableTopTransform.addChild(tableTopShape);
			tableTopTranslate.addChild(legsRoot);
				legsRoot.addChild(legTranslate);
					legTranslate.addChild(legTransform);
					legTransform.addChild(legShape);
					//legTransform.addChild(legShape);
				legsRoot.addChild(legTranslate2);
					legTranslate2.addChild(legTransform2);
					legTransform2.addChild(legShape2);
				legsRoot.addChild(legTranslate3);
					legTranslate3.addChild(legTransform3);
					legTransform3.addChild(legShape3);
				legsRoot.addChild(legTranslate4);
					legTranslate4.addChild(legTransform4);
					legTransform4.addChild(legShape4);
		//Update
		tableRoot.print(0, false);
		tableRoot.update();
	}
	
	public void render(GL3 gl) {
		tableRoot.draw(gl);
	}
}
