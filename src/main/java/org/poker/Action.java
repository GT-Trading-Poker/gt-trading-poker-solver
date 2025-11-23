package org.poker;

/**
 * Represents a poker action taken by the player or the dealer.
 * @author Samarth P
 * @version 2.0
 */
public class Action {
     // Enum representing possible action types in poker
    public enum ActionType {
        BET("Bet"),
        CALL("Call"),
        RAISE("Raise"),
        FOLD("Fold"),
        CHECK("Check"),
        ALL_IN("AllIn"),
        DEAL("Deal"),
        UNKNOWN("Unknown");
        
        private final String displayName;
        
        ActionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        /**
         * Parse action type from string (case-insensitive)
         */
        public static ActionType fromString(String type) {
            if (type == null) return UNKNOWN;
            
            for (ActionType actionType : values()) {
                if (actionType.displayName.equalsIgnoreCase(type)) {
                    return actionType;
                }
            }
            return UNKNOWN;
        }
        
        /**
         * Check if this is a betting action
         */
        public boolean isBettingAction() {
            return this == BET || this == CALL || this == RAISE || this == ALL_IN;
        }
        
        /**
         * Check if this action requires an amount
         */
        public boolean requiresAmount() {
            return this == BET || this == RAISE || this == ALL_IN;
        }
    }
    
    private final String actionKey;
    private final int player;
    private final ActionType type;
    private final double amount;
    
    /**
     * Constructor from action key string
     */
    public Action(String actionKey) {
        this.actionKey = actionKey;
        this.player = parsePlayer(actionKey);
        this.type = parseType(actionKey);
        this.amount = parseAmount(actionKey);
    }
    
    /**
     * Constructor with specified attributes
     */
    public Action(int player, ActionType type, double amount) {
        this.player = player;
        this.type = type;
        this.amount = amount;
        this.actionKey = buildActionKey(player, type, amount);
    }
    
    /**
     * Constructor without amount
     */
    public Action(int player, ActionType type) {
        this(player, type, 0.0);
    }
    
    /**
     * Get Player number
     */
    private int parsePlayer(String actionKey) {
        if (actionKey.startsWith("P") && actionKey.contains(":")) {
            try {
                int i = actionKey.indexOf(":");
                String playerStr = actionKey.substring(1, i);
                return Integer.parseInt(playerStr);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                return -1;
            }
        }
        return -1;
    }
    
    /**
     * Get action type
     */
    private ActionType parseType(String actionKey) {
        if (actionKey.contains(":")) {
            String[] parts = actionKey.split(":");
            return ActionType.fromString(parts[1]);
        }
        return ActionType.fromString(actionKey);
    }
    
    /**
     * Get amount
     */
    private double parseAmount(String actionKey) {
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
     * Make Action Key
     */
    private String buildActionKey(int player, ActionType type, double amount) {
        if (player < 0) {
            return type.getDisplayName();
        }
        
        if (amount > 0 && type.requiresAmount()) {
            return String.format("P%d:%s:%.2f", player, type.getDisplayName(), amount);
        }
        
        return String.format("P%d:%s", player, type.getDisplayName());
    }
    
    public String getActionKey() {
        return actionKey;
    }
    
    public int getPlayer() {
        return player;
    }
    
    public ActionType getType() {
        return type;
    }
    
    public String getTypeString() {
        return type.getDisplayName();
    }
    
    public double getAmount() {
        return amount;
    }
    
    // Methods we may need
    public boolean isBettingAction() {
        return type.isBettingAction();
    }
    
    public boolean isFold() {
        return type == ActionType.FOLD;
    }
    
    public boolean isCheck() {
        return type == ActionType.CHECK;
    }
    
    public boolean isAllIn() {
        return type == ActionType.ALL_IN;
    }
    
    public boolean isValid() {
        return type != ActionType.UNKNOWN && player >= -1;
    }
    
    @Override
    public String toString() {
        return actionKey;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Action)) return false;
        Action other = (Action) obj;
        return player == other.player && 
               type == other.type && 
               Double.compare(amount, other.amount) == 0;
    }
}
