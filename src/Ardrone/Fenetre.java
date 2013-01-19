package Ardrone;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class Fenetre extends JFrame {
	private static final long serialVersionUID = 1L;
	public VideoPanel video;
	
	public Fenetre (final ARDrone drone)
	{
		this.setTitle("ARDrone");
		this.setSize(1200, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.LINE_AXIS));
		
		NavPanel data = new NavPanel(drone);
		
		video = new VideoPanel();
		
		this.getContentPane().add(video);
		this.getContentPane().add(data);
		
		this.setVisible(true);
		
		new Thread(data).start();
	}
}
