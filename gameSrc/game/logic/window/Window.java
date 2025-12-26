package game.logic.window;


import java.awt.Graphics;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import game.logic.player.Bullet;
import game.logic.player.BulletStandard;

import game.logic.player.Player;

import game.Keys;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.logic.zombie.Zombie;
import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.File;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import client.GameClient;

@SuppressWarnings("unchecked")
public class Window extends JFrame{
	public JPanel tela;
    public List<Player> players;
    public boolean[] controleTecla = new boolean[7];
	public List<Zombie> zombies; 
	public List<Bullet> bullets;
	public List<Bullet>[] bulletsFromOtherPlayers;
	private int fortressHP;
	private int damageTaken;

	private Image main_menu_start;
	private Image main_menu_exit;
	private Image current_menu_image;
	private Image wait_room_ready;
	private Image wait_room_not_ready;
	private Image current_wait_room_image;
	private int status_menu_image;
	private int status_wait_room_image;

	private Integer game_status;
	private Integer game_exit;

	private Image scenario;
	private Image death_image;

	private GameClient gameClient;	
    
    public Window(int width, int height){

        this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				setaTecla(e.getKeyCode(), false);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				setaTecla(e.getKeyCode(), true);
			}
		});

		this.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if(gameClient != null){
					closeGameClient(true);
				}
			}
		 });

        tela = new JPanel() {
            @Override
            public void paintComponent(Graphics g){

				if(game_status == 0){
					g.drawImage(current_menu_image, 0, 0, rootPane);
				}

				else if(game_status == 1){
					g.drawImage(current_wait_room_image, 0, 0, rootPane);
				}

				else if(players.
						stream().
						anyMatch(p -> p.getHP() > 0) 
						&& fortressHP > 0){
					g.drawImage(scenario, 0, 0, rootPane);
					
					for(Zombie zombie : zombies){
						if(zombie.getMustRender()){
							if(zombie.isDead()){
								if(zombie.isAboutTimeToRemove()){
									continue;
								}
								else{
									g.drawImage(zombie.zombieStandard.graveyard, 
												zombie.getPosX(),
												zombie.getPosY(), 
												rootPane);
								}
							}
							else if(zombie.getAttacking()){
								g.drawImage(zombie.zombieStandard.attack.get(zombie.getNextImageAttack()),
										zombie.getPosX(),
										zombie.getPosY(),
										rootPane);
							} 
							else{g.drawImage(zombie.zombieStandard.images.get(zombie.getNextImage()),
										zombie.getPosX(),
										zombie.getPosY(),
										rootPane);
							}
						}
					}
					for(Player player : players){
						g.setColor(Color.green);
						g.drawString("PLAYER_" + player.getId() + 1, player.getPosX(), player.getPosY() - 10);
						g.drawString("AMMO: " + player.getAmmo(), player.getPosX(), player.getPosY());
						g.drawString("HP: " + Integer.toString(player.getHP()), player.getPosX(), player.getPosY() + 10);
						if(player.getWalking()){
							g.drawImage(player.walk.get(player.getNextImage()),
										player.getPosX(),
										player.getPosY() + 20,
										rootPane);
						}
						if(player.getShooting() && player.getAmmo() > 0){
							g.drawImage(player.shoot.get(player.getNextImageShooting()),
										player.getPosX(),
										player.getPosY(),
										rootPane);
						}
						else if(player.getNextImageReloading() >= 0){
							player.updateNextImageReloading();
							g.drawImage(player.reload.get(player.getNextImageReloading()),
										player.getPosX(),
										player.getPosY(),
										rootPane);
						}
						else{
							g.drawImage(player.imagePlayer, player.getPosX(), player.getPosY(), rootPane);
						}
					}
					for(Bullet bullet : bullets){
						if(bullet.getMustRender()){
							g.drawImage(bullet.bulletStandard.bullet,
										bullet.getPosX(),
										bullet.getPosY(),
										rootPane);
						}
					}
					for(List<Bullet> listBullets : bulletsFromOtherPlayers) {
						for(Bullet bullet : listBullets) {
							if (bullet.getMustRender()) {
								g.drawImage(bullet.bulletStandard.bullet,
										bullet.getPosX(),
										bullet.getPosY(),
										rootPane);
							}

						}
					}
				}
				else if(game_status == 3){
					g.drawImage(death_image, 0, 0, rootPane);
				}
            }
        };

		BufferedImage buffered_main_menu_start = null;
		BufferedImage buffered_main_menu_exit = null;

		try{
			buffered_main_menu_start = ImageIO.read(new File("../images/main_menu/start.jpg"));
			buffered_main_menu_exit = ImageIO.read(new File("../images/main_menu/exit.jpg"));
		} catch(IOException e){
			System.out.println("ERROR!");
			e.printStackTrace();
		}

		main_menu_start = buffered_main_menu_start.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		main_menu_exit = buffered_main_menu_exit.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		current_menu_image = main_menu_start;

		BufferedImage bufferedImageScenario = null;
		BufferedImage bufferedImageDeath = null;

		try{
			bufferedImageScenario = ImageIO.read(new File("../images/scenario/floor_1.jpg"));
			bufferedImageDeath = ImageIO.read(new File("../images/scenario/death_image2.jpg"));
		}catch (IOException e){
			System.out.println("ERROR!");
			e.printStackTrace();
			System.exit(1);
		}

		scenario = bufferedImageScenario.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		death_image = bufferedImageDeath.getScaledInstance(width, height, Image.SCALE_SMOOTH);

		BufferedImage buffered_wait_room_ready = null;
		BufferedImage buffered_wait_room_not_ready = null;

		try{
			buffered_wait_room_ready = ImageIO.read(new File("../images/wait_room/Ready.jpg"));
			buffered_wait_room_not_ready = ImageIO.read(new File("../images/wait_room/NotReady.jpg"));
		} catch(IOException e){
			System.out.println("ERROR!");
			e.printStackTrace();
		}

		wait_room_ready = buffered_wait_room_ready.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		wait_room_not_ready = buffered_wait_room_not_ready.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		current_wait_room_image = wait_room_not_ready;

		getContentPane().add(tela);
		setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height);
		fortressHP = 100;
		damageTaken = 10;
		
        setVisible(true);
        players = new ArrayList<Player>();
		zombies = new ArrayList<Zombie>();
		bullets = new ArrayList<Bullet>();

		game_status = 0;
		status_menu_image = 0;
		status_wait_room_image = 0;
		game_exit = 0;
    }

    public void render(){
        repaint();
    }

    public void addPlayer(Player player){
        players.add(player);
    }

	public void addZombie(Zombie zombie){
		zombies.add(zombie);
	}

	public void initBulletsFromOtherPlayers(int noPlayers){
		bulletsFromOtherPlayers = new ArrayList[noPlayers];
		for(int i = 0;i < noPlayers;i++){
			bulletsFromOtherPlayers[i] = new ArrayList<>();
		}
	}

	public void addBullet(int pos_x, int pos_y, BulletStandard bulletStandard){
		bullets.add(new Bullet(pos_x, pos_y, bulletStandard));
	}

	public void moveBullets(){
		for(Bullet bullet : bullets){
			bullet.move();
		}
	}

	public void movimentaZumbi(){
		for(Zombie zumbi : zombies){
			zumbi.move();
		}
	}

	public void decreasesHP(){
		fortressHP -= damageTaken;
	}

	public Integer getStatus(){
		return game_status;
	}

	public Integer getFortressHP(){
		return fortressHP;
	}

	public void changeMenuImage(){
		if(controleTecla[Keys.DOWN] && status_menu_image == 0){
			current_menu_image = main_menu_exit;
			status_menu_image = 1;
		}
		else if(controleTecla[Keys.UP] && status_menu_image == 1){
			current_menu_image = main_menu_start;
			status_menu_image = 0;
		}
	}

	public void checkEnterPressed(){
		if(game_status == 0){
			if(controleTecla[Keys.ENTER] && status_menu_image == 0){
				game_status = 1;
			}
			else if(controleTecla[Keys.ENTER] && status_menu_image == 1){
				game_exit = 1;
			}
		} else if(game_status == 1){
			if(controleTecla[Keys.ENTER] && status_wait_room_image == 0){
				status_wait_room_image = 1;
				current_wait_room_image = wait_room_ready;
				gameClient.tellServerReady();
			}
		}
	}

	public void checkStartGame(){
		if(gameClient.getStartGame()){
			game_status = 2;
		}
	}

	public Integer getGameExit(){
		return game_exit;
	}

	public void finishGame(){
		game_status = 3;
	}

	public void clean(){
		players.clear();
		zombies.clear();
		bullets.clear();
	}

	public void setGameClient(GameClient gameClient){
		this.gameClient = gameClient;
	}

	public void closeGameClient(boolean mustClose){
		gameClient.closeClientSocket(mustClose);
	}
    private void setaTecla(int tecla, boolean pressionada) {
		switch (tecla) {
			case KeyEvent.VK_UP:
				// Seta para cima
				controleTecla[Keys.UP] = pressionada;
				break;
			case KeyEvent.VK_DOWN:
				// Seta para baixo
				controleTecla[Keys.DOWN] = pressionada;
				break;
			case KeyEvent.VK_LEFT:
				// Seta para esquerda
				controleTecla[Keys.LEFT] = pressionada;
				break;
			case KeyEvent.VK_RIGHT:
				// Seta para direita
				controleTecla[Keys.RIGHT] = pressionada;
				break;
			case KeyEvent.VK_S:
				// Atirar
				controleTecla[Keys.SHOOT] = pressionada;
				break;
			case KeyEvent.VK_R:
				// Carregar
				controleTecla[Keys.RELOAD] = pressionada;
				break;
			case KeyEvent.VK_ENTER:
				// Enter
				controleTecla[Keys.ENTER] = pressionada;
				break;
		}
	}
}
