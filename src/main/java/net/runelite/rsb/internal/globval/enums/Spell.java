package net.runelite.rsb.internal.globval.enums;


import net.runelite.rsb.methods.MethodContext;
import net.runelite.rsb.wrappers.RSWidget;

public interface Spell
{
    int getLevel();

    RSWidget getWidget(MethodContext ctx);

    boolean canCast(MethodContext ctx);

    public int getBaseHit();

    public class RuneRequirement
    {
        int quantity;
        Rune rune;

        public RuneRequirement(int quantity, Rune rune) {
            this.quantity = quantity;
            this.rune = rune;
        }

        public boolean meetsRequirements()
        {
            return rune.getQuantity() >= quantity;
        }
    }
}