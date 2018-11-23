package anilamp;
import gmaths.*;
import meshes.Mesh;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

import gameobjects.LightObject;

public class Model {
  public Shader shader;
  public Mat4 modelMatrix;
  public Mesh mesh;
  public int[] textureId1; 
  public int[] textureId2; 
 
  public Material material;
  
  public Model(Shader shader, Material material, Mat4 modelMatrix, Mesh mesh, int[] textureId1, int[] textureId2) {
    this.mesh = mesh;
    this.material = material;
    this.modelMatrix = modelMatrix;
    this.shader = shader;
    this.textureId1 = textureId1;
    this.textureId2 = textureId2;
    
    //Should probably be handled somewhere else
    uint uniformBlockIndexRed    = glGetUniformBlockIndex(shaderRed.ID, "Matrices");
    unsined int uniformBlockIndexGreen  = glGetUniformBlockIndex(shaderGreen.ID, "Matrices");
    unsigned int uniformBlockIndexBlue   = glGetUniformBlockIndex(shaderBlue.ID, "Matrices");
    unsigned int uniformBlockIndexYellow = glGetUniformBlockIndex(shaderYellow.ID, "Matrices");  
      
    glUniformBlockBinding(shaderRed.ID,    uniformBlockIndexRed, 0);
    glUniformBlockBinding(shaderGreen.ID,  uniformBlockIndexGreen, 0);
    glUniformBlockBinding(shaderBlue.ID,   uniformBlockIndexBlue, 0);
    glUniformBlockBinding(shaderYellow.ID, uniformBlockIndexYellow, 0);

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