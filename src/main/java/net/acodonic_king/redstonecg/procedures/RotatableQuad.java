package net.acodonic_king.redstonecg.procedures;

public class RotatableQuad {
    public final float[][] RECTOID = new float[][]{
            {0f, 0f, 0f, 1f},
            {1f, 0f, 0f, 0f},
            {1f, 1f, 1f, 0f},
            {0f, 1f, 1f, 1f}
    };
    public float WIDTH = 1f; // Quad width
    public float HEIGHT = 1f; // Quad height
    public float POS_X = 0f; // Quad center x world position
    public float POS_Y = 0f; // Quad center y world position
    public float CEN_X = 0f; // On Quad center x
    public float CEN_Y = 0f; // On Quad center y
    public float ANGLE = 0f;
    public RotatableQuad(){}
    
    public float w(){return WIDTH;}
    public RotatableQuad w(float v){WIDTH = v; return this;}
    public float h(){return HEIGHT;}
    public RotatableQuad h(float v){HEIGHT = v; return this;}
    
    public float px(){return POS_X;}
    public RotatableQuad px(float v){POS_X = v; return this;}
    public float py(){return POS_Y;}
    public RotatableQuad py(float v){POS_Y = v; return this;}
    
    public float cx(){return CEN_X;}
    public RotatableQuad cx(float v){CEN_X = v; return this;}
    public float cy(){return CEN_Y;}
    public RotatableQuad cy(float v){CEN_Y = v; return this;}
    
    public float degrees(){return (float) Math.toDegrees(ANGLE);}
    public RotatableQuad degrees(float v){ANGLE = (float) Math.toRadians(v); return this;}
    public float radians(){return ANGLE;}
    public RotatableQuad radians(float v){ANGLE = v; return this;}

    public float rotateX(float x, float y, float val_cos, float val_sin){
        return (x * val_cos - y * val_sin);
    }
    public float rotateY(float x, float y, float val_cos, float val_sin){
        return (x * val_sin + y * val_cos);
    }
    
    public RotatableQuad transform(){
        float val_cos = (float) Math.cos(ANGLE);
        float val_sin = (float) Math.sin(ANGLE);
        
        float rx = WIDTH - CEN_X;
        float by = HEIGHT - CEN_Y;

        RECTOID[0][0] = rotateX(-CEN_X, -CEN_Y, val_cos, val_sin);
        RECTOID[0][1] = rotateY(-CEN_X, -CEN_Y, val_cos, val_sin);

        RECTOID[3][0] = rotateX(rx, -CEN_Y, val_cos, val_sin);
        RECTOID[3][1] = rotateY(rx, -CEN_Y, val_cos, val_sin);

        RECTOID[2][0] = rotateX(rx, by, val_cos, val_sin);
        RECTOID[2][1] = rotateY(rx, by, val_cos, val_sin);

        RECTOID[1][0] = rotateX(-CEN_X, by, val_cos, val_sin);
        RECTOID[1][1] = rotateY(-CEN_X, by, val_cos, val_sin);

        for(int i = 0; i < RECTOID.length; i++){
            RECTOID[i][0] += POS_X;
            RECTOID[i][1] += POS_Y;
        }

        return this;
    }
}
