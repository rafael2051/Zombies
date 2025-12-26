package game.threads;

import game.apis.ApiPlayerClient;
import game.logic.player.Bullet;
import game.logic.player.BulletStandard;

import game.logic.window.Window;

import game.logic.player.Player;
import game.logic.zombie.Zombie;

import java.util.List;

public class ControlOtherPlayersTask implements Runnable {
    private Window window;
    private BulletStandard bulletStandard;
    private ApiPlayerClient apiPlayerClient;
    private Player player;
    private List<Bullet> listBullets;

    public ControlOtherPlayersTask(Window window, BulletStandard bulletStandard,
                                     Player player) {
        this.window = window;
        this.bulletStandard = bulletStandard;
        apiPlayerClient = ApiPlayerClient.getInstance();
        this.player = player;
        listBullets = window.bulletsFromOtherPlayers[player.getId()];
    }

    @Override
    public void run() {
        while (true) {
            if (apiPlayerClient.getThereIsMessage(player.getId())) {
                String msg = apiPlayerClient.getMessage(player.getId());
                apiPlayerClient.setThereIsMessage(false, player.getId());
                String[] parametersMessage = msg.split(";");
                if (parametersMessage[1].equals("donothing")) {
                    player.setWalking(false);
                }

                if (parametersMessage[1].equals("walk")) {
                    System.out.println(msg);
                    if (parametersMessage[2].equals("up") &&
                            player.getPosY() > 20) {
                        player.walkUp();
                        player.setWalking(true);
                        player.updateNextImage();
                    } else if (parametersMessage[2].equals("down") &&
                            player.getPosY() < window.getHeight() - 130) {
                        player.walkDown();
                        player.setWalking(true);
                        player.updateNextImage();
                    } else if (parametersMessage[2].equals("left") &&
                            player.getPosY() > 0) {
                        player.walkLeft();
                        player.setWalking(true);
                        player.updateNextImage();
                    } else if (parametersMessage[2].equals("right") &&
                            player.getPosX() < window.getWidth() - 100) {
                        player.walkRight();
                        player.setWalking(true);
                        player.updateNextImage();
                    }
                    if (parametersMessage.length == 6) {
                        try {
                            int realPosX = Integer.parseInt(parametersMessage[3]);
                            int realPosY = Integer.parseInt(parametersMessage[4]);
                            if (player.getPosX() != realPosX || player.getPosY() != realPosY) {
                                player.setPosX(realPosX);
                                player.setPosY(realPosY);
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (parametersMessage[1].equals("shoot")) {
                    System.out.println(msg);
                    player.setShooting(true);
                    player.setReloading(false);
                    player.updateNextImageShooting();
                    player.shoot();
                    listBullets.add(new Bullet(player.getPosX() + 74, player.getPosY() + 59,
                            bulletStandard));
                } else if (parametersMessage[1].equals("reload")) {
                    player.setReloading(true);
                    player.setShooting(false);
                    player.reload();
                    player.updateNextImageReloading();
                }
            }
            try{
                for (Bullet bullet : listBullets) {
                    boolean collision = false;
                    for (Zombie zombie : window.zombies) {
                        if (bullet.getPosX() >= zombie.getPosX() &&
                                bullet.getPosX() <= zombie.getPosX() + zombie.getHeight() &&
                                bullet.getPosY() >= zombie.getPosY() + 20 &&
                                bullet.getPosY() <= zombie.getPosY() + zombie.getHeight() - 20 &&
                                !zombie.isDead()) {
                            if (bullet.getMustRender()) {
                                zombie.gettingShooted();
                            }
                            bullet.setMustRender(false);
                            if (zombie.getHP() <= 0) {
                                zombie.kill();
                            }
                            collision = true;
                            break;
                        }
                    }
                    if (!collision) {
                        bullet.move();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
