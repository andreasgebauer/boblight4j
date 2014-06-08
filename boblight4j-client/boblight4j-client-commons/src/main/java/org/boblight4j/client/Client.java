package org.boblight4j.client;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.utils.Pointer;

public interface Client {

    /**
     * Setup the client. Like connecting to the server (for remote clients).
     * 
     * @return
     */
    boolean setup(FlagManager flagManager);

    /**
     * Sets a boblight option.
     * 
     * @param lightname
     * @param option
     * @throws BoblightException
     */

    void setOption(String lightname, String option) throws BoblightException;

    LightsHolder getLightsHolder();

    void sendRgb(boolean sync, Pointer<Integer> pointer) throws BoblightException;

    void destroy();

}
