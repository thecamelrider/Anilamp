package anilamp;
import gmaths.*;
import meshes.Mesh;
import scenegraph.SGNode;

import java.nio.*;
import java.util.ArrayList;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

import gameobjects.LightObject;

public class Model {
  public Mesh mesh;
  //Shader uniforms
  public Shader shader;
  
  public Mat4 worldMatrix;
  public Mat4 modelMatrix; 
  public int[] textureId1; 
  public int[] textureId2; 
  public Material material;
  
  //Nodes attached to this model
  public ArrayList<SGNode> transforms = new ArrayList<SGNode> ();
  
  public Model(Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    //World matrix just replaces model matrix in teacher's code
    this.modelMatrix = modelMatrix;
    this.worldMatrix = modelMatrix;
    this.shader = shader;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
  }
  
  public Model(Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1) {
    this(shader, material, modelMatrix, mesh, textureId1, null);
  }
  
  public Model(Shader shader, Material material, Mat4 modelMatrix, Mesh mesh) {
    this(shader, material, modelMatrix, mesh, null, null);
  }
  
  public void setModelMatrix(Mat4 m) {
    modelMatrix = m;
  }
  
  public void dispose(GL3 gl) {
    mesh.dispose(gl);
    if (textureId1!=null) gl.glDeleteBuffers(1, textureId1, 0);
    if (textureId2!=null) gl.glDeleteBuffers(1, textureId2, 0);
  }
  
}