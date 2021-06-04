import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;


public class Planet {

    float radius;
    float distance;
    float orbitSpeed;
    float axisTilt;
    String name;
    List<Planet> satellites = new ArrayList<>();
    float orbitAnimation = 0.0f;
    float axisAnimation = 0.0f;
    TextureHandler rings;
    TextureHandler moon;
    GLUT glut = new GLUT();
    GL2 gl2;
    GLU glu;
    GL gl;
    

    Planet(float radius, float distance, float orbitSpeed, float axisTilt, String name, ArrayList<Planet> satellites, GL gl, GL2 gl2, GLU glu) {
        this.radius = radius;
        this.distance = distance;
        this.orbitSpeed = orbitSpeed;
        this.axisTilt = axisTilt;
        this.name = name;
        this.satellites = satellites;
        this.gl = gl;
        this.gl2 = gl2;
        this.glu = glu;
        setMaterialProp(name,gl2);
        GLUgl2 glugl = new GLUgl2();
        setTexture();
    }
    
    private void setMaterialProp(String name,GL2 gl) {
    	float[] rgba = { 1f, 1f, 1f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		
		if(name.contentEquals("Sun")) {
			
			float SHINE_ALL_DIRECTIONS = 1;
			float[] lightPos = { 0, 0, 0, 0 };
			float[] lightColorAmbient = { 0.5f, 0.5f, 0.5f, 1f };
			float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };
			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
//			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
//			gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);
//			gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 1f);
			gl.glEnable(GL2.GL_LIGHT1);
			gl.glEnable(GL2.GL_LIGHTING);
		
		}else {
		
			gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.6f);
		}
    }
    /// add here you texture please
    
    void setTexture() {

    	 GLUgl2 glugl = new GLUgl2();
         this.rings = new TextureHandler(gl2, glugl, MainFrame.devPath +"mercury.bmp", false);
         this.moon = new TextureHandler(gl2, glugl, MainFrame.devPath +"moontexture.jpg", false);
        

    };


    void move(float speedExp) {
        orbitAnimation += orbitSpeed * Math.pow(2.0, speedExp);
        for (Planet s : satellites) {
            s.move(speedExp);
        }
    }


    void rotate(float speedExp) {
        axisAnimation += 10.0 * Math.pow(2.0, speedExp);
    }

    void drawOrbitalTrail() {
        this.glut.glutWireTorus(0.001, distance, 6, 100);
    }



    void drawSmallOrbit() {
        ((GLMatrixFunc) gl).glPushMatrix();
        gl2.glColor3ub((byte) 255, (byte) 255, (byte) 255);
        ((GLMatrixFunc) gl).glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        glut.glutWireTorus(0.001, distance, 6, 60);
        ((GLMatrixFunc) gl).glPopMatrix();
    }

    ;

    void drawMoon(GLUquadric quadric) {
        ((GLMatrixFunc) gl).glPushMatrix();
        gl2.glColor3ub((byte) 255, (byte) 255, (byte) 255);
        ((GLMatrixFunc) gl).glRotatef(orbitAnimation, 0.0f, 1.0f, 0.0f);
        ((GLMatrixFunc) gl).glTranslatef(distance, 0.0f, 0.0f);
        glu.gluSphere(quadric, radius, 20, 20);
        ((GLMatrixFunc) gl).glPopMatrix();
    }

    ;

    // GLUquadric quadric = glu.gluNewQuadric();
    void draw(int showLabels, int showSatellitesOrbit, int showSatellites, GLUquadric quadric) {

        ((GLMatrixFunc) gl).glPushMatrix();

        ((GLMatrixFunc) gl).glRotatef(orbitAnimation, 0.0f, 1.0f, 0.0f);
        ((GLMatrixFunc) gl).glTranslatef(distance, 0.0f, 0.0f);
        if (showLabels == 1) {
            if (name == "Sun")
                gl2.glRasterPos3f(-1.2f, 7.0f, 0.0f);
            else
                gl2.glRasterPos3f(0.0f, 3f, 0.0f);
            gl2.glColor3ub((byte) 255, (byte) 255, (byte) 255);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, name);
        }
        ((GLMatrixFunc) gl).glPushMatrix();
        ((GLMatrixFunc) gl).glRotatef(axisTilt, 1.0f, 0.0f, 0.0f);
        ((GLMatrixFunc) gl).glRotatef(axisAnimation, 0.0f, 1.0f, 0.0f);
        ((GLMatrixFunc) gl).glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        gl2.glEnable(GL.GL_TEXTURE_2D);
//	    gl.glBindTexture(GL.GL_TEXTURE_2D, textureId);
        String imageName = name.toLowerCase();
        GLUgl2 glugl = new GLUgl2();

        TextureHandler handler = new TextureHandler(gl2, glugl, MainFrame.devPath  + imageName + ".bmp", false);


        handler.bind();
        handler.enable();

        glu.gluQuadricTexture(quadric, true);
        glu.gluSphere(quadric, radius, 20, 20);

        handler.disable();
        ((GLMatrixFunc) gl).glPopMatrix();
        if (name.contentEquals("Saturn")) {
            // Draw Saturn's rings
//            TextureHandler rings = new TextureHandler(gl2, glugl, MainFrame.devPath+"mercury.bmp", false);
            rings.bind();
            rings.enable();
            ((GLMatrixFunc) gl).glPushMatrix();
            gl2.glColor3ub((byte) 158, (byte) 145, (byte) 137);
            ((GLMatrixFunc) gl).glRotatef(-63.0f, 1.0f, 0.0f, 0.0f);
            glut.glutWireTorus(0.15, 4.0, 3, 60);
           
            glut.glutSolidSphere(0.15, 60, 3);
            rings.bind();
            rings.enable();
            glut.glutWireTorus(0.3, 3.25, 3, 60);
           
            ((GLMatrixFunc) gl).glPopMatrix();
            rings.disable();
        }
        if (showSatellitesOrbit == 1) {
            for (Planet s : satellites) {
                s.drawSmallOrbit();
            }
        }
        if (showSatellites == 1) {
            for (Planet s : satellites) {
            	

                moon.bind();
                moon.enable();
                s.drawMoon(quadric);
                moon.disable();

            }
        }
        ((GLMatrixFunc) gl).glPopMatrix();
    }

    ;




}



