package me.syldium.thimble.common.util;

import java.util.Random;

public abstract class Fireworks {

    protected final Random random = new Random();

    public abstract void spawn(int count);

    protected int randomRed() {
        return this.random.nextInt(215) + 40;
    }

    protected int randomGreen() {
        return this.random.nextInt(175) + 80;
    }

    protected int randomBlue() {
        return this.random.nextInt(255);
    }
}
