package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Rolf
 */
public class TowerDamageParticleEmitter extends AbstractControl{
    private static final float MAXLIFETIME = 2.0f;
    ParticleEmitter emitter;
    SimpleApplication sa;
    Node parent;
    private float time;
    Vector3f location;
    
    public TowerDamageParticleEmitter(SimpleApplication sa, Node parent, Vector3f location){
        this.sa = sa;
        this.parent = parent;
        this.location = location;
        init();
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        time+=tpf;
        if (time>MAXLIFETIME){
            emitter.removeControl(this);
            parent.detachChild(emitter);
	    
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }  
    
    public void init(){
        emitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 70);
        Material debris_mat = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        debris_mat.setTexture("Texture", sa.getAssetManager().loadTexture("Effects/Explosion/spark.png"));
        emitter.setMaterial(debris_mat);
        emitter.setImagesX(1);
        emitter.setImagesY(1); // 3x3 texture animation
        emitter.setRotateSpeed(0);
        emitter.setSelectRandomImage(true);
        emitter.setStartColor(ColorRGBA.Cyan);
        emitter.setEndColor(new ColorRGBA(0,0,0,0));
        emitter.setGravity(0,1, 0);
        emitter.getParticleInfluencer().setVelocityVariation(0.5f);
        emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 7, 0));
        emitter.setStartSize(0.5f);
        emitter.setEndSize(1f);
        emitter.setLowLife(0.5f);
        emitter.setHighLife(MAXLIFETIME);
        emitter.setParticlesPerSec(0);
        emitter.setLocalTranslation(location);
				emitter.setFacingVelocity(true);
        parent.attachChild(emitter);
        emitter.emitAllParticles();
        emitter.addControl(this);
    }
}



