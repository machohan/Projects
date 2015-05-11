import java.applet.Applet;
import java.awt.*;

public class Anim2b extends Applet {
   private Animation t, t1;
   private Image img;        // image buffer
   private Graphics gcopy;   // graphics tool for the image buffer
   public void init() {
     img = createImage(getWidth(), getHeight());
     gcopy = img.getGraphics();
   }
   
   public void start() {
     if (t == null) {
	t = new Animation(this, Color.red, 1, 50, 3);
	t.start();
     }
     if (t1 == null) {
	t1 = new Animation(this, Color.blue, 2, 60, 2);
	t1.start();
     }
   }

   public void stop() {
     if (t != null) {
	t.stop();
	t = null;
     }
	     
     if (t1 != null) {
	t1.stop();
	t1 = null;
     }
   }
	   
   // change update so that it calls paint without clearing surface
   // the default implementation of update() clears the background
   public void update(Graphics g) {
	paint(g);
   }

   public void paint(Graphics g) {
	gcopy.setColor(Color.white);
	gcopy.fillRect(0, 0, size().width, size().height);  
	// prepare the image buffer
	t.draw(gcopy);   // draw 1st object on the image offscreen buffer       
	t1.draw(gcopy);  // draw the 2nd object
	g.drawImage(img, 0, 0, this);  // copy the offscreen buffer to                                     // the applet display
  }
}

class Animation extends Thread {
	  private Applet appl;
	  private Color color;
	  private int increment;
	  private int which;
	  private int position = 0;
	  private int pause;
	  Animation(Applet a, Color c, int which, int p, int in) {
	     color = c;
	     appl = a;
	     pause = p;
	     increment = in;
	     this.which = which;
	  }

	  public void run() {
	     while (true) {
	       try {
	         Thread.sleep(pause);
	       } catch (InterruptedException e) {}
	       position += increment;
	       if (position > 100 || position < 0)
		  increment = -increment;
		  appl.repaint();
		}
	  }

	  public void draw(Graphics g) {
	    g.setColor(color);
	    if (which == 1)
	        g.fillOval(5, 5 + position, 30, 30);
	    else
	    	g.fillRect(40 + position, 25, 30, 30);
	 }
}