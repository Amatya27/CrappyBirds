package flappybird;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** All the drawing for the game. The game logic stays in FlappyBird. */
public final class Scene
{
	private static final Color SKY_TOP = new Color(0x4FC3F7);
	private static final Color SKY_BOTTOM = new Color(0xB3E5FC);
	private static final Color PIPE = new Color(0x43A047);
	private static final Color PIPE_DARK = new Color(0x2E7D32);
	private static final Color PIPE_LIGHT = new Color(0x81C784);
	private static final Color GROUND = new Color(0xDEB887);
	private static final Color GRASS = new Color(0x7CB342);
	private static final Color INK = new Color(0x1F2937);

	private Scene()
	{
	}

	public static void smooth(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}

	public static void background(Graphics2D g, int width, int height, int groundHeight, int ticks, boolean moving)
	{
		int skyBottom = height - groundHeight;
		g.setPaint(new GradientPaint(0, 0, SKY_TOP, 0, skyBottom, SKY_BOTTOM));
		g.fillRect(0, 0, width, skyBottom);

		int drift = moving ? ticks : 0;
		g.setColor(new Color(255, 255, 255, 200));
		for (int i = 0; i < 5; i++)
		{
			int x = Math.floorMod(i * 340 + 120 - drift, width + 300) - 150;
			int y = 70 + (i * 97) % 260;
			cloud(g, x, y, 1 + (i % 3) * 0.25);
		}

		g.setColor(new Color(0xA5D6A7));
		int hills = width / 210 + 3;
		for (int i = 0; i < hills; i++)
		{
			int x = Math.floorMod(i * 210 - drift * 2, hills * 210) - 210;
			g.fill(new Ellipse2D.Double(x, skyBottom - 70, 220, 140));
		}

		g.setColor(GROUND);
		g.fillRect(0, skyBottom, width, groundHeight);
		g.setColor(GRASS);
		g.fillRect(0, skyBottom, width, 18);
		g.setColor(PIPE_DARK);
		int offset = moving ? Math.floorMod(ticks * 10, 40) : 0;
		for (int x = -offset; x < width; x += 40)
		{
			g.fillRect(x, skyBottom + 18, 20, 6);
		}
	}

	private static void cloud(Graphics2D g, int x, int y, double s)
	{
		g.fill(new Ellipse2D.Double(x, y + 18 * s, 90 * s, 40 * s));
		g.fill(new Ellipse2D.Double(x + 25 * s, y, 60 * s, 55 * s));
		g.fill(new Ellipse2D.Double(x + 55 * s, y + 12 * s, 60 * s, 45 * s));
	}

	public static void pipe(Graphics2D g, Rectangle c)
	{
		g.setPaint(new GradientPaint(c.x, 0, PIPE_LIGHT, c.x + c.width, 0, PIPE_DARK));
		g.fillRect(c.x + 6, c.y, c.width - 12, c.height);

		boolean top = c.y == 0;
		int capY = top ? c.y + c.height - 30 : c.y;
		g.setPaint(new GradientPaint(c.x, 0, PIPE_LIGHT, c.x + c.width, 0, PIPE));
		g.fill(new RoundRectangle2D.Double(c.x, capY, c.width, 30, 8, 8));
		g.setColor(PIPE_DARK);
		g.setStroke(new BasicStroke(3));
		g.draw(new RoundRectangle2D.Double(c.x, capY, c.width, 30, 8, 8));
		g.drawLine(c.x + 6, top ? c.y : capY + 30, c.x + 6, top ? capY : c.y + c.height);
		g.drawLine(c.x + c.width - 6, top ? c.y : capY + 30, c.x + c.width - 6, top ? capY : c.y + c.height);
	}

	/** Draws the bird centred on its hitbox, tilted by how fast it is moving. */
	public static void bird(Graphics2D g, Rectangle b, int yMotion, int ticks, boolean alive)
	{
		AffineTransform saved = g.getTransform();
		double cx = b.getCenterX(), cy = b.getCenterY();
		double tilt = Math.max(-0.45, Math.min(1.2, yMotion * 0.07));
		g.translate(cx, cy);
		g.rotate(tilt);
		g.scale(1.5, 1.5);

		g.setColor(new Color(0, 0, 0, 40));
		g.fill(new Ellipse2D.Double(-15, -9, 32, 24));

		g.setColor(new Color(0xFFCA28));
		g.fill(new Ellipse2D.Double(-17, -12, 32, 24));
		g.setColor(new Color(0xFFF59D));
		g.fill(new Ellipse2D.Double(-10, 0, 18, 10));

		double flap = alive ? Math.sin(ticks * 0.9) * 5 : 0;
		g.setColor(new Color(0xFFB300));
		g.fill(new Ellipse2D.Double(-16, -4 + flap, 16, 10));
		g.setColor(new Color(0xE65100));
		g.setStroke(new BasicStroke(1.5f));
		g.draw(new Ellipse2D.Double(-16, -4 + flap, 16, 10));

		g.setColor(Color.WHITE);
		g.fill(new Ellipse2D.Double(3, -10, 11, 11));
		g.setColor(INK);
		g.fill(new Ellipse2D.Double(8, -7, 5, 5));

		Path2D beak = new Path2D.Double();
		beak.moveTo(12, -2);
		beak.lineTo(23, 1);
		beak.lineTo(12, 5);
		beak.closePath();
		g.setColor(new Color(0xFF7043));
		g.fill(beak);

		g.setTransform(saved);
	}

	public static void scorePill(Graphics2D g, int width, int score)
	{
		String text = String.valueOf(score);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
		FontMetrics fm = g.getFontMetrics();
		int w = Math.max(120, fm.stringWidth(text) + 70);
		int x = (width - w) / 2, y = 30;
		glass(g, x, y, w, 90, 45);
		g.setColor(Color.WHITE);
		g.drawString(text, (width - fm.stringWidth(text)) / 2, y + 68);
	}

	public static void startCard(Graphics2D g, int width, int height, int best)
	{
		int w = 640, h = best > 0 ? 260 : 220;
		int x = (width - w) / 2, y = height / 2 - h - 40;
		card(g, x, y, w, h);
		center(g, "Crappy Birds", width, y + 85, new Font(Font.SANS_SERIF, Font.BOLD, 60), INK);
		center(g, "Click or press space to flap", width, y + 145, new Font(Font.SANS_SERIF, Font.PLAIN, 28), new Color(0x4B5563));
		if (best > 0)
		{
			center(g, "Best: " + best, width, y + 205, new Font(Font.SANS_SERIF, Font.BOLD, 30), new Color(0xF59E0B));
		}
	}

	public static void gameOverCard(Graphics2D g, int width, int height, int score, int best)
	{
		int w = 560, h = 330;
		int x = (width - w) / 2, y = height / 2 - h / 2 - 60;
		card(g, x, y, w, h);
		center(g, "Game Over", width, y + 80, new Font(Font.SANS_SERIF, Font.BOLD, 56), new Color(0xDC2626));

		stat(g, x + 70, y + 115, "SCORE", score, INK);
		stat(g, x + w / 2 + 20, y + 115, "BEST", best, new Color(0xF59E0B));
		if (score > 0 && score >= best)
		{
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
			g.setColor(new Color(0xF59E0B));
			g.drawString("NEW BEST!", x + w / 2 + 20, y + 245);
		}
		center(g, "Click or press space to try again", width, y + 295, new Font(Font.SANS_SERIF, Font.PLAIN, 24), new Color(0x4B5563));
	}

	private static void stat(Graphics2D g, int x, int y, String label, int value, Color color)
	{
		g.setColor(new Color(0xF3F4F6));
		g.fill(new RoundRectangle2D.Double(x, y, 190, 110, 24, 24));
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		g.setColor(new Color(0x6B7280));
		g.drawString(label, x + 22, y + 34);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 52));
		g.setColor(color);
		g.drawString(String.valueOf(value), x + 22, y + 92);
	}

	private static void card(Graphics2D g, int x, int y, int w, int h)
	{
		g.setColor(new Color(0, 0, 0, 45));
		g.fill(new RoundRectangle2D.Double(x + 6, y + 10, w, h, 36, 36));
		g.setColor(new Color(255, 255, 255, 240));
		g.fill(new RoundRectangle2D.Double(x, y, w, h, 36, 36));
	}

	private static void glass(Graphics2D g, int x, int y, int w, int h, int r)
	{
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(new Color(0, 0, 0, 70));
		g.fill(new RoundRectangle2D.Double(x, y, w, h, r * 2, r * 2));
		g.setColor(new Color(255, 255, 255, 120));
		g.setStroke(new BasicStroke(2));
		g.draw(new RoundRectangle2D.Double(x, y, w, h, r * 2, r * 2));
	}

	private static void center(Graphics2D g, String s, int width, int y, Font font, Color color)
	{
		g.setFont(font);
		g.setColor(color);
		g.drawString(s, (width - g.getFontMetrics().stringWidth(s)) / 2, y);
	}
}
