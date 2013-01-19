package Ardrone;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NavPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private ARDrone drone;
	private JTextField last,state,fly,flagcmd,batField,thetaField,phiField,psiField,altField,vxField,vyField;
	
	public NavPanel(final ARDrone drone)
	{
		this.drone = drone;
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setPreferredSize(new Dimension(550,400));
		
		JPanel lastPan = new JPanel();
		lastPan.add(new JLabel("Dernière commande"));
		last = new JTextField();
		last.setEditable(false);
		last.setPreferredSize(new Dimension(250,25));
		lastPan.add(last);
		
		JPanel statePan = new JPanel();
		statePan.add(new JLabel("State"));
		state = new JTextField();
		state.setEditable(false);
		state.setPreferredSize(new Dimension(250,25));
		//fly.setText("Test");
		statePan.add(state);
		
		JPanel statesPan = new JPanel();
		JPanel statesPan2 = new JPanel();
		statesPan.add(new JLabel("Battery"));
		batField = new JTextField();
		batField.setEditable(false);
		batField.setPreferredSize(new Dimension(50,25));
		statesPan.add(batField);
		
		statesPan.add(new JLabel("Theta"));
		thetaField = new JTextField();
		thetaField.setEditable(false);
		thetaField.setPreferredSize(new Dimension(50,25));
		statesPan.add(thetaField);
		
		statesPan.add(new JLabel("Phi"));
		phiField = new JTextField();
		phiField.setEditable(false);
		phiField.setPreferredSize(new Dimension(50,25));
		statesPan.add(phiField);
		
		statesPan.add(new JLabel("Psi"));
		psiField = new JTextField();
		psiField.setEditable(false);
		psiField.setPreferredSize(new Dimension(50,25));
		statesPan.add(psiField);
		
		statesPan.add(new JLabel("Altitude"));
		altField = new JTextField();
		altField.setEditable(false);
		altField.setPreferredSize(new Dimension(50,25));
		statesPan.add(altField);
		
		statesPan2.add(new JLabel("Vitesse x"));
		vxField = new JTextField();
		vxField.setEditable(false);
		vxField.setPreferredSize(new Dimension(50,25));
		statesPan2.add(vxField);
		
		statesPan2.add(new JLabel("Vitesse y"));
		vyField = new JTextField();
		vyField.setEditable(false);
		vyField.setPreferredSize(new Dimension(50,25));
		statesPan2.add(vyField);
		
		JPanel flagcmdPan = new JPanel();
		flagcmdPan.add(new JLabel("Flag cmd"));
		flagcmd = new JTextField();
		flagcmd.setEditable(false);
		flagcmd.setPreferredSize(new Dimension(250,25));
		//fly.setText("Test");
		flagcmdPan.add(flagcmd);
		
		JPanel flyPan = new JPanel();
		flyPan.add(new JLabel("Flying"));
		fly = new JTextField();
		fly.setEditable(false);
		fly.setPreferredSize(new Dimension(250,25));
		//fly.setText("Test");
		flyPan.add(fly);
		
		JPanel boutonsPan = new JPanel();
		JButton bDecoller = new JButton("Décoller");
		bDecoller.addActionListener(new ActionListener ()
		{
			public void actionPerformed (ActionEvent e)
			{
				try {
					drone.decoller();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton bAtterrir = new JButton("Atterrir");
		bAtterrir.addActionListener(new ActionListener ()
		{
			public void actionPerformed (ActionEvent e)
			{
				try {
					drone.atterrir();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton bUrgence = new JButton("Urgence");
		bUrgence.addActionListener(new ActionListener ()
		{
			public void actionPerformed (ActionEvent e)
			{
				try {
					drone.emergency();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton bUp = new JButton("Monter");
		bUp.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent e)
			{
				try {
					drone.deplacement(0f, 0f, 0.1f, 0f);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton bDown = new JButton("Descendre");
		bDown.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent e)
			{
				try {
					drone.deplacement(0f, 0f, -0.1f, 0f);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JButton bStat = new JButton("Stationnaire");
		bStat.addActionListener(new ActionListener()
		{
			public void actionPerformed (ActionEvent e)
			{
				try {
					drone.stationnaire();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		boutonsPan.add(bDecoller);
		boutonsPan.add(bAtterrir);
		boutonsPan.add(bUrgence);
		boutonsPan.add(bUp);
		boutonsPan.add(bDown);
		boutonsPan.add(bStat);
		
		this.add(lastPan);
		this.add(statePan);
		this.add(statesPan);
		this.add(statesPan2);
		this.add(flagcmdPan);
		this.add(flyPan);
		this.add(boutonsPan);
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			if (drone.navdata.recu)
			{
				last.setText(this.drone.dernierCmd);
				state.setText(this.drone.navdata.getState());
				batField.setText(this.drone.navdata.getBattery()+"%");
				thetaField.setText(""+this.drone.navdata.getTheta());
				phiField.setText(""+this.drone.navdata.getPhi());
				psiField.setText(""+this.drone.navdata.getPsi());
				altField.setText(""+this.drone.navdata.getAlt());
				vxField.setText(""+this.drone.navdata.getVx());
				vyField.setText(""+this.drone.navdata.getVy());
				flagcmd.setText(this.drone.navdata.flagCmd() ? "Levé" : "Baissé");
				fly.setText(this.drone.navdata.flying() ? "En vol" : "À terre");
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
