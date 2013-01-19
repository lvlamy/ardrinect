package GUI;

import Ardrone.NavPanel;
import Ardrone.VideoPanel;
import UsersViewer3D.SkelsManager;
import UsersViewer3D.TrackerPanel3D;
import ViewerPanel.ViewerPanel;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.OpenNI.*;

import com.primesense.NITE.IdValueEventArgs;
import com.primesense.NITE.NullEventArgs;
import com.primesense.NITE.PointEventArgs;
import com.primesense.NITE.SessionManager;
import com.primesense.NITE.SteadyDetector;
import com.primesense.NITE.StringPointValueEventArgs;
import com.primesense.NITE.WaveDetector;

import controle.Controle;

import Ardrone.ARDrone;
import java.awt.Font;
import java.io.PrintStream;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import javax.swing.JCheckBox;

public class GUI implements Runnable{
	
	public JFrame frame;
	private volatile boolean isRunning;
	
	private Context context;
	private ImageGenerator imageGen;
	private DepthGenerator depthGen;
	private UserGenerator userGen;
	private SkelsManager skels;   // the skeletons manager
	private SessionManager sessionMan;
	private TrackerPanel3D tp3D;
	private ViewerPanel cameraView;
		
	private VideoPanel vpanel;
	private NavPanel pqdpanel;
	private ARDrone drone;
	
	private Controle controle;
	private JPanel jauges;
	private JProgressBar vitesseX;
	private JProgressBar vitesseY;
	private JProgressBar vitesseZ;
	private JProgressBar omegaY;
	private JCheckBox checkDem;
	private JCheckBox checkDeco;
	private JCheckBox checkInit;
	private JCheckBox checkWave;
	
	private PositionInfo pi = null;

	
	KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
	Action escapeAction = new AbstractAction() {
		@Override
	    public void actionPerformed(ActionEvent e) {
			try {
				drone.atterrir();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Au revoir");
			isRunning=false;
	        frame.dispose();
	        System.exit(1);
	    }
	}; 
	
	KeyStroke iKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_I, 0, false);
	Action iAction = new AbstractAction() {
		@Override
	    public void actionPerformed(ActionEvent e) {
       	 controle.init(skels);
	    }
	}; 
	
	KeyStroke spaceKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false);
	Action spaceAction = new AbstractAction() {
		@Override
	    public void actionPerformed(ActionEvent e) {
			try {
				drone.atterrir();
				} 
			catch (Exception e1) 
				{
				e1.printStackTrace();
				}
			System.out.println("Aterrissage");
	    }
	}; 

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
		new Thread(this).start();   // start updating the panel's image
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(0, 0,screen.width,screen.height - 30);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JTextArea consolePanel = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(consolePanel);
		Echo echo = new Echo(consolePanel, "");
		System.setOut(new PrintStream(echo));
		scrollPane.setBounds(10, 10, 680, 400);
		DefaultCaret caret = (DefaultCaret)consolePanel.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		frame.getContentPane().add(scrollPane);
		
		JCheckBox imgEClille = new JCheckBox();
		
		imgEClille.setBounds(30, 600, 210, 250);
		imgEClille.setIcon(new ImageIcon("LogoECLille.jpg"));
		imgEClille.setSelectedIcon(new ImageIcon("LogoECLille.jpg"));
		frame.getContentPane().add(imgEClille);

		
		jauges = new JPanel();
		jauges.setBounds(528, 527, 200, 512);
		frame.getContentPane().add(jauges);
		jauges.setLayout(null);
		
		
		vitesseX = new JProgressBar();
		vitesseX.setMinimum(-100);
		vitesseX.setMaximum(100);
		vitesseX.setToolTipText("Vx");
		vitesseX.setBounds(10, 10, 40, 200);
		jauges.add(vitesseX);
		vitesseX.setOrientation(SwingConstants.VERTICAL);
		
		vitesseY = new JProgressBar();
		vitesseY.setToolTipText("Vx");
		vitesseY.setOrientation(SwingConstants.VERTICAL);
		vitesseY.setMinimum(-100);
		vitesseY.setMaximum(100);
		vitesseY.setBounds(80, 10, 40, 200);
		jauges.add(vitesseY);
		
		vitesseZ = new JProgressBar();
		vitesseZ.setToolTipText("Vx");
		vitesseZ.setOrientation(SwingConstants.VERTICAL);
		vitesseZ.setMinimum(-100);
		vitesseZ.setMaximum(100);
		vitesseZ.setBounds(150, 10, 40, 200);
		jauges.add(vitesseZ);
		
		omegaY = new JProgressBar();
		omegaY.setToolTipText("OmegaY");
		omegaY.setMinimum(-100);
		vitesseZ.setMaximum(100);
		omegaY.setBounds(25, 360, 150, 30);
		jauges.add(omegaY);
		
		JLabel Vx = new JLabel("Vx");
		Vx.setFont(new Font("Tahoma", Font.BOLD, 15));
		Vx.setHorizontalAlignment(SwingConstants.CENTER);
		Vx.setBounds(15, 210, 30, 30);
		jauges.add(Vx);
		
		JLabel Vz = new JLabel("Vz");
		Vz.setFont(new Font("Tahoma", Font.BOLD, 15));
		Vz.setHorizontalAlignment(SwingConstants.CENTER);
		Vz.setBounds(155, 210, 30, 30);
		jauges.add(Vz);
		
		JLabel Vy = new JLabel("Vy");
		Vy.setFont(new Font("Tahoma", Font.BOLD, 15));
		Vy.setHorizontalAlignment(SwingConstants.CENTER);
		Vy.setBounds(85, 210, 30, 30);
		jauges.add(Vy);
		
		JLabel Oy = new JLabel("\u03A9y");
		Oy.setHorizontalAlignment(SwingConstants.CENTER);
		Oy.setFont(new Font("Tahoma", Font.BOLD, 15));
		Oy.setBounds(85, 400, 30, 30);
		jauges.add(Oy);
		
		vpanel = new VideoPanel();
		configOpenNI();
		configDrone();
		controle=new Controle(drone);
		pqdpanel = new NavPanel(drone);
		new Thread(pqdpanel).start();
	
		vpanel.setBounds(700, 11, 650, 400);
		pqdpanel.setBounds(1360, 11, 550, 400);
		
		
		cameraView = new ViewerPanel(context, imageGen);
		cameraView.setBounds(1270, 559, 640, 480);
			
		tp3D.setBounds(738, 527, 512, 512);
		
		frame.getContentPane().add(vpanel);
		frame.getContentPane().add(pqdpanel);
		frame.getContentPane().add(tp3D);
		frame.getContentPane().add(cameraView);
		
		checkInit = new JCheckBox("Initialis\u00E9");
		checkInit.setFont(new Font("Tahoma", Font.BOLD, 16));
		checkInit.setBounds(300, 750, 150, 40);
		checkInit.setIcon(new ImageIcon("Red Ball.png"));
		checkInit.setSelectedIcon(new ImageIcon("Clear Green Button.png"));
		frame.getContentPane().add(checkInit);
		
		checkWave = new JCheckBox("Wave");
		checkWave.setFont(new Font("Tahoma", Font.BOLD, 16));
		checkWave.setBounds(300, 650, 150, 40);
		checkWave.setIcon(new ImageIcon("Red Ball.png"));
		checkWave.setSelectedIcon(new ImageIcon("Clear Green Button.png"));
		frame.getContentPane().add(checkWave);
		
		checkDem = new JCheckBox("D\u00E9marr\u00E9");
		checkDem.setFont(new Font("Tahoma", Font.BOLD, 16));
		checkDem.setBounds(300, 550, 150, 40);
		checkDem.setIcon(new ImageIcon("Red Ball.png"));
		checkDem.setSelectedIcon(new ImageIcon("Clear Green Button.png"));
		frame.getContentPane().add(checkDem);
		
		checkDeco = new JCheckBox("D\u00E9coll\u00E9");
		checkDeco.setFont(new Font("Tahoma", Font.BOLD, 16));
		checkDeco.setBounds(300, 850, 150, 40);
		checkDeco.setIcon(new ImageIcon("Red Ball.png"));
		checkDeco.setSelectedIcon(new ImageIcon("Clear Green Button.png"));
		frame.getContentPane().add(checkDeco);
		

				
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		frame.getRootPane().getActionMap().put("ESCAPE", escapeAction);	
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(iKeyStroke, "I");
		frame.getRootPane().getActionMap().put("I", iAction);	
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(spaceKeyStroke, "SPACE");
		frame.getRootPane().getActionMap().put("SPACE", spaceAction);	
		
		checkDem.setSelected(true);
		imgEClille.setSelected(true);
		imgEClille.repaint();
	

		
	}
	
	private void configDrone()
	  {
	try {
		drone = new ARDrone(vpanel);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	try {
		drone.init();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  }  
	
	private void configOpenNI()
	  // create context, user generator, and skeletons
	  {
	    try {
	      context = new Context();
	      
	      // add the NITE Licence 
	      License licence = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");
	      context.addLicense(licence); 
	      
	      imageGen = ImageGenerator.create(context);
	      depthGen = DepthGenerator.create(context);
	      userGen = UserGenerator.create(context);
	      
	      HandsGenerator handsGen = HandsGenerator.create(context);    // OpenNI
	      handsGen.SetSmoothing(0.1f);
	               // 0-1: 0 means no smoothing, 1 means 'infinite'
	      setHandEvents(handsGen);

	      GestureGenerator gestureGen = GestureGenerator.create(context);     // OpenNI
	      setGestureEvents(gestureGen);
	      
	      MapOutputMode mapMode = new MapOutputMode(640, 480, 30);   // xRes, yRes, FPS
	      imageGen.setMapOutputMode(mapMode); 
	      imageGen.setPixelFormat(PixelFormat.RGB24);
	      
	      context.setGlobalMirror(true);         // set mirror mode
	      tp3D = new TrackerPanel3D(context, userGen);
	      skels = new SkelsManager(userGen, tp3D.sceneBG);
	      tp3D.setSkelMan(skels);
	      

	      context.startGeneratingAll(); 
	      System.out.println("Debut generation objets Kinect"); 
	      sessionMan = new SessionManager(context, "Wave", "RaiseHand");   // NITE
          // main focus gesture(s), quick refocus gesture(s)  
	      setSessionEvents(sessionMan);
	      SteadyDetector sdd  = initSteadyDetector(); 
	      sessionMan.addListener(sdd);
	      //WaveDetector wd = initWaveDetector();
	      //sessionMan.addListener(wd);
	    } 
	    catch (Exception e) {
	      System.out.println(e);
	      System.exit(1);
	    }
	  }  // end of configOpenNI()
	
	public void run()
	  /* update and display the userGenerator and skeletons
	     whenever the context is updated.
	  */
	  {
	    isRunning = true;
	    while (isRunning) {
	      try {
	        context.waitAnyUpdateAll();   
	        sessionMan.update(context);
	      }
	      catch(StatusException e)
	      {  System.out.println(e); 
	         System.exit(1);
	      }
	      skels.update();   // get the skeletons manager to carry out the updates
	      cameraView.updateImage();
	      cameraView.repaint();
	      controle.update(skels);
	      vitesseX.setValue((int) (controle.x*100));
	      vitesseY.setValue((int) (controle.y*100));
	      vitesseZ.setValue((int) (controle.z*100));
	      omegaY.setValue((int) (controle.omegaY*100));
	      if (controle.initialized) checkInit.setSelected(true);
	      if (drone.enVol()) checkDeco.setSelected(true);
	      try {
			if (sessionMan.IsInSession()) checkWave.setSelected(true);
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      checkDeco.repaint();
	      checkInit.repaint();
	      checkWave.repaint();
	      jauges.repaint();
	    }
	    // close down
	    try {
	      context.stopGeneratingAll();
	    }
	    catch (StatusException e) {}
	    context.release();
	    System.exit(0);
	  }  // end of run()
	
	 private void setHandEvents(HandsGenerator handsGen)
	  // create HandsGenerator callbacks
	  {
	    try {
	      // when hand is created
	      handsGen.getHandCreateEvent().addObserver( 
	                          new IObserver<ActiveHandEventArgs>() {
	        public void update(IObservable<ActiveHandEventArgs> observable, 
	                           ActiveHandEventArgs args)
	        { 
	          int id = args.getId();
	          Point3D pt = args.getPosition();
	          float time = args.getTime();
	          System.out.printf("Main %d localisée à (%.0f, %.0f, %.0f), à %.0f secondes\n", 
	              id, pt.getX(), pt.getY(), pt.getZ(), time);
	        }
	      });

	      // when hand is destroyed
	      handsGen.getHandDestroyEvent().addObserver( 
	                          new IObserver<InactiveHandEventArgs>() {
	        public void update(IObservable<InactiveHandEventArgs> observable, 
	                           InactiveHandEventArgs args)
	        { 
	          int id = args.getId();
	          float time = args.getTime();
	          System.out.printf("Main %d détruite à %.0f secs \n", id, time);
	        }
	      });
	    }
	    catch (StatusException e) {
	      e.printStackTrace();
	    }
	  }  // end of setHandEvents()



	  private void setGestureEvents(GestureGenerator gestureGen)
	  // create GestureGenerator callback
	  {
	    try {
	      // when gesture is recognized
	      gestureGen.getGestureRecognizedEvent().addObserver( 
	                          new IObserver<GestureRecognizedEventArgs>() {
	        public void update(IObservable<GestureRecognizedEventArgs> observable, 
	                           GestureRecognizedEventArgs args)
	        { 
	          String gestureName = args.getGesture();
	          Point3D idPt = args.getIdPosition();
	             // hand position when gesture was identified
	          Point3D endPt = args.getEndPosition();
	              // hand position at the end of the gesture 
	          System.out.printf("Geste \"%s\" reconnu à (%.0f, %.0f, %.0f); fini à (%.0f, %.0f, %.0f)\n", 
	              gestureName, idPt.getX(), idPt.getY(), idPt.getZ(), 
	                           endPt.getX(), endPt.getY(), endPt.getZ() );
	        }
	      });
	    }
	    catch (StatusException e) {
	      e.printStackTrace();
	    }
	  }  // end of setGestureEvents()



	  private void setSessionEvents(SessionManager sessionMan)
	  // create session callbacks
	  {
	    try {
	      // when a focus gesture has started to be recognized 
	      sessionMan.getSessionFocusProgressEvent().addObserver( 
	                          new IObserver<StringPointValueEventArgs>() {
	        public void update(IObservable<StringPointValueEventArgs> observable, 
	                           StringPointValueEventArgs args)
	        { 
	          Point3D focusPt = args.getPoint();
	          float progress = args.getValue();
	          String focusName = args.getName();
	          System.out.printf("Focus à (%.0f, %.0f, %.0f) sur %s [progress %.2f]\n", 
	               focusPt.getX(), focusPt.getY(), focusPt.getZ(), focusName, progress);
	        }
	      });
	      
	      // session started
	      sessionMan.getSessionStartEvent().addObserver( new IObserver<PointEventArgs>() {
	        public void update(IObservable<PointEventArgs> observable, PointEventArgs args)
	        { 
	          Point3D focusPt = args.getPoint();
	          System.out.printf("Session démarrée à (%.0f, %.0f, %.0f)\n", 
	                                        focusPt.getX(), focusPt.getY(), focusPt.getZ());
	        }
	      });

	      // session end
	      sessionMan.getSessionEndEvent().addObserver( new IObserver<NullEventArgs>() {
	        public void update(IObservable<NullEventArgs> observable, NullEventArgs args)
	        { 
	           System.out.println("Session finie");
	           isRunning = false;    // causes loop in constructor to end, so program exits
	        }
	      });
	    }
	    catch (StatusException e) {
	      e.printStackTrace();
	    }
	  }  // end of setSessionEvents()
	  /*
	  private WaveDetector initWaveDetector()
	  {
	    WaveDetector waveDetector = null;
	    try {
	      waveDetector = new WaveDetector();

	      // some wave settings; change with set
	      int flipCount = waveDetector.getFlipCount();
	      int flipLen = waveDetector.getMinLength();
	      System.out.println("Param Wave -- n° de passage : " + flipCount +
	                               "; longeur minimale : " + flipLen + "mm");  

	      
	      
	      // callback
	      waveDetector.getWaveEvent().addObserver( new IObserver<NullEventArgs>() {
	        public void update(IObservable<NullEventArgs> observable, NullEventArgs args)
	        { 
	          System.out.println("Wave detectee");  
	          System.out.println("  " + pi);  // show current hand point 
	          try {
				if(controle.initialized && !drone.enVol())
					{drone.decoller();
			          System.out.println("Decollage");
					}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        }
	      });
	    }
	    catch (GeneralException e) {
	      e.printStackTrace();
	    }
	    return waveDetector;
	  }  // end of initWaveDetector()*/
	  
	  private SteadyDetector initSteadyDetector()
	  {
	    SteadyDetector steadyDetector = null;
	    try {
	      steadyDetector = new SteadyDetector();
	      steadyDetector.setDetectionDuration(5000);
	      System.out.println("Immobile paramêtres -- durée mini : " +
	                     steadyDetector.getDetectionDuration() + " ms");
	      System.out.printf("                   mouvement maxi: %.3f mm\n", 
	                        steadyDetector.getMaxDeviationForSteady());

	      // callback
	      steadyDetector.getSteadyEvent().addObserver( new IObserver<IdValueEventArgs>() {
	        public void update(IObservable<IdValueEventArgs> observable, 
	                           IdValueEventArgs args)
	        { 
	          System.out.printf("Main %d immobile : mouvement %.3f\n", 
	                                       args.getId(), args.getValue());  
	          //System.out.println("  " + pi);  // show current hand point 
	          controle.init(skels);
	        }
	      });
	    }
	    catch (GeneralException e) {
	      e.printStackTrace();
	    }
	    return steadyDetector;
	  }  // end of initSteadyDetector()
}



