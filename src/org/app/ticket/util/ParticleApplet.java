package org.app.ticket.util;
/*
 * Copyright Jerry Huxtable 1999
 *
 * Feel free to do anything you like with this code.
 */
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jhlabs.image.*;

/**
 * An applet which animates a simple particle system.
 */
public class ParticleApplet extends JPanel implements Runnable, MouseListener {
	
	
	  public static void main(String[] arg0){
		
		ParticleApplet pl = new ParticleApplet();
		
		JFrame f = new JFrame();
		f.setSize(new Dimension(1000,700));
		f.setVisible(true);
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(pl,BorderLayout.CENTER);
		pl.init();
		
	}
	
	
	protected MemoryImageSource source;
	protected Image image;
	protected boolean newImage = true;
	protected ColorModel colorModel;
	protected Thread thread;
	protected byte[] pixels1, pixels2;
	protected int width, height;
	protected Image offscreen;
	protected Graphics offscreenG;
	public Action[] actions;
	protected Filter[] filters;
	protected Object[] gradients;
	private boolean running = false;
	private boolean startAnimation = false;

	public ParticleApplet() {
	}
	
	public void init() {
		Dimension size = getSize();
		width = size.width;
		height = size.height;
		try {
			ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream("gradients.ser"));
			gradients = (Object[])ois.readObject();
			ois.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		addMouseListener(this);
		setGradient(3);
		int numFilters = 8;
		filters = new Filter[numFilters];
		filters[0] = new ShiftDownFilter();
		filters[1] = new ShiftUpFilter();
		filters[2] = new ConvolveFilter();
		filters[3] = new BlurHFilter();
		filters[4] = new BlurVFilter();
		filters[5] = new ThingFilter();
		filters[6] = new WaterFilter();
		filters[7] = new ClearFilter();
		filters[3].setEnabled(true);
		filters[4].setEnabled(true);

		int numActions = 1;
		actions = new Action[numActions];
		int numParticles = getIntParameter("numParticles", 100);
		Particles particles = new Particles(numParticles, width/2, height/2, width, height);
		actions[0] = particles;
		actions[0].setEnabled(true);
		particles.rate = getIntParameter("rate", 100);
		particles.speed = (getIntParameter("speed", 0) << 8) / 10;
		particles.angle = getIntParameter("angle", 0);
		particles.spread = getIntParameter("spread", 360);
		particles.gravity = getIntParameter("gravity", 0);
		particles.color = getIntParameter("color", 255);
		particles.scatter = getIntParameter("scatter", 0);
		particles.hscatter = getIntParameter("hscatter", 0);
		particles.vscatter = getIntParameter("vscatter", 0);
		particles.randomness = getIntParameter("randomness", 0);
		particles.size = getIntParameter("size", 2);
		particles.x = getIntParameter("x", width/2);
		particles.y = getIntParameter("y", height/2);
		particles.lifetime = getIntParameter("lifetime", 50);
		particles.speedVariation = (getIntParameter("speedVariation", 0) << 8) / 10;
		particles.decay = getIntParameter("decay", 0);
		startAnimation = getIntParameter("startAnimation", 1) != 0;
		int colormap = getIntParameter("colormap", 4);
		setGradient(colormap);
//		String actions = getParameter("actions");
//		if (actions != null) {
//			String s = "duchvtwc";
//			for (int i = 0; i < s.length(); i++)
//				filters[i].setEnabled(actions.indexOf(s.charAt(i)) != -1);
//		}
	}

	private int getIntParameter(String name, int defaultValue) {
		if (name != null) {
			try {
				//return Integer.parseInt(getParameter(name));
			}
			catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}
	
	public void setGradient(int n) {
		n = Math.max(0, Math.min(n, gradients.length-1));
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];
		for (int i = 0; i < 256; i++) {
			int rgb = ((Gradient)gradients[n]).getColor((float) (i/255.0));
			r[i] = (byte)((rgb >> 16) & 0xff);
			g[i] = (byte)((rgb >> 8) & 0xff);
			b[i] = (byte)(rgb & 0xff);
		}
		colorModel = new IndexColorModel(8, 256, r, g, b);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	public void start() {
		if (startAnimation && thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	public void stop() {
		if (thread != null) {
			thread.stop();
			thread = null;
		}
	}
	
	public void run() {
		try {
			running = true;
			while (running) {
				repaintImage();
//				synchronized(this) {
//					wait();
//				}
				Thread.sleep(5);
			}
		}
		catch (InterruptedException e) {
		}
		thread = null;
	}
	
	public void update(Graphics g) {
		paint(g);
	}

	public synchronized void paint(Graphics g) {
		Dimension size = getSize();
		int w = size.width;
		int h = size.height;
		if (newImage || image == null)
			image = makeImage(w, h);
		g.drawImage(image, 0, 0, this);
		if (!running) {
			String s = "Click to Start";
			FontMetrics fm = g.getFontMetrics();
			int x = (size.width-fm.stringWidth(s))/2;
			int y = (size.height-fm.getAscent())/2;
			g.setColor(Color.white);
			g.drawString(s, x, y);
		}
		notify();
	}

	private Image makeImage(int w, int h) {
		if (pixels1 == null) {
			int i = 0;
			pixels1 = new byte[width*height];
			pixels2 = new byte[width*height];
			for (i = 0; i < actions.length; i++)
				if (actions[i].isEnabled())
					actions[i].apply(pixels1, width, height);
		} else {
			for (int i = 0; i < filters.length; i++) {
				if (filters[i].isEnabled()) {
					filters[i].apply(pixels1, pixels2, width, height);
					byte[] t = pixels1;
					pixels1 = pixels2;
					pixels2 = t;
				}
			}
			for (int i = 0; i < actions.length; i++)
				if (actions[i].isEnabled())
					actions[i].apply(pixels1, width, height);
		}
		newImage = false;
		if (image == null) {
			image = createImage(source = new MemoryImageSource(w, h, colorModel, pixels1, 0, w));
			source.setAnimated(true);
		} else
			source.newPixels(pixels1, colorModel, 0, w);
		return image;
	}
	
	private void repaintImage() {
		newImage = true;
		repaint();
	}
	
	public void mousePressed(MouseEvent e) {
		requestFocus();
		startAnimation = true;
		if (thread == null && !running)
			start();
		else
			running = false;
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
}

class Thing {
	private boolean enabled;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}
}

class Action extends Thing {
	public void apply(byte[] pixels, int width, int height) {
	}
}

class Filter extends Action {
	public void apply(byte[] in, byte[] out, int width, int height) {
	}
}

/*
class TextAction extends Action {
	private Font font = new Font("sansserif", Font.PLAIN, 72);
	
	public void apply(byte[] pixels, int width, int height) {
	}
}

class ImageAction extends Action {
	private Image image;
	
	public ImageAction(Image image) {
		this.image = image;
	}
	
	public void apply(byte[] pixels, int width, int height) {
	}
}
*/

/**
 * A particle system. We use fixed point integer maths with 8 fractional bits everywhere
 * for speed, hence the frequent shifts by 8.
 */
class Particles extends Action {
	public Particle[] particles;
	public int rate = 100;
	public int angle = 0;	// 0 degrees is north
	public int spread = 90;
	public int gravity = (1 << 8);
	public int lifetime = 65;
	public int scatter = 0;
	public int hscatter = 0;
	public int vscatter = 0;
	public int x;
	public int y;
	public int speed = 1;
	public int size;
	public int width;
	public int height;
	public int speedVariation = 0;
	public int decay = 0;
	public int randomness = (7 << 8);
	public int color;
	public int numParticles;
	private static int[] sinTable, cosTable;

	static {
		sinTable = new int[360];
		cosTable = new int[360];
		for (int i = 0; i < 360; i++) {
			double angle = 2*Math.PI*i/360;
			sinTable[i] = (int)(256 * Math.sin(angle));
			cosTable[i] = (int)(256 * Math.cos(angle));
		}
	}

	public Particles(int numParticles, int x, int y, int width, int height) {
		this.numParticles = numParticles;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		particles = new Particle[numParticles];
		for (int i = 0; i < numParticles; i++) {
			particles[i] = new Particle(this, width, height);
			newParticle(particles[i]);
		}
	}

	public double gaussian() {
		double sum = 0;
		for (int i = 0; i < 12; i++) {
			sum += Math.random();
		}
		return (sum-6)/3.0;
	}
	
	public void newParticle(Particle particle) {
		particle.color = color;
		particle.size = size;
		particle.lifetime = (int)(Math.random()*lifetime);
		particle.randomness = randomness;
		particle.x = x;
		particle.y = y;
		if (scatter != 0) {
			int a = ((int)(Math.random() * 360)) % 360;
			double distance = scatter * Math.random() / 256;
			particle.x += (int)(cosTable[a] * distance);
			particle.y += (int)(-sinTable[a] * distance);
		}
		if (hscatter != 0)
			particle.x += (int)(hscatter * (Math.random()-0.5));
		if (vscatter != 0)
			particle.y += (int)(vscatter * (Math.random()-0.5));
		int a = (angle + 450 - spread/2 + (int)(Math.random() * spread)) % 360;
		int s = speed + (int)(speedVariation * gaussian());
		particle.vx = ((cosTable[a] * s) >> 8);
		particle.vy = -((sinTable[a] * s) >> 8);

		particle.x <<= 8;
		particle.y <<= 8;
	}
	
	public void apply(byte[] pixels, int width, int height) {
		for (int i = 0; i < particles.length; i++) {
			Particle p = particles[i];
			if (p.lifetime < 0) {
				newParticle(p);
			}
			p.paint(pixels, width, height);
			p.move(width, height);
			p.color -= decay;
			if (p.color < 0)
				p.color = 0;
		}
	}

	public String toString() {
		return "Particles";
	}
}

class Particle {
	protected int x, y;
	protected int vx, vy;
	public int size;
	public int color = 255;
	public int randomness = 0;
	public int lifetime = -1;
	private Particles particles;
	
	public Particle(Particles particles, int width, int height) {
		this.particles = particles;
	}
	
	public void move(int width, int height) {
		if (randomness != 0) {
			vx += (int)(Math.random() * randomness)-randomness/2;
			vy += (int)(Math.random() * randomness)-randomness/2;
		}
		x += vx;
		y += vy;
		vy += particles.gravity;
		lifetime--;
	}
	
	/*
	 * How to draw circles of small sizes
	 */
	public int[] circle1 = { 0, 1 };
	public int[] circle3 = { 0, 1, -1, 3, 0, 1 };
	public int[] circle5 = { -1, 3, -2, 5, -2, 5, -2, 5, -1, 3 };
	public int[] circle7 = { -1, 3, -2, 5, -3, 7, -3, 7, -3, 7, -2, 5, -1, 3 };
	public int[][] circles = { circle1, circle3, circle5, circle7 };
	
	public void paint(byte[] pixels, int width, int height) {
		byte pixel = (byte)color;
		int[] c = circles[Math.min(size, circles.length)];
		int my = (y >> 8)-size;
		for (int i = 0; i < c.length; i += 2, my++) {
			if (my < 0)
				continue;
			else if (my >= height)
				break;
			int x1 = Math.max(0, (x >> 8)+c[i]);
			int x2 = Math.min(width-1, x1+c[i+1]);
			int j = my*width+x1;
			for (int mx = x1; mx <= x2; mx++)
				pixels[j++] = pixel;
		}
	}
}

/**
 * Scroll the image down
 */
class ShiftDownFilter extends Filter {
	public void apply(byte[] in, byte[] out, int width, int height) {
		int i = 0;
		int j = 0;
		for (int x = 0; x < width; x++)
			out[j++] = 0;
		for (int y = 1; y < height; y++) {
			for (int x = 0; x < width; x++) {
				out[j++] = in[i++];
			}
		}
	}

	public String toString() {
		return "Move Down";
	}
}

/**
 * Scroll the image up
 */
class ShiftUpFilter extends Filter {
	public void apply(byte[] in, byte[] out, int width, int height) {
		int i = width*height-1;
		int j = i;
		for (int x = 0; x < width; x++)
			out[j--] = 0;
		for (int y = 1; y < height; y++) {
			for (int x = 0; x < width; x++) {
				out[j--] = in[i--];
			}
		}
	}

	public String toString() {
		return "Move Up";
	}
}

/**
 * Zoom the image out to the left and right
 */
class ZoomInVFilter extends Filter {
	public void apply(byte[] in, byte[] out, int width, int height) {
		int i = 0;
		int j = 0;
		int height2 = height/2;
		for (int x = 0; x < width; x++)
			out[j++] = 0;
		j = width;
		for (int y = 1; y < height2; y++) {
			for (int x = 0; x < width; x++) {
				out[j++] = in[i++];
			}
		}
		i += 2*width;
		for (int y = 1; y < height2; y++) {
			for (int x = 0; x < width; x++) {
				out[j++] = in[i++];
			}
		}
		for (int x = 0; x < width; x++)
			out[j++] = 0;
	}

	public String toString() {
		return "Move In Vertical";
	}
}

/**
 * Zoom the image out sideways
 */
class ZoomInHFilter extends Filter {
	public void apply(byte[] in, byte[] out, int width, int height) {
		int i = 0;
		int j = 0;
		int height2 = height/2;
		for (int x = 0; x < width; x++)
			out[j++] = 0;
		j = width;
		for (int y = 1; y < height2; y++) {
			for (int x = 0; x < width; x++) {
				out[j++] = in[i++];
			}
		}
		i += 2*width;
		for (int y = 1; y < height2; y++) {
			for (int x = 0; x < width; x++) {
				out[j++] = in[i++];
			}
		}
		for (int x = 0; x < width; x++)
			out[j++] = 0;
	}

	public String toString() {
		return "Move In Horizontal";
	}
}

/**
 * Do a general convolution on the image (this is much slower than the blur filters)
 */
class ConvolveFilter extends Filter {
	protected int[] kernel = {
		-3, 0, 0, 0, -3,
		0, 0, 0, 0, 0,
		-3, 0, 50, 0, -3,
		0, 0, 0, 0, 0,
		-3, 0, 0, 0, -3,
	};
	int target = 40;

	public ConvolveFilter() {
	}
	
	public ConvolveFilter(int[] kernel, int target) {
		this.kernel = kernel;
		this.target = target;
	}
	
	public void apply(byte[] in, byte[] out, int width, int height) {
		int index = 0;
		int rows = 5;
		int cols = 5;
		int rows2 = rows/2;
		int cols2 = cols/2;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int t = 0;

				for (int row = -rows2; row <= rows2; row++) {
					int iy = y+row;
					int ioffset;
					if (0 <= iy && iy < height)
						ioffset = iy*width;
					else
						ioffset = y*width;
					int moffset = cols*(row+rows2)+cols2;
					for (int col = -cols2; col <= cols2; col++) {
						int f = kernel[moffset+col];

						if (f != 0) {
							int ix = x+col;
							if (!(0 <= ix && ix < width))
								ix = x;
							t += f * (in[ioffset+ix] & 0xff);
						}
					}
				}
				t /= target;
				if (t > 255)
					t = 255;
				out[index++] = (byte)t;
			}
		}
	}

	public String toString() {
		return "Convolve";
	}
}

/**
 * Blur horizontally
 */
class BlurHFilter extends Filter {
	public void apply(byte[] in, byte[] out, int width, int height) {
		for (int y = 0; y < height; y++) {
			int index = y*width;
			out[index] = (byte)(((in[index] & 0xff) + (in[index+1] & 0xff))/3);
			index++;
			for (int x = 1; x < width-1; x++) {
				out[index] = (byte)(((in[index-1] & 0xff) + (in[index] & 0xff) + (in[index+1] & 0xff))/3);
				index++;
			}
			out[index] = (byte)(((in[index-1] & 0xff) + (in[index] & 0xff))/3);
		}
	}

	public String toString() {
		return "Blur Horizontally";
	}
}

/**
 * Blur vertically
 */
class BlurVFilter extends Filter {
	public void apply(byte[] in, byte[] out, int width, int height) {
		for (int x = 0; x < width; x++) {
			int index = x;
			out[index] = (byte)(((in[index] & 0xff) + (in[index+width] & 0xff))/3);
			index += width;
			for (int y = 1; y < height-1; y++) {
				out[index] = (byte)(((in[index-width] & 0xff) + (in[index] & 0xff) + (in[index+width] & 0xff))/3);
				index += width;
			}
			out[index] = (byte)(((in[index-width] & 0xff) + (in[index] & 0xff))/3);
		}
	}

	public String toString() {
		return "Blur Vertically";
	}
}

/**
 * A sort of water-ripple type effect
 */
class WaterFilter extends Filter {
	public void apply(byte[] in, byte[] out, int width, int height) {
		for (int y = 1; y < height-1; y++) {
			int index = y*width;
			index++;
			for (int x = 1; x < width-1; x++) {
				int n = (byte)(
					((in[index-1] & 0xff) + (in[index+1] & 0xff) + (in[index-width] & 0xff) + (in[index+width] & 0xff))/2
					 - (out[index] & 0xff));
				n -= (byte)(n >> 6);
				out[index] = (byte)ImageMath.clamp(n, 0, 255);
				index++;
			}
			index++;
		}
	}

	public String toString() {
		return "Ripple";
	}
}

/**
 * Clears the image to black - you only see the moving particles with this one.
 */
class ClearFilter extends Filter {
	public void apply(byte[] in, byte[] out, int width, int height) {
		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				out[index] = 0;
				index++;
			}
		}
	}

	public String toString() {
		return "Clear";
	}
}

class ThingFilter extends ConvolveFilter {
/*
	protected static int[] kernel = {
		10, 0, 10, 0, 10,
		0, 0, 0, 0, 0,
		0, 10, 0, 10, 0,
		0, 0, 0, 0, 0,
		10, 0, 10, 0, 10,
	};
*/
	protected static int[] kernel = {
		10, 0, 0, 0, 10,
		0, 10, 0, 10, 0,
		0, 0, 10, 0, 0,
		0, 10, 0, 10, 0,
		10, 0, 0, 0, 10,
	};
	
	public ThingFilter() {
		super(kernel, 120);
	}

	public String toString() {
		return "Thing";
	}
	
	

}
