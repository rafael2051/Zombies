package game.logic.player;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class Player {

    private int id;
    private static int count = 0;

    private int pos_x;
    private int pos_y;
    private int width;
    private int height;

    public Image imagePlayer;

    public List <Image> walk;
    public List <Image> shoot;
    public List <Image> reload;

    private boolean walking;
    private boolean shooting;
    private boolean reloading;

    private int nextImage;
    private int nextImageShooting;
    private int nextImageReloading;

    private int hp;

    private double previousTime;

    private double currentTime;
    private static double attackTime = 1000;
    private static int speed;
    private int ammo;

    private boolean mustReload;

    public Player (int id, int pos_x, int pos_y, int width, int height){
        this.id = id;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.width = width;
        this.height = height;
        this.hp = 100;
        BufferedImage bufferedPlayer = null;
        List <BufferedImage> bufferedFeets = new ArrayList <BufferedImage>();
        List <BufferedImage> bufferedShoot = new ArrayList <BufferedImage>();
        List <BufferedImage> bufferedReload = new ArrayList <BufferedImage>();
        walk = new ArrayList<Image>();
        shoot = new ArrayList<Image>();
        reload = new ArrayList<Image>();

        try{
            bufferedPlayer = ImageIO.read(new File("../images/Top_Down_Survivor/rifle/move/survivor-move_rifle_1.png"));
            for(int i = 0;i < 20;i++){
                bufferedFeets.add(ImageIO.read(new File("../images/Top_Down_Survivor/feet/walk/survivor-walk_" + i + ".png")));
            }
            for(int i = 0;i < 3;i++){
                bufferedShoot.add(ImageIO.read(new File("../images/Top_Down_Survivor/rifle/shoot/survivor-shoot_rifle_" + i +".png")));
            }
            for(int i = 0;i < 20;i++){
                bufferedReload.add(ImageIO.read(new File("../images/Top_Down_Survivor/rifle/reload/survivor-reload_rifle_" + i + ".png")));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        imagePlayer = bufferedPlayer.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        for(BufferedImage bufferedFeet : bufferedFeets){
            walk.add(bufferedFeet.getScaledInstance(width - 20, height - 20, Image.SCALE_SMOOTH));
        }
        for(BufferedImage bufferedS : bufferedShoot){
            shoot.add(bufferedS.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }
        for(BufferedImage bufferedR : bufferedReload){
            reload.add(bufferedR.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }

        nextImage = 0;
        nextImageShooting = 0;
        nextImageReloading = -1;
        walking = false;
        shooting = false;
        reloading = false;
        previousTime = System.currentTimeMillis();
        currentTime = 0;
        speed = 8;
        ammo = 50;
    }
    public int getPosX() {
        return pos_x;
    }

    public int getPosY(){
        return pos_y;
    }

    public void setPosX(int pos_x){
        this.pos_x = pos_x;
    }

    public void setPosY(int pos_y){
        this.pos_y = pos_y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public void walkRight(){
        pos_x += speed;
    }

    public void walkLeft(){
        pos_x -= speed;
    }

    public void walkUp(){
        pos_y -= speed;
    }

    public void walkDown(){
        pos_y += speed;
    }
    
    public boolean getWalking(){
        return walking;
    }

    public void setWalking(Boolean walking){
        this.walking = walking;
    }

    public int getNextImage(){
        return nextImage;
    }

    public void updateNextImage(){
        nextImage++;
        if(nextImage >= 20){
            nextImage = 0;
        }
    }

    public void shoot(){
        if(ammo > 0){
            ammo--;
        }
    }

    public int getAmmo(){
        return ammo;
    }

    public boolean getShooting(){
        return shooting;
    }

    public void setShooting(Boolean shooting){
        this.shooting = shooting;
    }

    public int getNextImageShooting(){
        return nextImageShooting;
    }

    public void updateNextImageShooting(){
        nextImageShooting++;
        if(nextImageShooting >= 3){
            nextImageShooting = 0;
        }
    }

    public void setMustReload(boolean mustReload){
        this.mustReload = mustReload;
    }

    public boolean getMustReload(){
        return mustReload;
    }

    public void reload(){
        ammo = 50;
    }

    public void setReloading(boolean reloading){
        this.reloading = reloading;
    }

    public boolean getReloading(){
        return reloading;
    }

    public int getNextImageReloading(){
        return nextImageReloading;
    }

    public void updateNextImageReloading(){
        nextImageReloading++;
        if(nextImageReloading >=20){
            nextImageReloading = -1;
        }
    }

    public void gettingAttacked(){
        currentTime = System.currentTimeMillis() - previousTime;
        if(currentTime >= attackTime){
            currentTime = 0;
            previousTime = System.currentTimeMillis();
            hp -= 10;
        }
    }

    public int getHP(){
        return hp;
    }

    public int getId(){
        return id;
    }
}