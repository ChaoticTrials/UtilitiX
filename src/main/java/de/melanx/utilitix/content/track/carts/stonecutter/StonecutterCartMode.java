package de.melanx.utilitix.content.track.carts.stonecutter;

public enum StonecutterCartMode {

    TOP(0, 0, 1),
    LEFT(0, 1, 0),
    RIGHT(0, -1, 0),
    TOP_LEFT(0, 1, 1),
    TOP_RIGHT(0, -1, 1),
    FRONT(1, 0, 0);
    
    public final int offsetTrack;
    public final int offsetLeft;
    public final int offsetHor;

    StonecutterCartMode(int offsetTrack, int offsetLeft, int offsetHor) {
        this.offsetTrack = offsetTrack;
        this.offsetLeft = offsetLeft;
        this.offsetHor = offsetHor;
    }
}
