package org.poker;

/**
 * Represents a poker action taken by a player or the dealer.
 * @author Samarth P
 * @version 1.0
 */
public class Action {
    private String actionKey; //eg: P0:Bet
    
    public Action(String actionKey) {
        this.actionKey = actionKey;
    }
    
    @Override
    public String toString() {
        return this.actionKey;
    }
    
    public String getActionKey() {
        return this.actionKey;
    }
    
    /**
     * Gets the player number from the action
     */
    public int getPlayer() {
        if (actionKey.startsWith("P") && actionKey.contains(":")) {
            try {
                int colonIndex = actionKey.indexOf(":");
                String playerStr = actionKey.substring(1, colonIndex);
                return Integer.parseInt(playerStr);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                return -1;
            }
        }
        return -1;
    }
    
    /**
     * Gets the action type
     */
    public String getType() {
        if (actionKey.contains(":")) {
            String[] parts = actionKey.split(":");
            return parts[1];
        }
        return actionKey;
    }
    
    /**
     * Gets the amount if present
     */
    public double getAmount() {
        String[] parts = actionKey.split(":");
        if (parts.length >= 3) {
            try {
                return Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
    
    /**
     * Checks if this is a betting action
     */
    public boolean isBettingAction() {
        String type = getType();
        return type.equals("Bet") || type.equals("Call") || type.equals("Raise");
    }
    
    /**
     * Checks if this is a fold action
     */
    public boolean isFold() {
        return getType().equals("Fold");
    }
}
