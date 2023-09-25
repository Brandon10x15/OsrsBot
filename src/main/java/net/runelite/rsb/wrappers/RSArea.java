package net.runelite.rsb.wrappers;

import net.runelite.api.coords.WorldPoint;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.common.Positionable;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a shape made of RSTiles.
 *
 * @author GigiaJ
 */
public class RSArea {
    private final TileArea area;
    private final int plane;
    private final MethodContext ctx;

    /**
     * @param tiles An Array containing of <b>RSTiles</b> forming a polygon shape.
     * @param plane The plane of the <b>RSArea</b>.
     */
    public RSArea(MethodContext ctx, RSTile[] tiles, int plane) {
        this.ctx = ctx;
        this.area = tileArrayToPolygon(tiles);
        this.plane = plane;
    }

    /**
     * @param tiles An Array containing of <b>RSTiles</b> forming a polygon shape.
     */
    public RSArea(MethodContext ctx, RSTile[] tiles) {
        this(ctx, tiles, 0);
    }

    /**
     * @param sw    The <i>South West</i> <b>RSTile</b> of the <b>RSArea</b>
     * @param ne    The <i>North East</i> <b>RSTile</b> of the <b>RSArea</b>
     * @param plane The plane of the <b>RSArea</b>.
     */
    public RSArea(MethodContext ctx, RSTile sw, RSTile ne, int plane) {
        this(ctx, new RSTile[]{sw, new RSTile(ctx, ne.getWorldLocation().getX() + 1, sw.getWorldLocation().getY(), plane),
                new RSTile(ctx, ne.getWorldLocation().getX() + 1, ne.getWorldLocation().getY() + 1, plane),
                new RSTile(ctx, sw.getWorldLocation().getX(), ne.getWorldLocation().getY() + 1, plane)}, plane);
    }

    /**
     * @param sw The <i>South West</i> <b>RSTile</b> of the <b>RSArea</b>
     * @param ne The <i>North East</i> <b>RSTile</b> of the <b>RSArea</b>
     */
    public RSArea(MethodContext ctx, RSTile sw, RSTile ne) {
        this(ctx, sw, ne, 0);
    }

    /**
     * @param swX The X axle of the <i>South West</i> <b>RSTile</b> of the
     *            <b>RSArea</b>
     * @param swY The Y axle of the <i>South West</i> <b>RSTile</b> of the
     *            <b>RSArea</b>
     * @param neX The X axle of the <i>North East</i> <b>RSTile</b> of the
     *            <b>RSArea</b>
     * @param neY The Y axle of the <i>North East</i> <b>RSTile</b> of the
     *            <b>RSArea</b>
     */
    public RSArea(MethodContext ctx, int swX, int swY, int neX, int neY) {
        this(ctx, new RSTile(ctx, swX, swY), new RSTile(ctx, neX, neY), 0);
    }

    /**
     * @param swX The X axle of the <i>South West</i> <b>RSTile</b> of the
     *            <b>RSArea</b>
     * @param swY The Y axle of the <i>South West</i> <b>RSTile</b> of the
     *            <b>RSArea</b>
     * @param neX The X axle of the <i>North East</i> <b>RSTile</b> of the
     *            <b>RSArea</b>
     * @param neY The Y axle of the <i>North East</i> <b>RSTile</b> of the
     *            <b>RSArea</b>
     * @param p   The plane of the <b>RSArea</b>
     */
    public RSArea(MethodContext ctx, int swX, int swY, int neX, int neY, int p) {
        this(ctx, new RSTile(ctx, swX, swY), new RSTile(ctx, neX, neY), p);
    }

    /**
     * Creates an area with the given tile as the center and the sides being the given radius from the center tile
     *
     * @param positionable The tile to be the center of the area
     * @param radius       The radius of the area
     */
    public RSArea(MethodContext ctx, Positionable positionable, int radius) {
        this.ctx = ctx;
        this.plane = positionable.getLocation().getWorldLocation().getPlane();
        TileArea tileArea = new TileArea();
        tileArea.addPoint(positionable.getLocation().getWorldLocation().getX() - radius, positionable.getLocation().getWorldLocation().getY() + radius);
        tileArea.addPoint(positionable.getLocation().getWorldLocation().getX() + radius, positionable.getLocation().getWorldLocation().getY() + radius);
        tileArea.addPoint(positionable.getLocation().getWorldLocation().getX() + radius, positionable.getLocation().getWorldLocation().getY() - radius);
        tileArea.addPoint(positionable.getLocation().getWorldLocation().getX() - radius, positionable.getLocation().getWorldLocation().getY() - radius);
        area = tileArea;
	}

    public boolean contains(WorldPoint point) {
		return this.contains(new RSTile(ctx, point));
	}

	/**
	 * @param x The x location of the <b>RSTile</b> that will be checked.
	 * @param y The y location of the <b>RSTile</b> that will be checked.
	 * @return True if the <b>RSArea</b> contains the given <b>RSTile</b>.
	 */
	public boolean contains(int x, int y) {
		return this.contains(new RSTile(ctx, x, y));
	}

	/**
	 * @param plane The plane to check.
	 * @param tiles The <b>RSTile(s)</b> that will be checked.
	 * @return True if the <b>RSArea</b> contains the given <b>RSTile(s)</b>.
	 */
	public boolean contains(int plane, RSTile... tiles) {
		return this.plane == plane && this.contains(tiles);
	}

	/**
	 * @param tiles The <b>RSTile(s)</b> that will be checked.
	 * @return True if the <b>RSArea</b> contains the given <b>RSTile(s)</b>.
	 */
	public boolean contains(RSTile... tiles) {
		RSTile[] areaTiles = this.getTileArray();
		for (RSTile check : tiles) {
			for (RSTile space : areaTiles) {
				if (check.equals(space)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return The central <b>RSTile</b> of the <b>RSArea</b>.
	 */
	public RSTile getCentralTile() {
		if (area.npoints < 1) {
			return null;
		}
		int totalX = 0, totalY = 0;
        for (int i = 0; i < area.npoints; i++) {
            totalX += area.xpoints[i];
            totalY += area.ypoints[i];
		}
		return new RSTile(ctx, Math.round(totalX / area.npoints),
				Math.round(totalY / area.npoints));
	}

	/**
	 * @param base The base tile to measure the closest tile off of.
	 * @return The nearest <b>RSTile</b> in the <b>RSArea</b>
	 *         to the given <b>RSTile</b>.
	 */
	public RSTile getNearestTile(RSTile base) {
		RSTile[] tiles = this.getTileArray();
		RSTile cur = null;
		double dist = -1;
		for (RSTile tile : tiles) {
			double distTmp = distanceBetween(tile, base);
			if (cur == null) {
				dist = distTmp;
				cur = tile;
			} else if (distTmp < dist) {
				cur = tile;
				dist = distTmp;
			}
		}
		return cur;
	}

	/**
	 * @return The <b>RSTiles</b> the <b>RSArea</b> contains.
	 */
	public RSTile[] getTileArray() {
		ArrayList<RSTile> list = new ArrayList<>();
		for (int x = this.getX(); x <= (this.getX() + this.getWidth()); x++) {
			for (int y = this.getY(); y <= (this.getY() + this.getHeight()); y++) {
				if (this.area.contains(new Point(x, y))) {
					list.add(new RSTile(ctx, x, y, plane));
				}
			}
		}
		RSTile[] tiles = new RSTile[list.size()];
		for (int i = 0; i < list.size(); i++) {
			tiles[i] = list.get(i);
		}
		return tiles;
	}

	/**
	 * @return The <b>RSTiles</b> the <b>RSArea</b> contains.
	 */
	public RSTile[][] getTiles() {
		RSTile[][] tiles = new RSTile[this.getWidth()][this.getHeight()];
		for (int i = 0; i < this.getWidth(); ++i) {
			for (int j = 0; j < this.getHeight(); ++j) {
                if (this.area.contains(new Point(this.getX() + i, this.getY() + j))) {
					tiles[i][j] = new RSTile(ctx, this.getX() + i, this.getY() + j);
				}
			}
		}
		return tiles;
	}

	/**
	 * @return a random <b>RSTile</b> in the <b>RSArea</b>
	 */
	public RSTile getRandomTile() {
		RSTile[] tiles = this.getTileArray();
		return tiles[(int) (Math.random() * tiles.length)];
	}

	/**
	 * @return The distance between the the <b>RSTile</b> that's most
	 *         <i>East</i> and the <b>RSTile</b> that's most <i>West</i>.
	 */
	public int getWidth() {
		return this.area.getBounds().width;
	}

	/**
	 * @return The distance between the the <b>RSTile</b> that's most
	 *         <i>South</i> and the <b>RSTile</b> that's most <i>North</i>.
	 */
	public int getHeight() {
		return this.area.getBounds().height;
	}

	/**
	 * @return The X axle of the <b>RSTile</b> that's most <i>West</i>.
	 */
	public int getX() {
		return this.area.getBounds().x;
	}

	/**
	 * @return The Y axle of the <b>RSTile</b> that's most <i>South</i>.
	 */
	public int getY() {
		return this.area.getBounds().y;
	}

	/**
	 * @return The plane of the <b>RSArea</b>.
	 */
	public int getPlane() {
		return plane;
	}

	/**
	 * @return The bounding box of the <b>RSArea</b>.
	 */
	public Rectangle getBounds() {
		return new Rectangle(this.area.getBounds().x + 1,
				this.area.getBounds().y + 1, this.getWidth(), this.getHeight());
	}

	/**
	 * Converts an shape made of <b>RSTile</b> to a polygon.
	 *
	 * @param tiles The <b>RSTile</b> of the Polygon.
	 * @return The Polygon of the <b>RSTile</b>.
	 */
	private TileArea tileArrayToPolygon(RSTile[] tiles) {
		TileArea poly = new TileArea();
		for (RSTile t : tiles) {
			poly.addPoint(t.getWorldLocation().getX(), t.getWorldLocation().getY());
		}

		return poly;
	}

	/**
	 * @param curr first <b>RSTile</b>
	 * @param dest second <b>RSTile</b>
	 * @return the distance between the first and the second rstile
	 */
	private double distanceBetween(RSTile curr, RSTile dest) {
		return Math.sqrt((curr.getWorldLocation().getX() - dest.getWorldLocation().getX())
				* (curr.getWorldLocation().getX() - dest.getWorldLocation().getX()) + (curr.getWorldLocation().getY() - dest.getWorldLocation().getY())
				* (curr.getWorldLocation().getY() - dest.getWorldLocation().getY()));
	}

	class TileArea extends Polygon {
		Point[] points = new Point[]{};

		TileArea() {
			super();
		}

        @Override
        public void addPoint(int x, int y) {
            super.addPoint(x, y);
            Point[] previousPoints = points;
            points = new Point[previousPoints.length + 1];
            System.arraycopy(previousPoints, 0, points, 0, previousPoints.length);
			points[previousPoints.length] = new Point(x, y);
		}

		@Override
		public boolean contains(Point test) {
			int i, j;

			for (i = 0, j = points.length - 1; i < points.length; j = i++) {
				boolean checkOne = (points[i].y > test.y) != (points[j].y > test.y) &&
						(test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y-points[i].y) + points[i].x);
				boolean checkTwo = (points[i].y >= test.y) != (points[j].y >= test.y) &&
						(test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y-points[i].y) + points[i].x);
				boolean checkThree = (points[i].y >= test.y) != (points[j].y >= test.y) &&
						(test.x <= (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y-points[i].y) + points[i].x);
				boolean checkFour = (points[i].y > test.y) != (points[j].y > test.y) &&
						(test.x <= (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y-points[i].y) + points[i].x);
				if (checkOne || checkTwo || checkThree || checkFour) {
					return true;
				}
			}
			return false;
		}
	}
}