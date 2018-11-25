package anilamp;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import anilamp.Light.Types;
import gmaths.Mat4;
import javafx.geometry.VPos;
import scenegraph.SGNode;

public class SceneRenderer {
	//Buffers and cam
	//CAMERA SHOULD NOT BE HERE, ONLY PV
	public Camera camera;
	private int[] uboBuffers;
	
	//MVP matrix
	private Mat4 pv;
	
	//Shader
	private Shader shader;
	
	//Lights
	private Light dirLight;
	private Light spotLight;
	private Light[] lights;
	private Light[] pointLights;
	private int numLights = 0;
	private final int MAX_LIGHTS = 5;
	
	//Renderables
	//Model[] models;
	SGNode[] rootNodes;
	
	public SceneRenderer(GLAutoDrawable drawable) {
		//Useful Debug
	    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
	    GL3 gl = drawable.getGL().getGL3();

		//Setup rendering options
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
	    gl.glClearDepth(1.0f);
	    gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glDepthFunc(GL.GL_LESS);
	    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
	    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
	    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW

	    //Init lists
	    pointLights = new Light[MAX_LIGHTS];
	    
		//Create 2 UBOS and bind to i
		//0 for matrix uniforms (Nevermind not important for now)
		//1 for lighting uniforms
	    /*
		gl.glGenBuffers(1, uboBuffers, 0);
		gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, uboBuffers[0]);
		gl.glBufferData(gl.GL_UNIFORM_BUFFER, 152, null, gl.GL_STATIC_DRAW); // BULLSHIT SIZE
		gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, 0);
		*/
	    
		/*
		 * BINDS UNIFORM BUFFER TO UNIFORM BLOCK ARRAY
		glBindBufferBase(GL_UNIFORM_BUFFER, 2, uboExampleBlock); 
		// or
		glBindBufferRange(GL_UNIFORM_BUFFER, 2, uboExampleBlock, 0, 152);
		*/
		
	}
	
	public void addSceneObject() {
		
	}
	public void addLight(Light light, Light.Types lightType) {
		//Update UBO
		
		switch (lightType) {
		case DIRECTION:
			dirLight = light;
			break;
		case SPOTLIGHT:
			spotLight = light;
			break;
		case POINTLIGHT:
			pointLights[numLights] = light;
			numLights++;
			break;
		default:
			break; 
		}
				
	}
	
	public void removeLight(Light light) {
		//Update UBO
	}
	
	public void updateLightBuffers(GL3 gl) {
		//Go through buffer and subbufferdata 
		//Update lights uniform buffer 
		/*
		glBindBuffer(GL_UNIFORM_BUFFER, uboBuffers[0]);
		glBufferSubData(GL_UNIFORM_BUFFER, 0, size, updatedLightData);
		glBindBuffer(GL_UNIFORM_BUFFER, 0);
		*/
		
		//Just do it the ugly way
        // directional light
        shader.setVec3(gl, "dirLight.direction", dirLight.direction);
        shader.setVec3(gl, "dirLight.ambient", dirLight.ambient);
        shader.setVec3(gl, "dirLight.diffuse", dirLight.diffuse);
        shader.setVec3(gl, "dirLight.specular", dirLight.specular);
        
        //For each pointlight
        for(int i = 0; i < numLights; i++) {
        	String pointLightID = "pointLights[" + i + "]";
        	Light ptLight = pointLights[i];
        	
        	shader.setVec3(gl, pointLightID + ".position", ptLight.position);
        	shader.setVec3(gl, pointLightID + ".ambient", ptLight.ambient);
        	shader.setVec3(gl, pointLightID + ".diffuse", ptLight.diffuse);
        	shader.setVec3(gl, pointLightID + ".specular", ptLight.specular);
        	
            //Attenuation params
        	shader.setFloat(gl, pointLightID + ".constant", 1.0f);
        	shader.setFloat(gl, pointLightID + ".linear", 0.09f);
        	shader.setFloat(gl, pointLightID + ".quadratic", 0.032f);
        }
    }
	
	//Gets updated by cam
	public void updatePV(Mat4 perspective, Mat4 view) {
		pv = Mat4.multiply(perspective, view);
	}
	
	public void updateTransforms(SGNode[] roots) {
		//Update all positions
		for (int i =0; i < roots.length; i++) {
			SGNode rootNode = roots[i];
			rootNode.update();
		}
		
	}
	
	public void render(GL3 gl, SGNode[] rootNodes) {
		//Extract models from root nodes
		
	}
	//Sexy Pure function, not pure anymore :(
	public void render(GL3 gl, Model[] models) {
		//This is gonna be moved outta here
		updatePV(camera.getPerspectiveMatrix(), camera.getViewMatrix());

		//Clear color buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	
		//Really disgusted by all the scattered, virtual and overly OOP rendering
	    //Full rendering is in here now
	    
	    //Phase 1: update all the shader params for each mode
	    for(Model model : models) {
		    //Set shader props of each model
		    shader.use(gl);

		    //Matrix
		    Mat4 mvpMatrix = Mat4.multiply(pv, model.modelMatrix);
		    shader.setFloatArray(gl, "model", model.modelMatrix.toFloatArrayForGLSL());
		    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());		    
		    shader.setVec3(gl, "viewPos", camera.getPosition());

		    //Lighting
		    updateLightBuffers(gl);
		    
		    //Material props
		    shader.setVec3(gl, "material.ambient", model.material.getAmbient());
		    shader.setVec3(gl, "material.diffuse", model.material.getDiffuse());
		    shader.setVec3(gl, "material.specular", model.material.getSpecular());
		    shader.setFloat(gl, "material.shininess", model.material.getShininess());  
		    
		    //Diffuse map and spec map
		    if (model.textureId1!=null) {
		      shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
		      gl.glActiveTexture(GL.GL_TEXTURE0);
		      gl.glBindTexture(GL.GL_TEXTURE_2D, model.textureId1[0]);
		    }
		    if (model.textureId2!=null) {
		      shader.setInt(gl, "second_texture", 1);
		      gl.glActiveTexture(GL.GL_TEXTURE1);
		      gl.glBindTexture(GL.GL_TEXTURE_2D, model.textureId2[0]);
		    }

		    //Shader properties set, ready to render mesh
		    //Render mesh
	        gl.glBindVertexArray(model.mesh.vertexArrayId[0]);
	        gl.glDrawElements(GL.GL_TRIANGLES, model.mesh.indices.length, GL.GL_UNSIGNED_INT, 0);
	        gl.glBindVertexArray(0);
	    }
	}

	public void addShader(Shader shader) {
		this.shader = shader;
	}
}
