package game.logic.zombie;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class ZombieStandard {
    public static List <Image> images;
    public static List <Image> attack;
    public static Image graveyard;
    protected static int width;
    protected static int height;
    public ZombieStandard(int width, int height){
        images = new ArrayList<Image>();
        attack = new ArrayList<Image>();
        List <BufferedImage> bufferedImages = new ArrayList<BufferedImage>();
        List <BufferedImage> bufferedImagesAttack = new ArrayList<BufferedImage>();
        BufferedImage bufferedImageGraveyard = null;
        try{
            for(int i = 0;i < 17;i++){
                bufferedImages.add(ImageIO.read(new File("../images/tds_zombie/export/move/skeleton-move_" + i + ".png"))); 
            }
            for(int i = 0;i < 13;i++){
                bufferedImagesAttack.add(ImageIO.read(new File("../images/tds_zombie/export/attack/skeleton-attack_" + i + ".png"))); 
            }
            bufferedImageGraveyard = ImageIO.read(new File("../images/tds_zombie/export/ripZombie.png"));
        }
        catch(IOException e){
            System.out.println("ERROR!");
            System.exit(1);
        }
        if(bufferedImages.size() == 0){
            System.out.println("ERROR!");
            System.exit(1);
        }
        for(BufferedImage bufferedImage : bufferedImages){
            images.add(bufferedImage.getScaledInstance(width, height, Image.SCALE_FAST));
        }
        for(BufferedImage bufferedImageAttack : bufferedImagesAttack){
            attack.add(bufferedImageAttack.getScaledInstance(width, height, Image.SCALE_FAST));
        }
        graveyard = bufferedImageGraveyard.getScaledInstance(width, height, Image.SCALE_FAST);
        this.width = width;
        this.height = height;
    }

    protected int getWidth(){
        return width;
    }

    protected int getHeight(){
        return height;
    }
}
