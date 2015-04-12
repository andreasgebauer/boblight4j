# Boblight4J #

This project represents a Java port of [Boblight](http://code.google.com/p/boblight/). For getting a first impression of the running software watch the video below.

<a href='http://www.youtube.com/watch?feature=player_embedded&v=esPB5Ff0QFI' target='_blank'><img src='http://img.youtube.com/vi/esPB5Ff0QFI/0.jpg' width='425' height=344 /></a>

## Server ##
The server part is very limited currently. It will work with the following devices:
  * [Arduino](http://www.arduino.cc/) Mega

Any help regarding implementing support for other devices is strongly appreciated.


## Clients ##
Beside the server part which should work for the mentioned devices under all OSs also supported by Java there are four clients currently available.

### Linux (X11) Screen Grabber ###
The screen grabber client boblight4j-client-X11 needs X11 listen to port 6000. However an installation of XBMC Dharma's Live CD worked out-of-the-box for me.
An installation of [OpenElec](http://openelec.tv) and slightly changed X11 start script is working too.

### Linux (V4L) Video Grabber ###
The second is a video grabber which works with V4L. Many thanks go to Gilles who worked on making V4L accessible from Java by inventing [V4L4J](http://code.google.com/p/v4l4j/).  You need to have V4L4J up and running to use this client.

### Constant Client ###
The third is a constant light. It simply sends a constant light specified as rgb value to the server.

### Spectrum Analyzer (Experimental) ###
The last but not least is a spectrum analyzer. It grabs the signal from an audio device converts it via DCT to a spectrum and sends the amplitudes of the different channels to the server.

## Building boblight4j ##
Refer to [Compiling](Compiling.md) for building boblight4j.