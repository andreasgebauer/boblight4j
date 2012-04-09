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

import sys
import time
import xbmc
__scriptname__ = sys.modules[ "__main__" ].__scriptname__
__settings__   = sys.modules[ "__main__" ].__settings__
__cwd__        = sys.modules[ "__main__" ].__cwd__
__icon__       = sys.modules[ "__main__" ].__icon__
sys.path.append (__cwd__)

#general
global g_javahome

#init globals with defaults
def settings_initGlobals():
  global g_javahome

  g_javahome  = __settings__.getSetting("javahome")
  
#check for new settings and handle them if anything changed
#only checks if the last check is 5 secs old
#returns if a reconnect is needed due to settings change
def settings_checkForNewSettings():
#todo  for now impl. stat on addon.getAddonInfo('profile')/settings.xml and use mtime
#check for new settings every 5 secs
  global g_timer
  reconnect = False

  if time.time() - g_timer > 5:
    reconnect = settings_setup()
    g_timer = time.time()
  return reconnect

#handles all settings of boblight and applies them as needed
#returns if a reconnect is needed due to settings changes
def settings_setup():  
  global g_overwrite_cat
  global g_overwrite_cat_val
  reconnect = False
  settingChanged = False
  categoryChanged = False

  g_overwrite_cat = __settings__.getSetting("overwrite_cat") == "true"
  g_overwrite_cat_val = int(__settings__.getSetting("overwrite_cat_val"))

  category = settings_getSettingCategory()
  categoryChanged = settings_handleCategory(category)
  reconnect = settings_handleNetworkSettings()
  settingChanged = settings_handleGlobalSettings(category)
  settings_handleStaticBgSettings(category)
  settings_handleDisableSetting()

  #notify user via toast dialog when a setting was changed (beside category changes)
  if settingChanged and not categoryChanged:
    text = __settings__.getLocalizedString(502)
    xbmc.executebuiltin("XBMC.Notification(%s,%s,%s,%s)" % (__scriptname__,text,10,__icon__))

  return reconnect
  
