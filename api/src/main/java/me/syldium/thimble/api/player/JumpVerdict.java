package me.syldium.thimble.api.player;

/**
 * The result of a player's jump.
 */
public enum JumpVerdict {

    /**
     * Missed the water.
     */
    MISSED,

    /**
     * Landed in the water.
     */
    LANDED,

    /**
     * Landed in the water and did a thimble.
     */
    THIMBLE
}
