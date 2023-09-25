package net.runelite.rsb.wrappers;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.cache.definitions.NpcDefinition;
import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.common.CacheProvider;
import net.runelite.rsb.wrappers.common.Positionable;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public class RSNPC extends RSCharacter implements CacheProvider<NpcDefinition> {
    private static HashMap<Integer, NpcDefinition> npcDefinitionCache;
    private static HashMap<Integer, File> npcFileCache;
    private final SoftReference<NPC> npc;
    private final int id;
    private final NpcDefinition def;

    public RSNPC(final MethodContext ctx, final NPC npc) {
        super(ctx);
        this.npc = new SoftReference<>(npc);
        this.id = npc.getId();
        this.def = npc.getId() == -1 ? null : (NpcDefinition) createDefinition(npc.getId());
    }

    @Override
    public Actor getAccessor() {
        return this.npc.get();
    }

    @Override
    public Actor getInteracting() {
        return getAccessor().getInteracting();
    }

    public String[] getActions() {
        NpcDefinition def = getDef();

        if (def != null) {
            return def.getActions();
        }
        return new String[0];
    }

    public int getID() {
        return this.id;
    }

    public int getMaximumHP() {
        return methods.runeLite.getNPCManager().getHealth(getID());
    }

    public int getCurrentHP() {
        double healthRatio = getAccessor().getHealthRatio();
        double healthScale = getAccessor().getHealthScale();
        int maximumHealth = getMaximumHP();
        if (healthRatio == -1 || maximumHealth == -1) return -1;
        return isInCombat() ? (int)(healthRatio / healthScale * maximumHealth) : maximumHealth;
    }
    @Override
    public String getName() {
        return getAccessor().getName(); // Pulls name from NPC.
        /*
        NpcDefinition def = getDef();
        if (def != null) {
            return def.getName();
        }
        return "";*/
    }

    @Override
    public int getLevel() {
        NPC c = this.npc.get();
        if (c == null) {
            return -1;
        } else {
            return c.getCombatLevel();
        }
    }

    /**
     * @return <code>true</code> if RSNPC is interacting with RSPlayer; otherwise
     *         <code>false</code>.
     */
    @Override
    public boolean isInteractingWithLocalPlayer() {
        RSNPC npc = this;
        return npc.getInteracting() != null
                && npc.getInteracting().equals(methods.players.getMyPlayer().getAccessor());
    }

    public NpcDefinition getDef() {
        return this.def;
    }

    public RSTile getPosition() {
        return getLocation();
    }

    public int getWidth() {
        return getAccessor().getWorldArea().getWidth();
    }

    public int getHeight() {
        return getAccessor().getWorldArea().getHeight();
    }

    public RSTile getNearestTile(Positionable npc, Positionable from) {
        RSTile nearestTile = null;
        double minDistance = Float.POSITIVE_INFINITY;
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                RSTile tile = npc.getLocation().offset(i,j);
                double distance = npc.getLocation().offset(i,j).distanceToDouble(from);
                if (minDistance > distance) {
                    minDistance = distance;
                    nearestTile = tile;
                }
            }
        }
        return nearestTile;
    }

    public RSTile getNearestTile(Positionable from) {
        return getNearestTile(getLocation(), from.getLocation());
    }

    public RSTile getNearestTile() {
        return getNearestTile(getLocation(), methods.players.getMyPlayer());
    }

    /**
     * Line of sight of NPCS is calculated from the player to the npc regardless of who is attacking
     * This is the opposite of PvP where each player calculates their own LOS
     * @param from
     * @return
     */
    public boolean hasLineOfSight(Positionable from) {
        return methods.calc.hasLineOfSight(getNearestTile(from).getWorldLocation().toWorldArea(), from.getLocation().getWorldLocation().toWorldArea());
    }

    public boolean hasLineOfSight() {
        return methods.calc.hasLineOfSight(getNearestTile(methods.players.getMyPlayer()).getWorldLocation().toWorldArea(), methods.players.getMyPlayer().getLocation().getWorldLocation().toWorldArea());
    }
}
