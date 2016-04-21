# Introduction #

Using a custom app (e.g. a self made one) you may have trouble downloading the video stream from the AR.Drone 2.0. From our understanding the method is not described in Parrot's [SDK](https://projects.ardrone.org/attachments/download/495/ARDrone_SDK_2_0_1.tar.gz) nor on it's [project website](https://projects.ardrone.org/).

We will quickly describe here the AT commands you will need to send your drone to get the main camera video feed. However the second feed (the one from the ground-facing camera) will not be discussed (it may be in a future update).

# Setting up your code #

## Ports & sockets ##

After having initialized your navdata feed from the drone (port 5554) you can start working on the video stream. You will need to send packets to two different ports on your drone :
  * command port 5556
  * video port 5555
We recommend you create two distinct sockets with non-ambiguous names to keep things easy !

## Tickles ##

A tickle is a small packet which, from our understanding, is used to tell the destination port that something is happening. In this case we will use it to "wake up" the drone video port.

A tickle consists of four bytes. The first byte is 1 and the others are 0 : `[1,0,0,0]`. This is what is to be sent when we are referring to a _tickle_.

# Method #

Send the following packets in the following order to their respective ports :
  1. **(Command 5556)** : `AT*PMODE=sequence,2`
  1. **(Command 5556)** : `AT*MISC=sequence,20,2000,3000`
  1. **(Command 5556)** : `AT*FTRIM=sequence`
  1. **(Video 5555)** : _tickle_
  1. **(Command 5556)** : `AT*CONFIG=sequence,"general:video_enable","TRUE"`
  1. **(Video 5555)** : _tickle_
  1. **(Command 5556)** : `AT*CONFIG=sequence,"video:bitrate_ctrl_mode","0"`
Now the drone should start sending udp video packets to your app. You can use external libraries such as [Xuggler](http://www.xuggle.com/xuggler) (assuming you're using Java) to process the packets and "paint" the video on a panel for example.

# Java example #

Here's the corresponding snippet from our code :
```
socketCmd = new DatagramSocket(5556);
socketCmd.setSoTimeout(3000);
socketNavdata = new DatagramSocket(5554);
socketNavdata.setSoTimeout(3000);
socketVideo = new Socket(inet_addr, 5555);
socketVideo.setSoTimeout(3000);

byte[] bufferTickle = {01, 00, 00, 00};

/* Initializing drone configuration and navdatas here ... */

send("AT*PMODE="+(seq++)+",2");
send("AT*MISC="+(seq++)+",20,2000,3000");
send("AT*FTRIM="+(seq++));
socketVideo.getOutputStream().write(bufferTickle);		
send("AT*CONFIG="+(seq++)+",\"general:video_enable\",\"TRUE\"");
socketVideo.getOutputStream().write(bufferTickle);
send("AT*CONFIG="+(seq++)+",\"video:bitrate_ctrl_mode\",\"0\"");

/* Launching the video processing thread */
```
Our `send` method is linked to the `socketCmd`.