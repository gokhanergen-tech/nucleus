package nucleus.core.models.terrain;

public class BlendMapTerrain {

    TerrainTexture backdround,redTexture,greenTexture,blueTexture;

    public BlendMapTerrain(TerrainTexture backdround, TerrainTexture redTexture, TerrainTexture greenTexture, TerrainTexture blueTexture) {
        this.backdround = backdround;
        this.redTexture = redTexture;
        this.greenTexture = greenTexture;
        this.blueTexture = blueTexture;
    }

    public TerrainTexture getBackdround() {
        return backdround;
    }

    public TerrainTexture getRedTexture() {
        return redTexture;
    }

    public TerrainTexture getGreenTexture() {
        return greenTexture;
    }

    public TerrainTexture getBlueTexture() {
        return blueTexture;
    }
}
