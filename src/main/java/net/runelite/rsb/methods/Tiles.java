package net.runelite.rsb.methods;

import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.rsb.wrappers.RSTile;

/**
 * Tile related operations.
 */
public class Tiles extends MethodProvider {

    private final MethodContext ctx;
	Tiles(final MethodContext ctx) {
        super(ctx);
        this.ctx = ctx;
	}

	/**
	 * Clicks a tile if it is on screen with given offsets in 3D space.
	 *
	 * @param tile   The <code>RSTile</code> to do the action at.
	 * @param xd     Distance from bottom left of the tile to bottom right. Ranges
	 *               from 0-1.
	 * @param yd     Distance from bottom left of the tile to top left. Ranges from
	 *               0-1.
	 * @param h      Height to click the <code>RSTile</code> at. Use 1 for tables,
	 *               0 by default.
	 * @param action The action to perform at the given <code>RSTile</code>.
	 * @return <code>true</code> if no exceptions were thrown; otherwise
	 *         <code>false</code>.
	 */
	public boolean doAction(final RSTile tile, final double xd,
	                        final double yd, final int h, final String action) {
        return ctx.tiles.doAction(tile, xd, yd, h, action, null);
	}

	public boolean doAction(final RSTile tile, final double xd,
	                        final double yd, final int h, final String action, final String option) {
        Point location = ctx.calc.tileToScreen(tile, xd, yd, h);
		if (location.getX() != -1 && location.getY() != -1) {
            ctx.mouse.move(location, 3, 3);
            sleep(random(20, 100));
            return ctx.menu.doAction(action, option);
		}
		return false;
	}

    /**
     * Clicks a tile if it is on screen. It will left-click if the action is
     * available as the default option, otherwise it will right-click and check
     * for the action in the context ctx.menu.
     *
     * @param tile   The RSTile that you want to click.
     * @param action Action command to use click
     * @return <code>true</code> if the tile was clicked; otherwise
     * <code>false</code>.
     */
	public boolean doAction(final RSTile tile, final String action) {
        return ctx.tiles.doAction(tile, action, null);
    }

    /**
     * Clicks a tile if it is on screen. It will left-click if the action is
     * available as the default menu action, otherwise it will right-click and check
     * for the action in the context ctx.menu.
     *
     * @param tile   The RSTile that you want to click.
     * @param action Action of the menu entry to click
     * @param option Option of the menu entry to click
     * @return <code>true</code> if the tile was clicked; otherwise
     *         <code>false</code>.
	 */
	public boolean doAction(final RSTile tile, final String action, final String option) {
		try {
			for (int i = 0; i++ < 5; ) {
                Point location = ctx.calc.tileToScreen(tile);
                if (location.getX() == -1 || location.getY() == -1) {
                    return false;
                }
                ctx.mouse.move(location, 5, 5);
                if (ctx.menu.doAction(action, option)) {
                    return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Returns the RSTile under the mouse.
	 *
	 * @return The <code>RSTile</code> under the mouse, or null if the mouse is
	 *         not over the viewport.
	 */
	public RSTile getTileUnderMouse() {
        Point p = ctx.mouse.getLocation();
        if (!ctx.calc.pointOnScreen(p)) {
            return null;
        }
        RSTile close = null;
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                RSTile t = new RSTile(ctx, x + ctx.client.getBaseX(), y
                        + ctx.client.getBaseY(), ctx.client.getPlane());
                Point s = ctx.calc.tileToScreen(t);
                if (s.getX() != -1 && s.getY() != -1) {
                    if (close == null) {
                        close = t;
                    }
                    if (ctx.calc.tileToScreen(close).distanceTo(p) > ctx.calc
                            .tileToScreen(t).distanceTo(p)) {
                        close = t;
					}
				}
			}
		}
		return close;
	}

	/**
	 * Gets the tile under a point.
	 *
	 * @param point	a point (X, Y)
	 * @return RSTile at the point's location
	 */
	public RSTile getTileUnderPoint(final Point point) {
        if (!ctx.calc.pointOnScreen(point)) {
            return null;
		}
		RSTile close = null;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
                RSTile tile = new RSTile(ctx, x + ctx.client.getBaseX(), y
                        + ctx.client.getBaseY(), ctx.client.getPlane());
                Point pointOfTile = ctx.calc.tileToScreen(tile);
                if (pointOfTile.getX() != -1 && pointOfTile.getY() != -1) {
                    if (close == null) {
                        close = tile;
                    }
                    if (ctx.calc.tileToScreen(close).distanceTo(point) > ctx.calc
                            .tileToScreen(tile).distanceTo(point)) {
                        close = tile;
					}
				}
			}
		}
		return close;
	}

	public Tile getTile(MethodContext ctx, WorldPoint worldPoint) {
		return getTile(ctx, worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
	}

	public Tile getTile(MethodContext ctx, int x, int y, int plane) {
		Client client = ctx.client;
		int correctedX = x < Constants.SCENE_SIZE ? x + client.getBaseX() : x;
		int correctedY = y < Constants.SCENE_SIZE ? y + client.getBaseY() : y;

		if (!WorldPoint.isInScene(client, correctedX, correctedY))
		{
			return null;
		}

		x = correctedX - client.getBaseX();
		y = correctedY - client.getBaseY();

		return client.getScene().getTiles()[plane][x][y];
	}

	/**
	 * Checks if the tile "t" is closer to the player than the tile "tt"
	 *
	 * @param tile1 First tile.
	 * @param tile2 Second tile.
	 * @return True if the first tile is closer to the player than the second
	 *         tile, otherwise false.
	 */
	public boolean isCloser(RSTile tile1, RSTile tile2) {
        return ctx.calc.distanceTo(tile1) < ctx.calc.distanceTo(tile2);
	}
}
