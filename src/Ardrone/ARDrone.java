package Ardrone;

import java.net.*;
import java.util.*;

public class ARDrone {
	// L'ip est publique : on peut la changer entre deux connexions ...
	public String ip = "192.168.1.1";
	public Navdata navdata = new Navdata();
	public String dernierCmd = "";
	private InetAddress inet_addr;
	private DatagramSocket socketCmd;
	public DatagramSocket socketNavdata;
	private Socket socketVideo;
	public int seq = 1;
	public VideoPanel vpanel;

	public ARDrone (VideoPanel vpanel) throws Exception
	{
		this.vpanel=vpanel;
	}
	
	public void init () throws Exception
	{
		System.out.println("Initialisation ARDrone ...");
		
		// On réécrit l'ip dans un objet InetAddress
		StringTokenizer st = new StringTokenizer(ip, ".");
		byte[] ip_bytes = new byte[4];
		for (int i = 0; i < 4; i++)
		{
			ip_bytes[i] = (byte)Integer.parseInt(st.nextToken());
		}
		inet_addr = InetAddress.getByAddress(ip_bytes);

		// Init sockets
		socketCmd = new DatagramSocket(5556);
		socketCmd.setSoTimeout(3000);
		socketNavdata = new DatagramSocket(5554);
		socketNavdata.setSoTimeout(3000);
		socketVideo = new Socket(inet_addr, 5555);
		socketVideo.setSoTimeout(3000);

		// Initialisation de la réception des navdata (cf p.41 du pdf)
		byte[] bufferTickle = {01, 00, 00, 00};
		DatagramPacket tickle = new DatagramPacket(bufferTickle, bufferTickle.length, inet_addr, 5554);

		send("AT*CONFIG="+(seq++)+",\"general:navdata_demo\",\"TRUE\"");
		
		send("AT*FTRIM="+(seq++));
		
		socketNavdata.send(tickle);
		
		send("AT*PMODE="+(seq++)+",2");
		
		send("AT*MISC="+(seq++)+",20,2000,3000");
		
		send("AT*FTRIM="+(seq++));

		// Init du thread watchdog + récup navdata
		ThreadNavdata Tn = new ThreadNavdata("TNavdata", this);
		Tn.start();
		
		socketVideo.getOutputStream().write(bufferTickle);
		
		send("AT*CONFIG="+(seq++)+",\"general:video_enable\",\"TRUE\"");
		
		socketVideo.getOutputStream().write(bufferTickle);
		
		send("AT*CONFIG="+(seq++)+",\"video:bitrate_ctrl_mode\",\"0\"");
		
		ThreadVideo Tv = new ThreadVideo(socketVideo, vpanel);
		Tv.start();
		
		System.out.println("\tARDrone pret.");
	}
	
	public void send(String str) throws Exception // On pourrait passer le port en argument ...
	{
		byte[] buffer = (str + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer,buffer.length,inet_addr,5556);
		socketCmd.send(packet);
		this.dernierCmd = str;
	}

	public void emergency() throws Exception
	{
		while (!this.navdata.emergency())
		{
			send("AT*REF=" + (seq++) + ",290717952");
			Thread.sleep(5);
		}
		System.out.println("\t["+seq+"] Atterissage d'urgence reussi !");
	}

	public void decoller() throws Exception
	{
		while (!this.navdata.flying())
		{
			send("AT*REF=" + (seq++) + ",290718208");
			Thread.sleep(5);
		}
		System.out.println("\t["+seq+"] Decollage réussi !");
	}

	public void atterrir() throws Exception
	{
		while (this.navdata.flying())
		{
			send("AT*REF=" + (seq++) + ",290717696");
			Thread.sleep(5);
		}
		System.out.println("\t["+seq+"] Atterrissage réussi !");
	}

	/* Déplacement : angles phi (horizontal latéral), theta (horizontal avant/arrière) et vitesses z (verticale), psi (angulaire verticale)
	 * Les arguments sont des pourcentages de l'angle (la vitesse) maximal ([-1 ; 1]) */
	public void deplacement(float phi, float theta, float vspeed, float aspeed) throws Exception
	{
		Boolean CombinedYaw = false;
		int phi2 = (phi>-0.1 && phi<0.1) ? 0 : Float.floatToIntBits(phi);
		int theta2 = (theta>-0.1 && theta<0.1) ? 0 : Float.floatToIntBits(theta);
		int vspeed2 = (vspeed>-0.1 && vspeed<0.1) ? 0 : Float.floatToIntBits(vspeed);
		int aspeed2 = (aspeed>-0.1 && aspeed<0.1) ? 0 : Float.floatToIntBits(aspeed);
		send("AT*PCMD=" + (seq++) + "," + (CombinedYaw ? 3 : 1) + ","+phi2+","+theta2+","+vspeed2+","+aspeed2);
	}
	
	public void stationnaire() throws Exception
	{
		send("AT*PCMD="+ (seq++) +",1,0,0,0,0");
	}
	
	public boolean enVol ()
	{
		return this.navdata.flying();
	}

}
