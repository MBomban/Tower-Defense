/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author adeut_000
 */
public class Tower extends Node {

		Main main;
		public float hp = 100;
		private boolean alive = true;
		Laser towerLaser;
		
		
    public Tower(Main main) {
        this.main = main;
				float height = 2;
        Box b = new Box(.5f, height, .5f);
        Geometry g = new Geometry("TowerGeo", b);
        
        Material mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        g.setMaterial(mat);
				this.towerLaser = new Laser(main.getAssetManager(), main);
				this.attachChild((Node)towerLaser);
				this.addControl(new TowerControl(this));
        this.attachChild(g);
				this.move(0, height, 0);
    }
		
		public void receiveDamage(float dmg)
		{
				
				new TowerDamageParticleEmitter((SimpleApplication)main, this, Vector3f.ZERO);
				//System.out.println("Ow!, I took " + dmg + "damage!");
				if((this.hp -= dmg) <= 0)
				{
						System.out.println("Oh no! I have died...");
						die();
						alive = false;
				}
		}
		
		public boolean isAlive()
		{
				return alive;
		}
		
		//death animation
		public void die()
		{
				new TowerDamageParticleEmitter((SimpleApplication)main, this, Vector3f.ZERO);
		}
		
		
		
		public class TowerControl extends AbstractControl{

				Tower tower;
				Enemy target;
				
				//how far away the towers can attack
				private static final float ATTACK_RANGE = 10f;
				//how often they attack
				private float ATTACK_COOLDOWN = 2f;
				//how much damage the towers do
				private float ATTACK_DMG = 40f;
				private float attackTimer = 0;
				private float time = 0;
				private int state;
				private boolean haveTarget = false;
				
				public TowerControl(Tower tower)
				{
						this.tower = tower;
				}
				
				@Override
				protected void controlUpdate(float tpf) {
						
						//check if our target has died
						if(haveTarget && !target.isAlive())
						{
								haveTarget = false;
								findTarget();
								attackTimer = 0;
						}
						
						//if our target is alive check if we can attack them
						if(haveTarget && tower.isAlive())
						{
								attackTimer+=tpf;
								if(attackTimer >= ATTACK_COOLDOWN)
								{
										//System.out.println("tower attacking enemy!");
										towerLaser.shoot(target.getLocalTranslation()
														.subtract(tower.getWorldTranslation()));
										target.receiveDamage(ATTACK_DMG);
										attackTimer = 0;
								}		
						}else{
								findTarget();
						}
						
						
						//if tower is dead keep moving it down
						if(!tower.isAlive())
								tower.move(0, -tpf, 0);
						
						//update the laser tower
						tower.towerLaser.update(tpf);
				}

				@Override
				protected void controlRender(RenderManager rm, ViewPort vp) {
						
				}
				
				//this method will find the closest tower to this enemy node 
				//and set it as its target. If this enemy currently has a target 
				//it will change its target if it finds a closer tower. 
				void findTarget() {
					float distTo, minDist;
					Enemy newTarget;

					//if no current target find initial target
					if (target == null || !target.isAlive()) {
						minDist = Float.MAX_VALUE;
						newTarget = null;
					} else {
						minDist = target.getLocalTranslation()
										.distance(tower.getLocalTranslation());
						newTarget = target;
					}

					//look for closer target
					for (Enemy e : main.enemies) {

						if(e.isAlive())
						{
								//get the distance to the current tower under consideration
								distTo = e.getWorldTranslation()
												.distance(tower.getWorldTranslation());

								//check to see if it is closer than the closest tower
								//we current have
								if ( (distTo < minDist) && (distTo < ATTACK_RANGE) ) {
									newTarget = e;
									minDist = distTo;
								}
						}

					}

					if (newTarget != null) {
							//System.out.println("found new target!");
						target = newTarget;
						haveTarget = true;
					}
				}
				
		}
}
