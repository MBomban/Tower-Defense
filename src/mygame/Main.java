package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import de.lessvoid.nifty.Nifty;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

  private static Dimension screen;
  BitmapText stateInfoText;
  private NiftyJmeDisplay niftyDisplay;
  private Nifty nifty;
  public ArrayList<Tower> towers;
  public ArrayList<Enemy> enemies;
  public int enemyCount = 0;
  public  String difficulty = "";
  public  float diff=1;
  public float roundDifficultyIncr=0;
  
  public AudioNode mainSong, explosion,laserSound,demolish;
  public int roundNum = 0;
  /**
   * Physics for collision
   */
  public BulletAppState bulletAppState;
  public Tower homeTower;
  public int health = 100;
  public int bit = 0;
  public MainTower mainTower;
  Material whiteGlow,TrojanGlow;
  Material laserGlow, redGlow;

  public static void main(String[] args) {
    Main app = new Main();
    initAppScreen(app);
    app.start();
  }
  Spatial spikeBombSpatial, spywareTowerSpatial, AntiTrojanSpatial;
  
  public void InitializeDifficulty(){
      if (difficulty.equals("easy")){
          diff=1;
      }else if(difficulty.equals("medium")){
           diff=(float) 1.3;
      }else{
           diff=(float) 1.5; 
      }
      
      
  }
  
  @Override
  public void simpleInitApp() {

    initMaterials();
    initLights();
    initModels();
    initGui();
    initAudio();
    InitializeDifficulty();

    //MainTower
    initCrossHairs();
    /**
     * Set up Physics Game
     */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);


    StartScreenState startScreen = new StartScreenState();
    stateManager.attach(startScreen);

//				TestState testState = new TestState();
//        stateManager.attach(testState);
  }

  private void initMaterials() {

    //glow for enemies
    whiteGlow = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    whiteGlow.setColor("Color", ColorRGBA.Cyan);
    whiteGlow.setColor("GlowColor", ColorRGBA.White);
    TrojanGlow = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    TrojanGlow.setColor("Color", ColorRGBA.Yellow);
    TrojanGlow.setColor("GlowColor", ColorRGBA.Red);
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
    bloom.setBloomIntensity(3.0f);
    bloom.setBlurScale(7.0f);
    fpp.addFilter(bloom);
    viewPort.addProcessor(fpp);
    
     //glow for enemies
    redGlow = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    ColorRGBA red = ColorRGBA.Red.clone();
    red.a = 0.5f;
    redGlow.setColor("Color", ColorRGBA.White);
    redGlow.setColor("GlowColor", red);

    //glow for laser beams
    laserGlow = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    laserGlow.setColor("Color", ColorRGBA.Red);
    laserGlow.setColor("GlowColor", ColorRGBA.Red);


  }

  private void initLights() {
  }
  
  public void initAudio(){
     /* nature sound - keeps playing in a loop. */
    mainSong = new AudioNode(assetManager, "Audio/tronMusic.wav", false);
    mainSong.setLooping(true);  // activate continuous playing
    mainSong.setPositional(false);   
    mainSong.setVolume(3);
    rootNode.attachChild(mainSong);
    mainSong.play(); // play continuously!
    
    //action triggered sounds 
    explosion = new AudioNode(assetManager, "Audio/explosion_x.wav",false);
    explosion.setPositional(false);
    explosion.setLooping(false);
    explosion.setVolume(2);
    rootNode.attachChild(explosion);
    
    //laser shots
    laserSound = new AudioNode(assetManager, "Audio/phasers3.wav",false);
    laserSound.setPositional(false);
    laserSound.setLooping(false);
    laserSound.setVolume(2);
    rootNode.attachChild(laserSound);
    
    //tower demolish
     demolish = new AudioNode(assetManager, "Audio/aoogah.wav",false);
    demolish.setPositional(false);
    demolish.setLooping(false);
    demolish.setVolume(2);
    rootNode.attachChild(laserSound);
    
    
    
  }

  private void initModels() {

    //initialize towers and enemies arrays
    towers = new ArrayList<Tower>();
    enemies = new ArrayList<Enemy>();

    //attach skybox
    Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/BlueClouds.dds", false);
    rootNode.attachChild(sky);
    
    //model for the spyware enemy
    spikeBombSpatial = this.assetManager.loadModel("Models/spikeBomb/spikeBomb4.j3o");
    TangentBinormalGenerator.generate(spikeBombSpatial);
    
    spywareTowerSpatial = this.assetManager.loadModel("Models/spywareTower/spywareTower.j3o");
    TangentBinormalGenerator.generate(spikeBombSpatial);
    
    //model for AntiTrojan
    AntiTrojanSpatial = this.assetManager.loadModel("Models/AntiTrojan/untitled.j3o");
    TangentBinormalGenerator.generate(AntiTrojanSpatial);
    

  }
  
  public void initBG(){
      Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/BlueClouds.dds", false);
    rootNode.attachChild(sky);
  }

  private void initGui() {
    //create the state info break in the top right
    BitmapFont bmf = this.getAssetManager().loadFont("Interface/Fonts/ArialBlack.fnt");
    stateInfoText = new BitmapText(bmf);
    stateInfoText.setSize(bmf.getCharSet().getRenderedSize() * 1f);
    stateInfoText.setColor(ColorRGBA.White);
    stateInfoText.setText("");
    stateInfoText.setLocalTranslation(10f, this.getAppSettings().getHeight() - 50, 0f);
    this.getGuiNode().attachChild(stateInfoText);

    niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
    nifty = niftyDisplay.getNifty();
  }

  public Dimension getScreenDimension() {
    return this.screen;
  }

  public AppSettings getAppSettings() {
    return this.settings;
  }

  public Nifty getNifty() {
    return this.nifty;
  }

  public NiftyJmeDisplay getNiftyDisplay() {
    return this.niftyDisplay;
  }

  public InputManager getInputManager() {
    return this.inputManager;
  }

  private static void initAppScreen(SimpleApplication app) {
    //set screen settings 
    AppSettings aps = new AppSettings(true);
    screen = Toolkit.getDefaultToolkit().getScreenSize();
    screen.width *= 0.80;
    screen.height *= 0.75;
    aps.setResolution(screen.width, screen.height);
    app.setSettings(aps);

    //get rid of initial jmonkey screen
    app.setShowSettings(false);


  }

  public void deleteInputMappings(String mappings[]) {
    for (String i : mappings) {
      inputManager.deleteMapping(i);
    }
  }

  public boolean hasObject(LinkedList<?> sp, Class cls) {
    for (Object s : sp) {
      if (s.getClass().equals(cls)) {
        return true;
      }
    }
    return false;
  }

  //for mainTower crosshair
  public void initCrossHairs() {
    guiNode.detachAllChildren();
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
    ch.setText("+");
    ch.setLocalTranslation( // center
            settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
            settings.getHeight() / 2 + ch.getLineHeight() / 2 + -25, 0);
    guiNode.attachChild(ch);
  }
}
