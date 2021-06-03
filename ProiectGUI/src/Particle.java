import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.gl2.GLUgl2;

import java.util.Random;

public class Particle {
    double positionX, positionY, positionZ;
    double speedX, speedY, speedZ;
    double accelerationX, accelerationY, accelerationZ;
    double radius = 2;

    double life;
    TextureHandler texture = null;
    Random rand = null;

    public Particle(GL2 gl, GLUgl2 glu, String textName) {
        this.texture = new TextureHandler(gl, glu, textName, false);
        rand = new Random();
        this.init();
    }

    private void init() {
        this.positionX = -rand.nextDouble() *rand.nextDouble()  * 10;
        this.positionY = rand.nextDouble() / 100 * 10;
        this.positionZ = rand.nextDouble() / 100 * 10;

        this.accelerationX = rand.nextDouble();
        this.accelerationY = rand.nextDouble();
        this.accelerationZ = rand.nextDouble();

        this.speedX = 0;
        this.speedY = 0;
        this.speedZ = 0;

        this.life = 10;
    }

    public void update() {
        // Consider time equal to the unit (1).

        // speed = acceleration * time
        this.speedX += this.accelerationX;
        this.speedY += this.accelerationY;
        this.speedZ += this.accelerationZ;

        // position = speed * time
        this.positionX += this.speedX;
        this.positionY -= this.speedY / 100;
        this.positionZ += this.speedZ;

        // Decrease life.
        this.life -= 0.5;

        // If the life of the particle has ended then reinitialize it.
        if (this.life < 0)
            this.init();
    }
    public void draw(GL2 gl)
    {

        this.texture.bind();
        this.texture.enable();

        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        gl.glBegin(GL2.GL_POLYGON);
        gl.glTexCoord2d(0,0);
        gl.glVertex3d(this.positionX - this.radius, this.positionY - this.radius, this.positionZ);
        gl.glTexCoord2d(1,0);
        gl.glVertex3d(this.positionX + this.radius /10, this.positionY - this.radius / 10, this.positionZ);
        gl.glTexCoord2d(1,1);
       gl.glVertex3d(this.positionX + this.radius / 100, this.positionY + this.radius / 100, this.positionZ);
//        gl.glTexCoord2d(0,1);
//        gl.glVertex3d(this.positionX - this.radius, this.positionY + this.radius, this.positionZ);
        gl.glEnd();

        this.texture.disable();
    }
}