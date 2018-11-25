package anilamp;
import gmaths.*;
import meshes.Mesh;
import meshes.Sphere;
import meshes.TwoTriangles;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.sun.scenario.effect.impl.Renderer;

import gameobjects.*;

public class Anilamp_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public Anilamp_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,12f,18f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
	//Init scene time
	double startInit = getSeconds();
    GL3 gl = drawable.getGL().getGL3();
    
    //Scene renderer simply takes a huge array of meshes and renders them in series
    sceneRenderer = new SceneRenderer(drawable);	//Sets up scene renderer

    //Create all scene objects (Make sure to add to scene renderer!)
    initialise(gl);
    
    //Calc initialize time
    double endInit = getSeconds() - startInit;
    System.out.println("Initialize time: " + endInit);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    //Display FPS
    System.out.println("FPS: " + drawable.getAnimator().getLastFPS());
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    floor.dispose(gl);
    sphere.dispose(gl);
    cube.dispose(gl);
    cube2.dispose(gl);
  }
  
  
  // ***************************************************
  /* INTERACTION
   *
   *
   */
  
  private boolean animation = false;
  private double savedTime = 0;
  private SceneRenderer sceneRenderer;
  
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  //Scene objects
  private Model floor, sphere, cube, cube2, woodCube;
  
  //Renderables
  private Model[] myModels;
  
  //Extra stuff
  private Camera camera;
  private Mat4 perspective;
  private Lamp lamp;
  private Table table;
  private Light light;
  private Room room;
  
  private int[] uboBuffers;
  
  //Save ref to nodes
  private boolean animating, posing, jumping;
    
  private void initialise(GL3 gl) {
	//Initialize scene
	//Init randoms
	createRandomNumbers();
	
    //Default texture
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    //Shitty wood
    //int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    //int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    //Plaster wall
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/room/Plaster17_COL_VAR1_3K.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/room/Plaster17_REFL_3K.jpg");	
    //Tiles
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/room/Tiles05_COL_VAR1_3K.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/room/Tiles05_REFL_3K.jpg");
    //Wood flooring
    int[] textureId7 = TextureLibrary.loadTexture(gl, "textures/table/WoodFlooring044_COL_3K.jpg");   
    
    //Create shaders
    Shader cubeShader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
    Shader colorShader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
    
    //Add shaders to renderer
    sceneRenderer.addShader(cubeShader);
    sceneRenderer.camera = camera;
    
    //Add lights to scene renderer
    light = new Light();
    Light spotlight = new Light();
    Light directionLight = new Light();
    
    sceneRenderer.addLight(directionLight, Light.Types.DIRECTION);
    directionLight.ambient = new Vec3(0, 0, 0);
    directionLight.diffuse = new Vec3(0.2f, 0.1f, 0.4f);
    directionLight.specular = new Vec3(0.2f, 0.2f, 0.2f);
    directionLight.direction = new Vec3(0, -0.2f, 0.8f);
    
    //Cube shader
    Shader shader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
    shader.use(gl);
    
    //Room mesh and material
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(24,1f,24);
    floor = new Model(shader, material, modelMatrix, mesh, textureId5);
    
    mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
    material = new Material(new Vec3(0.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    modelMatrix = Mat4Transform.scale(4f,1f,4f);
    Model wall = new Model(shader, material, modelMatrix, mesh, textureId4);
    
    //Some weird thing
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(shader, material, modelMatrix, mesh, textureId3, textureId4);
    
    //Wooden cubes
    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube2 = new Model(shader, material, modelMatrix, mesh, textureId3, textureId4);
    cube = new Model(shader, material, modelMatrix, mesh, textureId5, textureId6); 
    
    woodCube = new Model(shader, material, modelMatrix, mesh, textureId7);
    //Setting up all unit models
    
    //Create wall and floors
    room = new Room(48, 24, 18, floor);
    
    //Create table
    table = new Table(6, 10, 5, woodCube);
    table.setPosition(0, 0, 0);
    
    //Create lamp
    //lamp = new Lamp(3, 0.7f, cube, cube2, spotlight);
    myModels = new Model[] {woodCube, floor}; 
  }
  
  private void render(GL3 gl) {
 
	//Update position of all root nodes and then render all models
	//sceneRenderer.updateTransforms();
    sceneRenderer.render(gl, myModels);
    sceneRenderer.render(gl, rootNodes);
    if(jumping)
    	updateJumpAnim();
    if(posing)
    	updatePoseAnim();
    
  }

  public void updateJumpAnim() {
	  
  }
  
  public void updatePoseAnim() {
	  
  }
 
  public void JumpToRandomPosition() {
	// If already animating ignore
	if(animating)
		return;
	
	//Otherwise set final jump position
	
	//Change table pos
	Vec3 pos = table.getPosition();
	table.setPosition(pos.x + 1f, pos.y, pos.z);
	
	//Random end pos
	float targetX = (float) ((Math.random() * table.width) + 0);
	float targetZ = (float) ((Math.random() * table.length) + 0);

	Vec3 targetPos = new Vec3(targetX, 0, targetZ);
  }
	
  public void strikeRandomPose() {
	// If already animating ignore
	if (animating)
		return;
	
	//Otherwise set final pose
	
    //double elapsedTime = getSeconds()-startTime;
    //float rotateAngle = 180f+90f*(float)Math.sin(elapsedTime);
    //leftArmRotate.setTransform(Mat4Transform.rotateAroundX(rotateAngle));
    //leftArmRotate.update();
  }
  
  public void toggleLight() {
		lamp.toggleLight();
	}
  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);   
    //return new Vec3(5f,3.4f,5f);
  }

  
  // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
}