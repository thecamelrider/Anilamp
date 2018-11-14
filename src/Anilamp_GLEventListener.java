import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
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
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
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
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    light.dispose(gl);
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
   
  public void startAnimation() {
    animation = true;
    startTime = getSeconds()-savedTime;
  }
   
  public void stopAnimation() {
    animation = false;
    double elapsedTime = getSeconds()-startTime;
    savedTime = elapsedTime;
  }
   
  public void incXPosition() {
    xPosition += 0.5f;
    if (xPosition>5f) xPosition = 5f;
    updateMove();
  }
   
  public void decXPosition() {
    xPosition -= 0.5f;
    if (xPosition<-5f) xPosition = -5f;
    updateMove();
  }
 
  private void updateMove() {
    robotMoveTranslate.setTransform(Mat4Transform.translate(xPosition,0,0));
    robotMoveTranslate.update();
  }
  
  public void loweredArms() {
    stopAnimation();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(180));
    rightArmRotate.update();
  }
   
  public void raisedArms() {
    stopAnimation();
    leftArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    leftArmRotate.update();
    rightArmRotate.setTransform(Mat4Transform.rotateAroundX(0));
    rightArmRotate.update();
  }
  
  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */

  private Camera camera;
  private Mat4 perspective;
  private Model floor, sphere, cube, cube2;
  private Light light;
  private SGNode lampRoot;
    
  //Animation params
  private boolean animating, posing, jumping;
  
  private float xPosition = 0;
  private TransformNode translateX, robotMoveTranslate, leftArmRotate, rightArmRotate;
  
  private TransformNode lTranslateX, lampMoveTranslate, lowerArmRotate, upperArmRotate;
  
  private void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/wattBook.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/wattBook_specular.jpg");
    
    light = new Light(gl);
    light.setCamera(camera);
    
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_tt_05.txt", "shaders/fs_tt_05.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.3f, 0.3f, 0.3f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId0);
    
    mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId1, textureId2);
    
    mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "shaders/vs_cube_04.txt", "shaders/fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
    cube = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId3, textureId4);
    
    cube2 = new Model(gl, camera, light, shader, material, modelMatrix, mesh, textureId5, textureId6); 
    
    // Lamp Dimensions
    float baseHeight = 0.5f;
    float armLength = 6;
    
    //Lamp builder
    lampRoot = new NameNode("root");
    lampMoveTranslate = new TransformNode("PlayerTransform", Mat4Transform.translate(5f, 0, 0));
    TransformNode lampTranslate = new TransformNode("lamp transform", Mat4Transform.translate(0.5f, 0, 0));
    
    NameNode base = new NameNode("base");
	Mat4 a = Mat4Transform.translate(0, 0.5f, 0);	//Bottom origin point
	a = Mat4.multiply(Mat4Transform.scale(3, baseHeight, 2.25f), a);	//Scale
		TransformNode baseTransform = new TransformNode("base transform", a);
		ModelNode baseModel = new ModelNode("Cube(base)", cube);
	
	//Lower arm
	//Translate lower arm
	//Rotate upper arm
    TransformNode translateLower = new TransformNode("Translate lower Arm",
    		Mat4Transform.translate(0, baseHeight, 0));
    lowerArmRotate = new TransformNode("Rotate lower arm",
    		Mat4Transform.rotateAroundZ(-20));
    
    NameNode lowerArm = new NameNode("lower arm");
	a = new Mat4(1);
	a = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), a);		//Bottom origin point
	a = Mat4.multiply(Mat4Transform.scale(1, armLength, 1), a);		//Scale
	//a = Mat4.multiply(Mat4Transform.rotateAroundZ(20), a);		//Local rotation
	
	TransformNode lowerArmTransform = new TransformNode("lower arm transform", a);
		ModelNode lowerArmModel = new ModelNode("Cube(lower arm)", cube);
		
	//Translate translate upper arm
	TransformNode translateUpper = new TransformNode("Translate upper", 
			Mat4Transform.translate(0, armLength, 0));
	
	upperArmRotate = new TransformNode("Rotate upper",
			Mat4Transform.rotateAroundZ(-20));
	
	//Upper arm
    NameNode upperArm = new NameNode("upper arm");
	a = new Mat4(1);
	a = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), a);		//Bottom origin point
	a = Mat4.multiply(Mat4Transform.scale(1, armLength, 1), a);		//Scale
	//a = Mat4.multiply(Mat4Transform.rotateAroundZ(20), a);		//Local Rotation
		TransformNode upperArmTransform = new TransformNode("upper arm transform", a);
		ModelNode upperArmModel = new ModelNode("Cube(upper arm)", cube);
	
	//Translate head
	TransformNode translateHead = new TransformNode("Translate head", 
			Mat4Transform.translate(0, armLength, 0));
	
	//Head
    NameNode head = new NameNode("head");
	a = new Mat4(1);
	a = Mat4.multiply(Mat4Transform.translate(0, 0.5f, 0), a);		//Bottom origin point
	a = Mat4.multiply(Mat4Transform.scale(4, 2, 2), a);				//Scale
		TransformNode headTransform = new TransformNode("head transform", a);
		ModelNode headModel = new ModelNode("Cube(head)", cube);

	
    //Build index
    lampRoot.addChild(lampMoveTranslate);
    	lampMoveTranslate.addChild(lampTranslate);
    		lampTranslate.addChild(base);
    		base.addChild(baseTransform);
    			baseTransform.addChild(baseModel);
    		base.addChild(translateLower);
    			//lowerArmRotate.addChild(translateLower);
    			//translateLower.addChild(lowerArm);
    			translateLower.addChild(lowerArmRotate);
    			lowerArmRotate.addChild(lowerArm);
	    			lowerArm.addChild(lowerArmTransform);
	    			lowerArmTransform.addChild(lowerArmModel);
	    			lowerArm.addChild(translateUpper);
	    			translateUpper.addChild(upperArmRotate);
	    			upperArmRotate.addChild(upperArm);
    					upperArm.addChild(upperArmTransform);
    					upperArmTransform.addChild(upperArmModel);
    					upperArm.addChild(translateHead);
    					translateHead.addChild(head);
    						head.addChild(headTransform);
    						headTransform.addChild(headModel);
    //Lamp
    				
    lampRoot.update();
    lampRoot.print(0, false);
  }
  
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);
    floor.render(gl);
    
    if(jumping)
    	updateJumpAnim();
    if(posing)
    	updatePoseAnim();
    
    lampRoot.draw(gl);
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