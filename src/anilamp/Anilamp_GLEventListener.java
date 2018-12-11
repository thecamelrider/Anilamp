package anilamp;
import gmaths.*;
import meshes.*;
import scenegraph.*;

import java.nio.*;
import java.util.ArrayList;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;

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
    initScene(gl);	//Creates all scene objects
    
    //Calc initialize time
    double endInit = getSeconds() - startInit;
    System.out.println("Initialize time: " + endInit);
    startTime = getSeconds();
    
    //Started
    int size = 0;
    for (Model model : myModels) {
    	//Add to size
    	size += model.transforms.size();
    }
    System.out.println("Num objects = " + myModels.size());
    System.out.println("INIT SUCCESS!");
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
    //System.out.println("FPS: " + drawable.getAnimator().getLastFPS());
    render(gl);		//Using scenerenderer
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    for (Model model : myModels) {
    	model.dispose(gl);
    	}
    }
  
  // ***************************************************
  /* INTERACTION
   *
   *	Handle animation separately?
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

  //Scene objects for rendering/updating
  private ArrayList<SGNode> myRootNodes = new ArrayList<SGNode>();
  private ArrayList<Model> myModels = new ArrayList<Model>();
    
  //Important scene objects
  private Camera camera;
  
  private Mat4 perspective;
  private Lamp lamp;
  private Table table;
  private Light sun;
  private Room room;
  
  private int[] uboBuffers;
  
  //Save ref to nodes
  private boolean animating, posing, jumping;
    
  private void initScene(GL3 gl) {
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
    
    //Add shaders to renderer
    //OPTIONAL MOVE SHADERS OUT OF MODELS/ ONLY STORE ID
    //Create shaders
    Shader cubeShader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
    Shader colorShader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
    sceneRenderer.addShader(cubeShader);
        
    //Add lights to scene renderer
    sun = new Light();
    Light sun = new Light();
    sun.ambient = new Vec3(0, 0, 0);
    sun.diffuse = new Vec3(0.2f, 0.1f, 0.4f);
    sun.specular = new Vec3(0.2f, 0.2f, 0.2f);
    sun.direction = new Vec3(0, -0.2f, 0.8f);
    sun.position = new Vec3(0, 15, 0);
    
    //Light spotlight = new Light();
    
    Light pointLight = new Light();
    pointLight.ambient = new Vec3(1, 1, 1);
    pointLight.diffuse = new Vec3(1, 1, 1);
    pointLight.specular = new Vec3(1, 1, 1);
    pointLight.position = new Vec3(0, 5, 0);
    
    Light pointLight2 = new Light();
    pointLight2.ambient = new Vec3(1, 1, 1);
    pointLight2.diffuse = new Vec3(0.3f, 0.8f, 0.3f);
    pointLight2.specular = new Vec3(1, 1, 1);
    pointLight2.position = new Vec3(5, 5, 0);
    
    sceneRenderer.addLight(pointLight2, Light.Types.POINTLIGHT);
    sceneRenderer.addLight(pointLight, Light.Types.POINTLIGHT);
    sceneRenderer.addLight(sun, Light.Types.DIRECTION);
    
    //Reused parts
    //Create all the meshes, shaders and mats
    Mesh planeMesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Mesh sphereMesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    Mesh cubeMesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    Shader shader = new Shader(gl, "shaders/phong/vs_cube.txt", "shaders/phong/fs_cube.txt");
   // Material standardMat = new Material(new Vec3(0.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Material standardMat = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    
    //Instances
    Model floorModel, wallModel, sphere, cube, cube2, woodCube, stoneCube, plasterCube;
    
    //Floor model
    Mat4 modelMatrix = Mat4Transform.scale(24f,1f,24f);
    floorModel = new Model(shader, standardMat, modelMatrix, planeMesh, textureId5);
    
    //Wall model
    modelMatrix = Mat4.multiply(Mat4Transform.scale(10f,1f,10f), Mat4Transform.rotateAroundZ(90));
    wallModel = new Model(shader, standardMat, modelMatrix, planeMesh, textureId3, textureId4);
    
    //Unit spheres
    modelMatrix = Mat4.multiply(Mat4Transform.scale(1,1,1), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(shader, standardMat, modelMatrix, sphereMesh, textureId3, textureId4);
    //Unit cubes
    modelMatrix = Mat4.multiply(Mat4Transform.scale(1,1,1), Mat4Transform.translate(0,0.5f,0));
    
    plasterCube = new Model(shader, standardMat, modelMatrix, cubeMesh, textureId3, textureId4);
    stoneCube = new Model(shader, standardMat, modelMatrix, cubeMesh, textureId5, textureId6); 
    woodCube = new Model(shader, standardMat, modelMatrix, cubeMesh, textureId7);
    
    //Complex objects
    //Create room, table and lamp
    room = new Room(48, 24, 18, floorModel, wallModel);
    table = new Table(6.1f, 7.62f, 6.1f, 1, woodCube);
    
    //table.setPosition(0, 0, 0);
    lamp = new Lamp(1.5f, 0.3f, 0.7f, stoneCube, plasterCube, new Light());

    //Add models to database
    myModels.add(woodCube);
    myModels.add(stoneCube);
    myModels.add(plasterCube);
    myModels.add(floorModel);
    myModels.add(wallModel);
    myModels.add(sphere);
    
    //Add complex objects to scene
    addObjectsToScene(new SceneObject[] {lamp, table, room});
    
    //Add test object
    addSimpleObject(sphere, new Vec3(1, 7, 1));
  }
  
  private void addSimpleObject(Model model, Vec3 position) {
	  //Create a node for a test object
	  NameNode rootNode = new NameNode("small object");
	  TransformNode posNode = new TransformNode("position", Mat4Transform.translate(position));
	  ModelNode modelNode = new ModelNode("Model", model);
			  
	  rootNode.addChild(posNode);
	  	posNode.addChild(modelNode);
	  	
	  	rootNode.update();
	  
	  myRootNodes.add(rootNode);
  }
  
  private void addObjectsToScene(SceneObject[] sceneObjects) {
	  //store all root nodes to list
	  for(SceneObject sceneObj : sceneObjects) {
		  myRootNodes.add(sceneObj.getRootNode());
	  }
  }
  
  //-----Update/render loop
  private void render(GL3 gl) {
 
	//Update animations
	if(jumping)
    	updateJumpAnim();
    if(posing)
    	updatePoseAnim();

	//Update all model transforms
	for (SGNode node : myRootNodes) {
		node.update();
	}
	
	//Render all models
    sceneRenderer.render(gl, myModels, camera.getPerspectiveMatrix(), camera.getViewMatrix());
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