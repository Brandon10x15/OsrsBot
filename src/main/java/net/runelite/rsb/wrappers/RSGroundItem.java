package net.runelite.rsb.wrappers;

import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.methods.MethodProvider;
import net.runelite.rsb.wrappers.common.ClickBox;
import net.runelite.rsb.wrappers.common.Clickable07;
import net.runelite.rsb.wrappers.common.Positionable;

import java.awt.*;
import java.util.List;

/**
 * Represents an item on a tile.
 */
public class RSGroundItem extends MethodProvider implements Clickable07, Positionable {
    private final RSItem groundItem;
    private final RSTile location;
    private final ClickBox clickBox;
    private final MethodContext ctx;

    public RSGroundItem(final MethodContext ctx, final RSTile location, final RSItem groundItem) {
        super(ctx);
        this.ctx = ctx;
        this.clickBox = new ClickBox(this.ctx, this);
        this.location = location;
        this.groundItem = groundItem;
    }

    /**
	 * Gets the top model on the tile of this ground item.
	 *
	 * @return The top model on the tile of this ground item.
	 */
	public RSModel getModel() {
        Tile tile = location.getTile(ctx);
		if (tile != null) {
			List<TileItem> groundItems = tile.getGroundItems();
			if (groundItems != null && !groundItems.isEmpty()) {
                return (tile.getItemLayer().getTop() != null) ?
                        new RSGroundObjectModel(ctx, tile.getItemLayer().getTop().getModel(), tile) :
                        new RSGroundObjectModel(ctx, groundItems.get(0).getModel(), tile);
            }
		}
		return null;
	}

	/**
	 * Performs the given action on this RSGroundItem.
	 *
	 * @param action The menu action to click.
	 * @return <code>true</code> if the action was clicked; otherwise <code>false</code>.
	 */
	public boolean doAction(final String action) {
		return doAction(action, groundItem.getName());
	}

	/**
	 * Performs the given action on this RSGroundItem.
	 *
	 * @param action The menu action to click.
	 * @param option The option of the menu action to click.
	 * @return <code>true</code> if the action was clicked; otherwise <code>false</code>.
	 */
	public boolean doAction(final String action, final String option) {
        if (getClickBox().doAction(action, option)) {
            return true;
        }
        return ctx.tiles.doAction(getLocation(), random(0.45, 0.55), random(0.45, 0.55), 0,
                action, option);
    }

	public RSItem getItem() {
		return groundItem;
	}

	public RSTile getLocation() {
        return new RSTile(ctx, location);
	}

	public boolean isOnScreen() {
		RSModel model = getModel();
		if (model == null) {
            return ctx.calc.tileOnScreen(location);
		} else {
            return ctx.calc.pointOnScreen(model.getPoint());
		}
	}

	public boolean turnTo() {
        if (!isOnScreen()) {
            ctx.camera.turnTo(getLocation());
            return isOnScreen();
        }
        return false;
    }

	public boolean doHover() {
		return getClickBox().doHover();
	}

	public boolean doClick() {
		return doClick(true);
	}

	public boolean doClick(boolean leftClick) {
		return getClickBox().doClick(leftClick);
	}

	public boolean isClickable() {
		RSModel model = getModel();
		if (model == null) {
			return false;
		}
		return model.getModel().isClickable();
	}

	public Shape getClickShape() {
		return getLocation().getClickShape();
	}
	public ClickBox getClickBox() {
		return clickBox;
	}
}