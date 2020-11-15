package me.syldium.decoudre.api.player;

/**
 * The result of a player's jump.
 */
public enum JumpVerdict {

    /**
     * Missed the water.
     */
    FAIL,

    /**
     * Landed in the water.
     */
    SUCCESS,

    /**
     * Landed in the water and did a combo.
     */
    SUCCESS_DAC
}
