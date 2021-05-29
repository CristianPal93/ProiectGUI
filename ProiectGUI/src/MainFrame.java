

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.Animator;

public class MainFrame
        extends JFrame
        implements GLEventListener,KeyListener,
		MouseListener,
		MouseMotionListener,
		MouseWheelListener{

    /**
     *
     */
	
	public static String devPath = "/Users/macbook/git/ProiectGUI/ProiectGUI/texture/"; //cristi
	
//	public static String devPath = "add your path here!"; //Mara

    int isAnimate = 1;
    int showPlanetOrbits = 1;
    int showSatelliteOrbit = 1;
    int showSatellites = 1;
    int showLabels = 1;
    int cameraPosition = 3;
    int zoom = 50;
    float speedExp = 0.0f;
    Planet sun;
    List<Planet> planets;

    private static final long serialVersionUID = 1L;
    private GLCanvas canvas;
    private Animator animator;
    private GLU glu;
    private GL2 gl;
    private GL test;
    //	GLU glut = new GLU();
    // For specifying the positions of the clipping planes (increase/decrease the distance) modify this variable.
    // It is used by the glOrtho method.
    private double v_size = 1.0;
    float cameraAzimuth = 0.0f, cameraSpeed = 0.0f, cameraElevation = 0.0f;

	float cameraCoordsPosx = 0.0f, cameraCoordsPosy = 0.0f, cameraCoordsPosz = -20.0f;

	// Set camera orientation
	float cameraUpx = 0.0f, cameraUpy = 1.0f, cameraUpz = 0.0f;
	private boolean[] keys = new boolean[250];
	float eyeX=0;
	float eyeY=0;



    TextureHandler starsTexture;


    public void setup() {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        GLUgl2 glugl = new GLUgl2();
        starsTexture = new TextureHandler(gl, glugl, devPath+"stars.bmp", false);
        gl.glEnable(GL2.GL_LIGHTING);
        float lightPos[] = {0.0f, 0.0f, -45.0f, 1.0f};
        float spotExponent = 1.0f;
        float lightAmb[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float lightDifAndSpec[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float globAmb[] = {0.4f, 0.4f, 0.4f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmb, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDifAndSpec, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightDifAndSpec, 0);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb, 0);
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
        gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_EXPONENT, spotExponent);
    }

    public void orbitalTrails() {
        ((GLMatrixFunc) gl).glPushMatrix();
        gl.glColor3ub((byte) 255, (byte) 255, (byte) 255);
        ((GLMatrixFunc) gl).glTranslatef(0.0f, 0.0f, 0.0f);
        ((GLMatrixFunc) gl).glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        for (Planet p : planets) {
            p.drawOrbitalTrail();
        }
        ((GLMatrixFunc) gl).glPopMatrix();
    }


    // Application main entry point
    public static void main(String args[]) {
        new MainFrame();

    }

    // Default constructor
    public MainFrame() {
        super("Java OpenGL");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(10000, 600);

        this.initializeJogl();

        this.setVisible(true);

    }

    private void initializeJogl() {
        // Creating a new GL profile.
        GLProfile glprofile = GLProfile.getDefault();
        // Creating an object to manipulate OpenGL parameters.
        GLCapabilities capabilities = new GLCapabilities(glprofile);

        // Setting some OpenGL parameters.
        capabilities.setHardwareAccelerated(true);
        capabilities.setDoubleBuffered(true);

        // Try to enable 2x anti aliasing. It should be supported on most hardware.
        capabilities.setNumSamples(2);
        capabilities.setSampleBuffers(true);

        // Creating an OpenGL display widget -- canvas.
        this.canvas = new GLCanvas(capabilities);

        // Adding the canvas in the center of the frame.
        this.getContentPane().add(this.canvas);

        // Adding an OpenGL event listener to the canvas.
        this.canvas.addGLEventListener(this);
        if (this.canvas.getGL() != null) {
            System.out.println("Canvas not null!");
        }
        // Creating an animator that will redraw the scene 40 times per second.
        this.animator = new Animator(this.canvas);

        this.canvas.addKeyListener(this);
		this.canvas.addMouseListener(this);
		this.canvas.addMouseMotionListener(this);
		this.canvas.addMouseWheelListener(this);
        // Starting the animator.
        this.animator.start();

        this.glu = new GLU();

    }

    public void init(GLAutoDrawable canvas) {
        // Obtaining the GL instance associated with the canvas.
        this.gl = canvas.getGL().getGL2();
        // Setting the clear color -- the color which will be used to erase the canvas.
        gl.glClearColor(0, 0, 0, 0);

        // Selecting the modelview matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//
        this.sun = new Planet(3.0f, 0f, 0f, 0f, "Sun", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu);
        this.planets = new ArrayList<Planet>(
                Arrays.asList(
                        new Planet(0.7f, 4.5f, 8.5f, 2.11f, "Mercury", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu),
                        new Planet(1.0f, 8.0f, 5.5f, 177.0f, "Venus", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu),
                        new Planet(1.0f, 12.0f, 3.8f, 23.44f, "Earth", new ArrayList<Planet>(
                                Arrays.asList(
                                        new Planet(0.4f, 2.0f, 5.40f, 0f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu)
                                )
                        ), canvas.getGL(), this.gl, this.glu),
                        new Planet(0.8f, 16.5f, 2.41f, 25.00f, "Mars", new ArrayList<Planet>(
                                Arrays.asList(
                                        new Planet(0.2f, 1.4f, 2.30f, 0f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu),
                                        new Planet(0.16f, 1.7f, 3.60f, 0f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu)
                               )
                                  ), canvas.getGL(), this.gl, this.glu),
                        new Planet(2.0f, 26.0f, 1.31f, 3.13f, "Jupiter", new ArrayList<Planet>(
                                Arrays.asList(
                                        new Planet(0.45f, 3.2f, 4.40f, 0f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu),
                                        new Planet(0.3f, 4.4f, 5.00f, 0f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu)
                                )
                        ), canvas.getGL(), this.gl, this.glu),
                        new Planet(1.8f, 35.0f, 0.97f, 26.70f, "Saturn", new ArrayList<Planet>(
                                Arrays.asList(
                                        new Planet(0.3f, 3.0f, 2.40f, 0f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu)
                                )
                        ), canvas.getGL(), this.gl, this.glu),
                        new Planet(1.3f, 42.5f, 0.68f, 97.77f, "Uranus", new ArrayList<Planet>(
                                Arrays.asList(
                                        new Planet(0.25f, 2.0f, 7.00f, 97.77f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu)
                                )
                        ), canvas.getGL(), this.gl, this.glu),
                        new Planet(1.3f, 50.2f, 0.54f, 28.32f, "Neptune", new ArrayList<Planet>(
                                Arrays.asList(
                                        new Planet(0.2f, 2.0f, 3.40f, 0f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu)
                                )
                        ), canvas.getGL(), this.gl, this.glu),
                        new Planet(0.4f, 56.5f, 0.47f, 119.6f, "Pluto", new ArrayList<Planet>(
                                Arrays.asList(
                                        new Planet(0.2f, 1.5f, 5.00f, 0f, "", new ArrayList<Planet>(), canvas.getGL(), this.gl, this.glu)
                                )
                        ), canvas.getGL(), this.gl, this.glu)
                ));

        setup();
    }

    public void display(GLAutoDrawable canvas) {

        GL2 gl = canvas.getGL().getGL2();

        // Erasing the canvas -- filling it with the clear color.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
        setCamera(gl, zoom);
        aimCamera(gl, this.glu);
		moveCamera();

        // Add your scene code here

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        ((GLMatrixFunc) gl).glPushMatrix();
//        switch (cameraPosition) {
//            case 0:
//                this.glu.gluLookAt(0.0, zoom, 50.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
//                break;
//            case 1:
//                this.glu.gluLookAt(0.0, 0.0, zoom, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
//                break;
//            case 2:
//                this.glu.gluLookAt(0.0, zoom, 0.00001, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
//                break;
//        }
//
        if (showPlanetOrbits == 1) {
            orbitalTrails();
        }
        GLUquadric quadric = glu.gluNewQuadric();
        sun.draw(showLabels, showSatelliteOrbit, showSatellites, quadric);
        for (Planet p : planets) {
            p.draw(showLabels, showSatelliteOrbit, showSatellites, quadric);
        }
        this.starsTexture.enable();
        this.starsTexture.bind();
        gl.glBegin(GL2.GL_POLYGON);
        gl.glTexCoord2f(-1.0f, 0.0f);
        gl.glVertex3f(-200f, -200f, -100f);
        gl.glTexCoord2f(2.0f, 0.0f);
        gl.glVertex3f(200f, -200f, -100f);
        gl.glTexCoord2f(2.0f, 2.0f);
        gl.glVertex3f(200f, 200f, -100f);
        gl.glTexCoord2f(-1.0f, 2.0f);
        gl.glVertex3f(-200f, 200f, -100f);
        gl.glEnd();

        this.starsTexture.bind();
        gl.glBegin(GL2.GL_POLYGON);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-200f, -83f, 200f);
        gl.glTexCoord2f(8.0f, 0.0f);
        gl.glVertex3f(200f, -83f, 200f);
        gl.glTexCoord2f(8.0f, 8.0f);
        gl.glVertex3f(200f, -83f, -200f);
        gl.glTexCoord2f(0.0f, 8.0f);
        gl.glVertex3f(-200f, -83f, -200f);
        gl.glEnd();
        this.starsTexture.disable();
        ((GLMatrixFunc) gl).glPopMatrix();
//		

        if (isAnimate == 1) {
            sun.rotate(speedExp);
            for (Planet p : planets) {
                p.move(speedExp);
                p.rotate(speedExp);
            }
        }

        // Forcing the scene to be rendered.
        gl.glFlush();
    }


    public void reshape(GLAutoDrawable canvas, int left, int top, int width, int height) {

        GL2 gl = canvas.getGL().getGL2(); // change this as needed
        double dpiScalingFactor = ((Graphics2D) getGraphics()).getTransform().getScaleX();
        width = (int) (width * dpiScalingFactor);
        height = (int) (height * dpiScalingFactor);
        gl.glViewport(0, 0, width, height);
        // then put the rest of your code here...
        // Determining the width to height ratio of the widget.
       double ratio = (double) width / (double) height;

        // Selecting the projection matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);

        gl.glLoadIdentity();

        // Selecting the view volume to be x from 0 to 1, y from 0 to 1, z from -1 to 1.
        // But we are careful to keep the aspect ratio and enlarging the width or the height.
        if (ratio < 1)
            gl.glOrtho(-v_size, v_size, -v_size, v_size / ratio, -5, 5);
        else
            gl.glOrtho(-v_size, v_size * ratio, -v_size, v_size, -5, 5);

        // Selecting the modelview matrix.
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

    }

    public void displayChanged(GLAutoDrawable canvas, boolean modeChanged, boolean deviceChanged) {
        return;
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        // TODO Auto-generated method stub
    }

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println(event.getKeyCode());
		
		if (event.getKeyCode() == KeyEvent.VK_UP) {
			cameraElevation -= 1f;
//			zoom-=1f;
//			System.out.println("Hello");
			eyeY+=1f;
		}

		if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			cameraElevation += 1f;
//			zoom+=1f;
			eyeY-=1f;


		}

		if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
			cameraAzimuth -= 2;
			eyeX+=1f;

		}

		if (event.getKeyCode() == KeyEvent.VK_LEFT) {
			cameraAzimuth += 2;
			eyeX-=1f;

		}

		if (event.getKeyCode() == KeyEvent.VK_I) {
			cameraSpeed += 0.05;
		}

		if (event.getKeyCode() == KeyEvent.VK_O) {
			cameraSpeed -= 0.05;
		}

		if (event.getKeyCode() == KeyEvent.VK_S) {
			cameraSpeed = 0;
		}
		if (event.getKeyCode() < 250)
			keys[event.getKeyCode()] = true;

		if (cameraAzimuth > 359)
			cameraAzimuth = 1;

		if (cameraAzimuth < 1)
			cameraAzimuth = 359;
	}
		
	

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void setCamera(GL2 gl, float distance) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		float widthHeightRatio = (float) getWidth() / (float) getHeight();
		glu.gluPerspective(45, widthHeightRatio, 1, 1000);
//		glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1f, 0f);
//		System.out.println(cameraCoordsPosx+" "+cameraCoordsPosy+" "+cameraCoordsPosz+" "+cameraUpx+" "+cameraUpy+" "+cameraUpz);
		
		glu.gluLookAt(eyeX, eyeY, distance, cameraCoordsPosx,
				cameraCoordsPosy, cameraCoordsPosz, cameraUpx, cameraUpy, cameraUpz);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		
	}
	private float[] polarToCartesian(float azimuth, float length, float altitude) {
		float[] result = new float[3];
		float x, y, z;

		// Do x-z calculation
		float theta = (float) Math.toRadians(90 - azimuth);
		float tantheta = (float) Math.tan(theta);
		float radian_alt = (float) Math.toRadians(altitude);
		float cospsi = (float) Math.cos(radian_alt);

		x = (float) Math.sqrt((length * length) / (tantheta * tantheta + 1));
		z = tantheta * x;

		x = -x;

		if ((azimuth >= 180.0 && azimuth <= 360.0) || azimuth == 0.0f) {
			x = -x;
			z = -z;
		}

		// Calculate y, and adjust x and z
		y = (float) (Math.sqrt(z * z + x * x) * Math.sin(radian_alt));

		if (length < 0) {
			x = -x;
			z = -z;
			y = -y;
		}

		x = x * cospsi;
		z = z * cospsi;

		result[0] = x;
		result[1] = y;
		result[2] = z;

		return result;
	}
	public void moveCamera() {
		float[] tmp = polarToCartesian(cameraAzimuth, cameraSpeed, cameraElevation);
		cameraCoordsPosx += tmp[0];
		cameraCoordsPosy += tmp[1];
		cameraCoordsPosz += tmp[2];
		
	}

	public void aimCamera(GL2 gl, GLU glu) {
		gl.glLoadIdentity();

//		float[] tmp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation);

		float[] camUp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation + 90);

		cameraUpx = camUp[0];
		cameraUpy = camUp[1];
		cameraUpz = camUp[2];
//
//		glu.gluLookAt(cameraCoordsPosx, cameraCoordsPosy, zoom, cameraCoordsPosx + tmp[0],
//				cameraCoordsPosy + tmp[1], cameraCoordsPosz + tmp[2], cameraUpx, cameraUpy, cameraUpz);
////		System.out.println(cameraCoordsPosx+" "+cameraCoordsPosy+" "+cameraCoordsPosz+" "+cameraUpx+" "+cameraUpy+" "+cameraUpz);
//		gl.glMatrixMode(GL2.GL_MODELVIEW);
//		gl.glLoadIdentity();
		

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getWheelRotation());
		if(e.getWheelRotation()<0 && zoom < 100) {
			zoom++;
		}
		if(e.getWheelRotation()>0 && zoom > -75 ) {
			zoom--;
		}
		
		
	}
}