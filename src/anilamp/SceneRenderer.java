package anilamp;

/* I declare that this code is my own work */ 
/* Author Husain Ahmed huss54@gmail.com */

import java.util.ArrayList;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import anilamp.Light.Types;
import gmaths.Mat4;
import javafx.geometry.VPos;
import jogamp.common.Debug;
import scenegraph.SGNode;

public class SceneRenderer {
	//Shader
	private Shader shader;
	
	//Lights
	private Light dirLight;
	private Light spotLight;
	private Light[] pointLights;
	private Light[] lights;
	private int numLights = 0;
	private final int MAX_LIGHTS = 5;
	
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
		
		boolean spotLightActive = spotLight.active;
		boolean dirLightActive = dirLight.active;
		
		shader.setInt(gl, "dirLightActive", (dirLightActive) ? 1 : 0);
		shader.setInt(gl, "spotLightActive", (spotLightActive) ? 1 : 0);
		
		//Just do it the ugly way
        // directional light
		if(dirLight.active) {
	        shader.setVec3(gl, "dirLight.direction", dirLight.direction);
	        shader.setVec3(gl, "dirLight.ambient", dirLight.ambient);
	        shader.setVec3(gl, "dirLight.diffuse", dirLight.diffuse);
	        shader.setVec3(gl, "dirLight.specular", dirLight.specular);			
		}
        
        shader.setFloat(gl, "numPointLights", numLights);
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
        
        if(spotLight.active) {
        	shader.setVec3(gl, "spotLight.position", spotLight.position);
	        shader.setVec3(gl, "spotLight.direction", spotLight.direction);
	        shader.setVec3(gl, "spotLight.ambient", spotLight.ambient);
	        shader.setVec3(gl, "spotLight.diffuse", spotLight.diffuse);
	        shader.setVec3(gl, "spotLight.specular", spotLight.specular);			
	        
            //Attenuation params
        	shader.setFloat(gl, "spotLight.constant", 1.0f);
        	shader.setFloat(gl, "spotLight.linear", 0.09f);
        	shader.setFloat(gl, "spotLight.quadratic", 0.032f);
        	
        	//Light size
        	shader.setFloat(gl, "spotLight.cutOff", spotLight.cutoff);
        	shader.setFloat(gl, "spotLight.outerCutOff", spotLight.outercutoff);

        }
    }
		
	public void updateTransforms(SGNode[] roots) {
		//Update all positions
		for (int i =0; i < roots.length; i++) {
			SGNode rootNode = roots[i];
			rootNode.update();
		}
		
	}
	
	//Sexy Pure function, not pure anymore :(
	public void render(GL3 gl, ArrayList<Model> models, Mat4 perspectiveMat, Mat4 viewMat) {
		//Clear color buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		//Compute pv
		Mat4 pv = Mat4.multiply(perspectiveMat, viewMat);
		
		//Go through all models and render
	    for(Model model : models) {
		    //Set shader props of each model
		    //shader.use(gl);
	    	shader = model.shader;
	    	shader.use(gl);
	    	
	    	//---------Can be set once in the future within uniform block
	    	//Set all the uniforms
		    //Set matrix uniforms in shader		    
		    shader.setFloatArray(gl, "viewPos", viewMat.toFloatArrayForGLSL());
		    
		    //Set lighting uniforms
		    updateLightBuffers(gl);
		    //-----------------------------------------
		    
		    //Set Material props
		    //shader.setVec3(gl, "material.ambient", model.material.getAmbient());
		    //shader.setVec3(gl, "material.diffuse", model.material.getDiffuse());
		    //shader.setVec3(gl, "material.specular", model.material.getSpecular());
		    shader.setFloat(gl, "material.shininess", model.material.getShininess());  
		    
		    //Diffuse map and spec map
		    if (model.textureId1!=null) {
		      shader.setInt(gl, "material.diffuse", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
		      gl.glActiveTexture(GL.GL_TEXTURE0);
		      gl.glBindTexture(GL.GL_TEXTURE_2D, model.textureId1[0]);
		    }
		    
		    if (model.textureId2!=null) {
		      shader.setInt(gl, "material.specular", 1);
		      gl.glActiveTexture(GL.GL_TEXTURE1);
		      gl.glBindTexture(GL.GL_TEXTURE_2D, model.textureId2[0]);
		    }
		    
		    //Foreach instance of model
		    for(SGNode transform : model.transforms) {
		    	Mat4 modelMatrix = Mat4.multiply(model.modelMatrix, transform.worldTransform);
		    	//modelMatrix = transform.worldTransform;
		    	
			    Mat4 mvpMatrix = Mat4.multiply(pv, modelMatrix);
			    //Combine local model transform and world transform from scene graph
			    shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
			    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
			    
			    //Render mesh
		        gl.glBindVertexArray(model.mesh.vertexArrayId[0]);
		        gl.glDrawElements(GL.GL_TRIANGLES, model.mesh.indices.length, GL.GL_UNSIGNED_INT, 0);
		        gl.glBindVertexArray(0);
		    }
	    }	    
	}

	public void addShader(Shader shader) {
		this.shader = shader;
	}
}
