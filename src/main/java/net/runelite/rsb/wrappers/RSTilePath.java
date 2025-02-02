package net.runelite.rsb.wrappers;

import net.runelite.rsb.methods.MethodContext;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * A path consisting of a list of tile waypoints.
 *
 * @author GigiaJ
 */
public class RSTilePath extends RSPath {

	protected RSTile[] tiles;
	protected RSTile[] orig;

	private boolean end;
    private final MethodContext ctx;

	public RSTilePath(MethodContext ctx, RSTile[] tiles) {
        super(ctx);
        this.ctx = ctx;
        this.orig = tiles;
		this.tiles = Arrays.copyOf(tiles, tiles.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean traverse(EnumSet<TraversalOption> options) {
		RSTile next = getNext();
		if (next == null) {
			return false;
		}
        if (next.equals(getEnd())) {
            if (ctx.calc.distanceTo(next) <= 1 || (end && ctx.players.getMyPlayer().isLocalPlayerMoving()) || next.equals(
                    ctx.walking.getDestination())) {
                return false;
            }
            end = true;
        } else {
            end = false;
        }
        if (options != null && options.contains(
                TraversalOption.HANDLE_RUN) && !ctx.walking.isRunEnabled() && ctx.walking.getEnergy() > 50) {
            ctx.walking.setRun(true);
            sleep(300);
        }
        if (options != null && options.contains(TraversalOption.SPACE_ACTIONS)) {
            RSTile dest = ctx.walking.getDestination();
            if (dest != null && ctx.players.getMyPlayer().isLocalPlayerMoving() &&
                    ctx.calc.distanceTo(dest) > 5 &&
                    ctx.calc.distanceBetween(next, dest) < 7) {
                return true;
            }
        }
        return ctx.walking.walkTileMM(next, 0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid() {
        return tiles.length > 0 && getNext() != null &&
                !ctx.players.getMyPlayer().getLocation().equals(getEnd());
	}

	/**
	 * {@inheritDoc}
	 */
	public RSTile getNext() {
		for (int i = tiles.length - 1; i >= 0; --i) {
            if (ctx.calc.tileOnMap(tiles[i])) {
                return tiles[i];
            }
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public RSTile getStart() {
		return tiles[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public RSTile getEnd() {
		return tiles[tiles.length - 1];
	}

	/**
	 * Randomize this path. The original path is stored so
	 * this method may be called multiple times without the
	 * waypoints drifting far from their original locations.
	 *
	 * @param maxX The max deviation on the X axis
	 * @param maxY The max deviation on the Y axis
	 * @return This path.
	 */
	public RSTilePath randomize(int maxX, int maxY) {
		for (int i = 0; i < tiles.length; ++i) {
			tiles[i] = orig[i].randomize(maxX, maxY);
		}
		return this;
	}

	/**
	 * Reverses this path.
	 *
	 * @return This path.
	 */
	public RSTilePath reverse() {
		RSTile[] reversed = new RSTile[tiles.length];
		for (int i = 0; i < orig.length; ++i) {
			reversed[i] = orig[tiles.length - 1 - i];
		}
		orig = reversed;
		reversed = new RSTile[tiles.length];
		for (int i = 0; i < tiles.length; ++i) {
			reversed[i] = tiles[tiles.length - 1 - i];
		}
		tiles = reversed;
		return this;
	}

	/**
	 * Returns an array containing all of the vertices in this path.
	 *
	 * @return an array containing all of the vertices in this path.
	 */
	public RSTile[] toArray() {
		RSTile[] a = new RSTile[tiles.length];
		System.arraycopy(tiles, 0, a, 0, tiles.length);
		return a;
	}

}