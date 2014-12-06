import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Metaball extends Panel
{
	static int w = 500, h = 200;
	static double thres = 1.0/2500;
	static double pos[][];
	static int colour[][];

	public static void main(String ...args)
	{
		pos = new double[3][2];
		colour = new int[3/*this is # of metaballs*/][3];

		colour[0][0] = colour[1][2] = colour[2][1] = 255;

		pos[0][1] = 100;
		pos[1][1] = 100;
		pos[2][1] = 100;

		JFrame f = new JFrame("Meatball Test"); //"meatball" is intentional
		Metaball m = new Metaball();
		m.setSize(w,h);
		f.add(m);
		f.getContentPane().setPreferredSize(new Dimension(w, h));
		f.pack();
		f.setVisible(true);

		try{
			for (int i = 0;; i++)
			{
				pos[0][0] = 250*(1-Math.cos(i/250.0*Math.PI));
				pos[1][0] = 250*(1+Math.cos(i/127.0*Math.PI));
				pos[2][0] = 250*(1-Math.sin(i/99.0*Math.PI));
				m.repaint();
				Thread.sleep(30);
			}
		} catch (Exception e) { }
	}


	FPSCount fp = new FPSCount();
	public void paint(Graphics gr)
	{
		update(gr);
	}
	public void update(Graphics gr)
	{
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		int colourpt[][] = new int[w][h];

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
			{
				double sum = 0, com, r = 0, g = 0, b = 0;

				for (int k = 0; k < pos.length; k++)
				{
					com = 1.0/(Math.pow(pos[k][0]-i, 2) + Math.pow(pos[k][1]-j, 2));
					sum += com;

					//colour mixing
					r += colour[k][0]*com;
					g += colour[k][1]*com;
					b += colour[k][2]*com;
				}
				r /= sum;
				g /= sum;
				b /= sum;

				if (sum < thres)
					colourpt[i][j] = 0xFF000000;
				else
					colourpt[i][j] = 0xFF000000 + (((int)r) << 16) + (((int)g) << 8) + (((int)b) << 0);
			} //close off for loop.

/*
				if (sum < thres)
					img.setRGB(i, j, 0xFF000000);
				else
					img.setRGB(i, j, 0xFF000000 + (((int)r) << 16) + (((int)g) << 8) + (((int)b) << 0));
			}
*/
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++)
				img.setRGB(i, j, colourpt[i][j]);
		gr.drawImage(img, 0, 0, null);
		fp.hit(gr);

	}
}

/*=======================================================================================*/
class FPSCount
{
	static final int time = 2000, maxn;
	static {
		maxn = time*10;
	}

	Queue q = new Queue(maxn);
	long sum = 0, len = 0;

	public void hit(Graphics g)
	{
		long t = System.currentTimeMillis(), mt = t-time, ot;
		while (!q.empty() && q.peek() < mt)
		{
			ot = q.remove();
			sum -= ot;
			len --;
		}
		q.insert(t);
		sum += t;
		len ++;

		g.setColor(Color.WHITE);
		g.drawString("fps@"+time+"ms: "+((int)(len*100000.0/(t-q.peek())))/100.0,0,10);
	}
}
class Queue //for debugging
{
	private static final int SIZE = 10;
	private long[] data = null;
	private int count = 0;

	//the data is from [head,tail). if it's empty or full, they will be equal, so check if any element is null
	//we disallow inserting nulls, so nulls mean an empty spot. therefore it will work

	private int head = 0, tail = 0;

	public Queue()
	{
		this(SIZE);
	}

	public Queue(int cap)
	{
		if (cap < SIZE)
			cap = SIZE;

		data = new long[cap];
	}

	public boolean empty()
	{
		return count == 0;
	}

	public long size() //number of objects
	{
		return count;
	}
	public void insert(long obj)
	{
		if (full())
			return;

		data[tail] = obj;
		tail = (tail+1) % data.length;
		count++;
	}
	public long remove()
	{
		long o = -1;

		if (!empty())
		{
			o = data[head];
			head = (head+1) % data.length;
			count--;
		}

		return o;
	}
	public long peek()
	{
		if (empty())
			return -1;
		return data[head];
	}
	private boolean full()
	{
		return head == tail && count != 0;
	}

}
