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

"""

cheat sheet

c_void_p(g_libboblight.boblight_init())
g_libboblight.boblight_destroy(boblight)
c_int(g_libboblight.boblight_connect(boblight, const char* address, int port, int usectimeout))
c_int(g_libboblight.boblight_setpriority(boblight, int priority))
c_char_p(g_libboblight.boblight_geterror(boblight))
c_int(g_libboblight.boblight_getnrlights(boblight))
c_char_p(g_libboblight.boblight_getlightname(boblight, int lightnr))
c_int(g_libboblight.boblight_getnroptions(boblight))
c_char_p(g_libboblight.boblight_getoptiondescriptboblight, int option))
c_int(g_libboblight.boblight_setoption(boblight, int lightnr, const char* option))
c_int(g_libboblight.boblight_getoption(boblight, int lightnr, const char* option, const char** output))
g_libboblight.boblight_setscanrange(boblight, int width, int height)
c_int(g_libboblight.boblight_addpixel(boblight, int lightnr, int* rgb))
g_libboblight.boblight_addpixelxy(boblight, int x, int y, int* rgb)
c_int(g_libboblight.boblight_sendrgb(boblight, int sync, int* outputused))
c_int(g_libboblight.boblight_ping(boblight, int* outputused))

"""
import platform
import xbmc
import sys
import os
import subprocess

__scriptname__ = sys.modules[ "__main__" ].__scriptname__
__settings__ = sys.modules[ "__main__" ].__settings__
__cwd__ = sys.modules[ "__main__" ].__cwd__
__icon__ = sys.modules[ "__main__" ].__icon__
__start__ = xbmc.translatePath(os.path.join(__cwd__, 'bin', "boblight4j-client.start"))


global g_boblightLoaded
global g_current_priority
global g_client
global g_connected

def bob_loadLibBoblight():
	global g_boblightLoaded
	global g_current_priority
	global g_client
	global g_connected
	
	g_connected = False
	g_current_priority = -1
	g_boblightLoaded = True
	return 1

def bob_set_priority(priority):
  global g_current_priority
  
  ret = True
  if g_boblightLoaded and g_connected:
    if priority != g_current_priority:
      g_current_priority = priority
      if not g_libboblight.boblight_setpriority(g_bobHandle, g_current_priority):
        print "boblight: error setting priority: " + c_char_p(g_libboblight.boblight_geterror(g_bobHandle)).value
        ret = False
  return ret
  
def bob_setscanrange(width, height):
  if g_boblightLoaded and g_connected:
  	scan = "scan" + str(width) + "," + str(height) + "\n"
  	g_client.stdin.write(scan)
  
def bob_addpixelxy(x, y, rgb):
  if g_boblightLoaded and g_connected:
    g_libboblight.boblight_addpixelxy(g_bobHandle, x, y, rgb)

def bob_addpixel(rgb):
  if g_boblightLoaded and g_connected:
    g_libboblight.boblight_addpixel(g_bobHandle, -1, rgb)

def bob_sendrgb():
  ret = False
  if g_boblightLoaded and g_connected:
    ret = c_int(g_libboblight.boblight_sendrgb(g_bobHandle, 1, None)) != 0
  else:
    ret = True
  return ret
  
def bob_setoption(option):
  ret = False
  if g_boblightLoaded and g_connected:
    ret = c_int(g_libboblight.boblight_setoption(g_bobHandle, -1, option)) != 0
  else:
    ret = True
  return ret
  
def bob_getnrlights():
	if g_boblightLoaded and g_connected:
		g_client.stdin.write("getNrLights")
		g_client.stdin.flush()
		g_client.stdout.flush()
		ret = g_client.stdout.readline()
		xbmc.log("boblight: got " + ret + " lights")
		return int(ret)
	return 0
  
def bob_getlightname(nr):
  ret = ""
  if g_boblightLoaded and g_connected:
    ret = g_libboblight.boblight_getlightname(g_bobHandle, nr)
  return ret

def bob_connect(hostip, hostport):
  global g_connected
  global g_client
  
  if g_boblightLoaded:
  	g_client = subprocess.Popen([__start__], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, shell=False)
  	g_connected = True
  else:
    g_connected = False
  return g_connected
  
def bob_set_static_color(rgb):
  if g_boblightLoaded and g_connected:
    bob_addpixel(rgb)
    bob_sendrgb()

def bob_destroy():
  if g_boblightLoaded:
    g_libboblight.boblight_destroy(g_bobHandle)

def bob_geterror():
  ret = ""
  if g_boblightLoaded:
    ret = c_char_p(g_libboblight.boblight_geterror(g_bobHandle)).value
  return ret
