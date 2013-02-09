package net.syamn.sakuracmd.serial.endreset;

import java.io.Serializable;

public class EndResetChunk implements Serializable {
    private static final long serialVersionUID = 6559801774574102260L;
    
    final String world;
    final int x;
    final int z;
    long v;

    /**
     * コンストラクタ
     * 
     * @param world
     * @param x
     * @param z
     */
    public EndResetChunk(final String world, final int x, final int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        result = prime * result + x;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof EndResetChunk)) return false;

        final EndResetChunk other = (EndResetChunk) obj;
        if (world.equals(other.world) && x == other.x && z == other.z) return true;

        return false;
    }
}
