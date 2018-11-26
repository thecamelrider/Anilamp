package scenegraph;
import com.jogamp.opengl.*;

import anilamp.Model;

public class ModelNode extends SGNode {

  protected Model model;

  public ModelNode(String name, Model m) {
    super(name);
    model = m; 
  }

  public void update() {
	  //Update world transform!
	  model.worldMatrix = worldTransform;
  }
}