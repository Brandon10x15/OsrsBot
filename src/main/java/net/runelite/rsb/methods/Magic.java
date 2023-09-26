package net.runelite.rsb.methods;

import net.runelite.api.VarPlayer;
import net.runelite.rsb.internal.globval.GlobalWidgetInfo;
import net.runelite.rsb.internal.globval.VarpIndices;
import net.runelite.rsb.internal.globval.VarpValues;
import net.runelite.rsb.internal.globval.enums.InterfaceTab;
import net.runelite.rsb.internal.globval.enums.MagicBook;
import net.runelite.rsb.internal.globval.enums.Spell;
import net.runelite.rsb.wrappers.RSCharacter;
import net.runelite.rsb.wrappers.RSGroundItem;
import net.runelite.rsb.wrappers.RSObject;
import net.runelite.rsb.wrappers.RSWidget;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Magic tab and spell related operations.
 *
 * @author Gigiaj
 */
public class Magic extends MethodProvider {

    private final MethodContext ctx;
	Magic(final MethodContext ctx) {
        super(ctx);
        this.ctx = ctx;
	}

	/**
	 * Checks whether a spell is selected.
	 *
	 * @return <code>true</code> if a spell is selected; otherwise <code>false</code>.
	 */
	public boolean isSpellSelected() {
        RSWidget widget = ctx.interfaces.getComponent(GlobalWidgetInfo.MAGIC_SPELL_LIST);
		for (RSWidget child : widget.getComponents()) {
			if (child.isVisible() || child.isSelfVisible()) {
				//Check api.widget to see what border is what or just validate that when one is selected
				// what border type it has at the time
				if (child.getBorderThickness() == 2) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines whether a spell is currently set to autocast.
	 *
	 * @return <code>true</code> if autocasting; otherwise <code>false</code>.
	 */
	public boolean isAutoCasting() {
        return ctx.combat.getFightMode() == VarpValues.COMBAT_STYLE_AUTOCAST.getValue();
	}

    /**
     * Clicks a specified spell, opens magic tab if not open and uses interface
     * of the spell to click it, so it works if the spells are layout in any
     * sway.
     *
     * @param spell The spell to cast.
     * @return <code>true</code> if the spell was clicked; otherwise <code>false</code>.
     */
    public boolean castSpell(final Spell spell) {
        if (ctx.game.getCurrentTab() != InterfaceTab.MAGIC) {
            ctx.game.openTab(InterfaceTab.MAGIC);
            for (int i = 0; i < 100; i++) {
                sleep(20);
                if (ctx.game.getCurrentTab() == InterfaceTab.MAGIC) {
                    break;
                }
            }
            sleep(random(150, 250));
        }
        if (ctx.game.getCurrentTab() == InterfaceTab.MAGIC) {
            RSWidget inter = getInterface();
            if (inter != null) {
                RSWidget comp = spell.getWidget(ctx);
                return comp != null && comp.doAction("Cast");
            }
        }
        return false;
    }

    /**
     * Hovers a specified spell, opens magic tab if not open and uses interface
     * of the spell to hover it, so it works if the spells are layout in any
     * sway.
     *
     * @param spell The spell to hover.
     * @return <code>true</code> if the spell was clicked; otherwise <code>false</code>.
     */
    public boolean hoverSpell(final Spell spell) {
        if (ctx.game.getCurrentTab() != InterfaceTab.MAGIC) {
            ctx.game.openTab(InterfaceTab.MAGIC);
            for (int i = 0; i < 100; i++) {
                sleep(20);
                if (ctx.game.getCurrentTab() == InterfaceTab.MAGIC) {
                    break;
                }
            }
            sleep(random(150, 250));
        }
        if (ctx.game.getCurrentTab() == InterfaceTab.MAGIC) {
            RSWidget inter = getInterface();
            if (inter != null) {
                RSWidget comp = spell.getWidget(ctx);
                return comp != null && comp.doHover();
            }
        }
        return false;
    }

    /**
     * Auto-casts a spell via the magic tab.
     *
     * @param spell The spell to auto-cast.
     * @return <code>true</code> if the "Auto-cast" interface option was clicked;
     * otherwise <code>false</code>.
     */
    public boolean autoCastSpell(final Spell spell) {
        if (ctx.clientLocalStorage.getVarpValueAt(VarpIndices.COMBAT_STYLE)
                != VarpValues.COMBAT_STYLE_AUTOCAST.getValue()) {
            if (ctx.game.getCurrentTab() != InterfaceTab.COMBAT) {
                ctx.game.openTab(InterfaceTab.COMBAT);
                sleep(random(150, 250));
            }
            if (ctx.interfaces.getComponent(GlobalWidgetInfo.COMBAT_AUTO_CAST_SPELL).doClick()) {
                sleep(random(500, 700));
                RSWidget widget = ctx.interfaces.getComponent(GlobalWidgetInfo.MAGIC_SPELL_LIST);
                //The children are the spells
                for (RSWidget child : widget.getComponents()) {
                    //To speed up the search we'll filter the undesirables
                    if (child.isVisible() || child.isSelfVisible()) {
                        //This is the autocast book spell list
                        for (RSWidget autoCastSpell : ctx.interfaces.getComponent(GlobalWidgetInfo.MAGIC_AUTOCAST_SPELL_LIST).getComponents()) {
                            //We need to compare sprites to determine if we've found the right value
                            //This alleviates the need to devise a convoluted method to find spells in this book
                            //All the spells start from at 4 so the index needs to be adjusted for that
                            if (child.getName().toLowerCase().contains(spell.toString().toLowerCase().replaceAll("_(ARCEUUS|STANDARD)?", " "))) {
                                if (autoCastSpell.getSpriteId() == child.getSpriteId()) {
                                    return autoCastSpell.doClick();
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

	/**
	 * Gets the open magic book interface.
	 *
	 * @return The current magic RSWidget.
	 */
	public RSWidget getInterface() {
        RSWidget widget = ctx.interfaces.getComponent(GlobalWidgetInfo.MAGIC_SPELL_LIST);
		if (widget.isVisible()) {
			return widget;
		}
		return null;
	}

    /**
     * Gets the current spell book.
     *
     * @return The Book enum of your current spell book.
     */
    public MagicBook getCurrentSpellBook(MethodContext ctx) {
        return MagicBook.getCurrent(ctx);
    }

    /**
     * Casts a spell on a Player/NPC/Object/Ground Item.
     *
     * @param entity A Character or Animable.
     * @param spell  The spell to cast.
     * @return <code>true</code> if casted; otherwise <code>false</code>.
     */
    public boolean castSpellOn(MethodContext ctx, final Object entity, final Spell spell) {
        if (isSpellSelected() || entity == null) {
            return false;
        }
        if (castSpell(spell)) {
            if (entity instanceof RSCharacter) {
                return ((RSCharacter) entity).doAction("Cast");
            } else if (entity instanceof RSObject) {
                return ((RSObject) entity).doAction("Cast");
            } else if (entity instanceof RSGroundItem) {
                return ((RSGroundItem) entity).doAction("Cast");
            }
        }
        return false;
    }

    public Instant getLastHomeTeleportUsage() {
        return Instant.ofEpochSecond(ctx.client.getVarpValue(VarPlayer.LAST_HOME_TELEPORT) * 60L);
    }

    public boolean isHomeTeleportOnCooldown() {
        return getLastHomeTeleportUsage().plus(30, ChronoUnit.MINUTES).isAfter(Instant.now());
    }
}
