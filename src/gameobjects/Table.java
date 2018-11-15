package gameobjects;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;

import anilamp.*;
import gmaths.*;

public class Table {
	public float width;
	public float length;
	public float legHeight;
	
	NameNode tableRoot;
	
	public Table(float width, float length, float legHeight, Model cubeModel) {
		this.width = width;
		this.length = length;
		this.legHeight = legHeight;
		buildIndex(cubeModel);
	}
	
	public void buildIndex(Model cubeModel) {
		//Build table index from 4 cubes and a surface
		tableRoot = new NameNode("Table Root");
		
		Mat4 m = Mat4Transform.translate(0, legHeight, 0);
		TransformNode  tableTopTranslate = new TransformNode("Tabletop Translate", m);
			m = Mat4Transform.translate(width/2, 0, length/2);			//origin point top left
			m = Mat4.multiply(Mat4Transform.scale(width, 1, length), m);					
			TransformNode tableTopTransform = new TransformNode("TableTop Transform", m);	//Local trans
			ModelNode tableTopShape = new ModelNode("Table top shape", cubeModel);
//			NameNode legsRoot;
		
			//Legs
			NameNode legsRoot = new NameNode("Legs");
				TransformNode legTranslate = new TransformNode("Leg translate", Mat4Transform.translate(0, legHeight, length/2));
				TransformNode legTranslate2 = new TransformNode("Leg translate 2", Mat4Transform.translate(-width/2, legHeight, length/2));

				m = Mat4Transform.scale(1, legHeight, 1);	//Local scale
				TransformNode legTransform = new TransformNode("Leg transform", m);
				ModelNode legShape = new ModelNode("Leg shape", cubeModel);
		
		legTransform.addChild(legShape);
				
		//Join
		tableRoot.addChild(tableTopTranslate);
			tableTopTranslate.addChild(tableTopTransform);
				tableTopTransform.addChild(tableTopShape);
			tableTopTranslate.addChild(legsRoot);
				legsRoot.addChild(legTranslate);
					legTranslate.addChild(legTransform);
//					legTransform.addChild(legShape);
				legsRoot.addChild(legTranslate2);
					legTranslate2.addChild(legTransform);
					
		//Update
		tableRoot.print(0, false);
		tableRoot.update();
	}
	public void render(GL3 gl) {
		tableRoot.draw(gl);
	}
}
