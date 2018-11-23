package gameobjects;

import com.jogamp.opengl.GL3;

import anilamp.Material;
import anilamp.Model;
import scenegraph.NameNode;

public class Room {
	float width;
	float length;
	float wallHeight;
	
	Model floorModel;
	Model wallModel;
	
	public Room(float width, float length, float wallHeight, Model floorModel) {
		this.width = width;
		this.length = length;
		this.wallHeight = wallHeight;
		this.floorModel = floorModel;
	}
	
	void buildIndex() {
		//Build wallModel mesh
		NameNode room = new NameNode("Room");
			NameNode wall = new NameNode("Wall");
		
	}
}
