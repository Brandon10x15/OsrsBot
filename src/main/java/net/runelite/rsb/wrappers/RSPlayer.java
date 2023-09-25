package net.runelite.rsb.wrappers;

import net.runelite.api.Actor;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.rsb.methods.MethodContext;

import java.lang.ref.SoftReference;

/**
 * Represents a player.
 */
public class RSPlayer extends RSCharacter {

	private final SoftReference<Player> p;
    private final MethodContext ctx;

	public RSPlayer(final MethodContext ctx, final Player p) {
		super(ctx);
        this.ctx = ctx;
        this.p = new SoftReference<>(p);
	}

	public Actor getAccessor() {
		return p.get();
	}

	public Actor getInteracting() {
		Actor interacting = getAccessor().getInteracting();
		if (interacting != null) {
			return getAccessor().getInteracting();
		}
		return null;
	}

	public int getCombatLevel() {
        Player player = p.get();
        if (player == null) {
            return -1;
        }
        return player.getCombatLevel();
    }

	public boolean isLocalPlayerMoving() {
        if (ctx.client.getLocalDestinationLocation() != null) {
            return ctx.client.getLocalPlayer().getLocalLocation() == ctx.client.getLocalDestinationLocation();
        }
        return false;
    }

	public boolean isMoving() {
		var poseAnimation = getAccessor().getPoseAnimation();
		return isLocalPlayerMoving()
				|| poseAnimation == getAccessor().getWalkRotate180()
				|| poseAnimation == getAccessor().getWalkRotateLeft()
				|| poseAnimation == getAccessor().getWalkRotateRight()
				|| poseAnimation == getAccessor().getRunAnimation()
				|| poseAnimation == getAccessor().getWalkAnimation();
  }

	@Override
	public String getName() {
        Player player = p.get();
        if (player == null) {
            return null;
        }
        return player.getName();
    }

	public int getTeam() {
        Player player = p.get();
        if (player == null) {
            return -1;
        }
        return player.getTeam();
    }

	public boolean isIdle() {
		return getAnimation() == -1 && !isInCombat();
	}

	@Override
	public boolean doAction(final String action) {
		return doAction(action, getName());
	}

	@Override
	public boolean doAction(final String action, final String target) {
		final RSModel model = getModel();
		if (model != null && isValid()) {
			return model.doAction(action, target);
		}
		try {
			Point screenLoc;
			for (int i = 0; i < 20; i++) {
                screenLoc = getScreenLocation();
                if (!isValid() || !ctx.calc.pointOnScreen(screenLoc)) {
                    return false;
                }
                if (ctx.mouse.getLocation().equals(screenLoc)) {
                    break;
                }
                ctx.mouse.move(screenLoc);
            }
            MenuEntry[] entries = ctx.menu.getEntries();
			if (entries.length <= 1) {
				return false;
			}
			if (entries[0].getOption().toLowerCase().contains(action.toLowerCase())) {
                ctx.mouse.click(true);
				return true;
			} else {
                ctx.mouse.click(false);
                return ctx.menu.doAction(action, target);
            }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String toString() {
		return "Player[" + getName() + "]" + super.toString();
	}

	public RSTile getPosition() {
		return getLocation();
	}
}