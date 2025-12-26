package game.logic.zombie;

public class Zombie {
    public ZombieStandard zombieStandard;
    private int pos_x;
    private int pos_y;
    private int next_image;
    private int next_imageAttack;
    private int idZombie;
    private boolean attacking;
    private static int noZombies = 0;
    private int hp;
    private int speed;
    private int damageTaken;
    private boolean dead;
    private double previousTimeSinceDeath;
    private double currentTimeSinceDeath;
    private static double timeToRemove;
    private int idOfPlayerGettingAttacked;
    private boolean mustRender;


    public Zombie(int pos_x, int pos_y, ZombieStandard zombieStandard){
        this.pos_x = 1000;
        this.pos_y = pos_y;
        this.zombieStandard = zombieStandard;
        next_image = 0;
        next_imageAttack = 0;
        attacking = false;
        idZombie = noZombies;
        noZombies++;
        hp = 100;
        speed = 1;
        damageTaken = 25;
        dead = false;
        timeToRemove = 10000;
        mustRender = true;
    }

    public int getPosX(){
        return pos_x;
    }

    public int getPosY(){
        return pos_y;
    }

    public int getNextImage(){
        return next_image;
    }

    public void move(){
        pos_x -= speed;
        next_image++;
        if(next_image >= ZombieStandard.images.size()){
            next_image = 0;
        }
    }

    public int getNextImageAttack(){
        return next_imageAttack;
    }
    
    public void attack(int id){
        idOfPlayerGettingAttacked = id;
        next_imageAttack++;
        if(next_imageAttack >= ZombieStandard.attack.size()){
            next_imageAttack = 0;
        }
    }

    public boolean getAttacking(){
        return attacking;
    }

    public void setAttacking(Boolean attacking){
        this.attacking = attacking;
    }

    public int getNoZombies(){
        return noZombies;
    }

    public int getIdZombie(){
        return idZombie;
    }

    public int getWidth(){
        return zombieStandard.getWidth();
    }

    public int getHeight(){
        return zombieStandard.getHeight();
    }

    public void gettingShooted(){
        hp -= damageTaken;
    }

    public int getHP(){
        return hp;
    }

    public void kill(){
        previousTimeSinceDeath = System.currentTimeMillis();
        currentTimeSinceDeath = 0;
        dead = true;
    }

    public boolean isDead(){
        return dead;
    }
    
    public boolean isAboutTimeToRemove (){
        currentTimeSinceDeath = System.currentTimeMillis() - previousTimeSinceDeath;
        if(currentTimeSinceDeath >= timeToRemove){
            return true;
        }
        else{
            return false;
        }
    }

    public int getPlayerAttacked(){
        return idOfPlayerGettingAttacked;
    }

    public void resetPlayerAttacked(){
        idOfPlayerGettingAttacked = 0;
    }

    public void setMustRender(boolean mustRender){
        this.mustRender = mustRender;
    }

    public boolean getMustRender(){
        return mustRender;
    }
}
