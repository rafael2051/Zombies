package game.logic.player;

import java.io.File;
import java.io.IOException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class BulletStandard {
    public Image bullet;
    private int width;
    private int height;

    public BulletStandard(int width, int height){
        BufferedImage bufferedBullet = null;
        try{
            bufferedBullet = ImageIO.read(new File("../images/Top_Down_Survivor/bullet.png"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        bullet = bufferedBullet.getScaledInstance(width, height, Image.SCALE_SMOOTH);

    }
}
