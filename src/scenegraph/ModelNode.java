package scenegraph;
import com.jogamp.opengl.*;

import anilamp.Model;
import gmaths.Mat4;

public class ModelNode extends SGNode {

  protected Model model;

  public ModelNode(String name, Model m) {
    super(name);
    model = m;
    model.transforms.add(this);
  }
  
  @Override
	protected void update(Mat4 t) {
		this.worldTransform = t;
	}
  
}