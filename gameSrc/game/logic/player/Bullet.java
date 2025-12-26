package game.logic.player;

public class Bullet {

    public BulletStandard bulletStandard;

    private int pos_x;
    private int pos_y;
    private Boolean mustRender;
    
    public Bullet(int pos_x, int pos_y, 
                BulletStandard bulletStandard){
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.bulletStandard = bulletStandard;
        mustRender = true;
    }

    public int getPosX(){
        return pos_x;
    }

    public int getPosY(){
        return pos_y;
    }

    public void move(){
        pos_x += 40;
    }

    public void setMustRender(Boolean mustRender){
        this.mustRender = mustRender;
    }

    public boolean getMustRender(){
        return mustRender;
    }
}