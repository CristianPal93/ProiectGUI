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
    }

    void setTexture() {
        String imageName = name.toLowerCase();
        GLUgl2 glugl = new GLUgl2();
        System.out.println(imageName);
        TextureHandler handler = new TextureHandler(gl2, glugl, "/Users/macbook/git/ProiectGUI/ProiectGUI/texture/" + imageName + ".bmp", false);
        handler.enable();
    }

    ;

    void move(float speedExp) {
        orbitAnimation += orbitSpeed * Math.pow(2.0, speedExp);
        for (Planet s : satellites) {
            s.move(speedExp);
        }
    }

    ;

    void rotate(float speedExp) {
        axisAnimation += 10.0 * Math.pow(2.0, speedExp);
    }

    void drawOrbitalTrail() {
        this.glut.glutWireTorus(0.001, distance, 6, 100);
    }

    ;

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
        TextureHandler handler = new TextureHandler(gl2, glugl, "/Users/macbook/git/ProiectGUI/ProiectGUI/texture/" + imageName + ".bmp", false);
        handler.bind();
        handler.enable();

        glu.gluQuadricTexture(quadric, true);
        glu.gluSphere(quadric, radius, 20, 20);

        handler.disable();
        ((GLMatrixFunc) gl).glPopMatrix();
        if (name == "Saturn") {
            // Draw Saturn's rings
            ((GLMatrixFunc) gl).glPushMatrix();
            gl2.glColor3ub((byte) 158, (byte) 145, (byte) 137);
            ((GLMatrixFunc) gl).glRotatef(-63.0f, 1.0f, 0.0f, 0.0f);
            glut.glutWireTorus(0.15, 4.0, 3, 60);
            glut.glutWireTorus(0.3, 3.25, 3, 60);
            ((GLMatrixFunc) gl).glPopMatrix();
        }
        if (showSatellitesOrbit == 1) {
            for (Planet s : satellites) {
                s.drawSmallOrbit();
            }
        }
        if (showSatellites == 1) {
            for (Planet s : satellites) {
                s.drawMoon(quadric);
            }
        }


        ((GLMatrixFunc) gl).glPopMatrix();
    }

    ;




}



