'''
	Boblight for XBMC
	Copyright (C) 2011 Team XBMC

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
'''
import xbmc
import xbmcaddon
import xbmcgui
import time, datetime
import os, subprocess
from array import array

__settings__ = xbmcaddon.Addon(id='${project.build.finalName}')
__cwd__ = __settings__.getAddonInfo('path')
__icon__ = os.path.join(__cwd__, "icon.png")
__scriptname__ = "XBMC Boblight4J"
__java__ = "/storage/programs/jre/bin/java"
__jar__ =  os.path.join(__cwd__, "bin/${project.build.finalName}.jar")
__stop__       = xbmc.translatePath( os.path.join( __cwd__, 'bin', "boblight4j-client.stop") )

baseDir = __cwd__
resDir = xbmc.translatePath(os.path.join(baseDir, 'resources'))
libDir = xbmc.translatePath(os.path.join(resDir, 'lib'))
imgDir = xbmc.translatePath(os.path.join(resDir, 'img'))
cacheDir = os.path.join(xbmc.translatePath('special://masterprofile/addon_data/'), os.path.basename(baseDir))

BASE_RESOURCE_PATH = xbmc.translatePath(os.path.join(__cwd__, 'resources', 'lib'))
sys.path.append (BASE_RESOURCE_PATH)

from settings import *

LOG_FILE = cacheDir + '/client.log'

capture_width = 20
capture_height = 20

client = None

def writeToLogFile(message):
	if os.path.isfile(LOG_FILE) == False:
		logFile = open(LOG_FILE, 'w')
	else:
		logFile = open(LOG_FILE, 'r+')
		logFile.seek(0, 2)
	logFile.write(str(datetime.datetime.now()) + ": " + message + '\n')
	logFile.close()

def process_boblight():
	writeToLogFile("Starting Processing. Failed: " + str(xbmc.CAPTURE_STATE_FAILED))
	capture = xbmc.RenderCapture()
	capture.capture(capture_width, capture_height, xbmc.CAPTURE_FLAG_CONTINUOUS)

	while not xbmc.abortRequested:
		if settings_checkForNewSettings():
			settings_setup()
		if settings_getBobDisable():
			bob_set_priority(255)
	  		time.sleep(1)
			continue
		result = capture.waitForCaptureStateChangeEvent(1000)
		if capture.getCaptureState() == xbmc.CAPTURE_STATE_DONE:
			width = capture.getWidth()
			height = capture.getHeight()
			pixels = capture.getImage()
			bob_setscanrange(width, height)
			rgb = array('l', [0, 0, 0])
			message = ''
			for y in range(height):
				row = width * y * 4
				for x in range(width):
					rgb[0] = pixels[row + x * 4 + 2]
					rgb[1] = pixels[row + x * 4 + 1]
					rgb[2] = pixels[row + x * 4]
					message = str(x) + ',' + str(y) + ':' + str(rgb[0]) + "," + str(rgb[1]) + "," + str(rgb[2]) + "\n"
					client.stdin.write(message)
			client.stdin.write("send\n")
	
	# set to black
	for y in range(capture_height):
		for x in range(capture_width):
			client.stdin.write(str(x) + ',' + str(y) + ":0,0,0\n")
	client.stdin.write("send\n")
	client.stdin.write("send\n")
	client.stdin.write("send\n")

def reconnectBoblight():
  global g_failedConnectionNotified
  
  hostip   = settings_getHostIp()
  hostport = settings_getHostPort()
  
  if hostip == None:
    print "boblight: connecting to local boblightd"
  else:
    print "boblight: connecting to boblightd " + hostip + ":" + str(hostport)

  while not xbmc.abortRequested:
    #check for new settingsk
    if settings_checkForNewSettings():    #networksettings changed?
      g_failedConnectionNotified = False  #reset notification flag
    hostip   = settings_getHostIp()
    hostport = settings_getHostPort()    
    ret = bob_connect(hostip, hostport)

    if not ret:
      print "boblight: connection to boblightd failed: " + bob_geterror()
      count = 10
      while (not xbmc.abortRequested) and (count > 0):
        time.sleep(1)
        count -= 1
      if not g_failedConnectionNotified:
        g_failedConnectionNotified = True
        text = __settings__.getLocalizedString(500)
        xbmc.executebuiltin("XBMC.Notification(%s,%s,%s,%s)" % (__scriptname__,text,10,__icon__))
    else:
      text = __settings__.getLocalizedString(501)
      xbmc.executebuiltin("XBMC.Notification(%s,%s,%s,%s)" % (__scriptname__,text,10,__icon__))
      print "boblight: connected to boblightd"
      settings_initGlobals()		#invalidate settings after reconnect
      break
  return True

def initGlobals():
  global g_failedConnectionNotified

  g_failedConnectionNotified = False   
  settings_initGlobals()

def printLights():
	nrLights = bob_getnrlights()
	print "boblight: Found " + str(nrLights) + " lights:"

	for i in range(0, nrLights):
		lightname = bob_getlightname(i)
    	print "boblight: " + lightname



initGlobals()
loaded = bob_loadLibBoblight()

xbmc.log("XBMC XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX Boblight started")

#main loop
while not xbmc.abortRequested:
	if reconnectBoblight():
		printLights()		 #print found lights to debuglog
		xbmc.log("Starting Loop")
		process_boblight()	#boblight loop

subprocess.Popen([__stop__], shell=True, close_fds=True)
xbmc.log("XBMC XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX Boblight stopped")