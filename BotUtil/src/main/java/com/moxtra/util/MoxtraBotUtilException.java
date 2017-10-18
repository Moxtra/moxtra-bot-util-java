package com.moxtra.util;

import org.apache.commons.lang.exception.NestableException; 

/**
 * @author jmi
 */
public class MoxtraBotUtilException extends NestableException {
    private static final long serialVersionUID = -866497531917758585L;
    
    public MoxtraBotUtilException(String message) {
        super(message);
    }
    public MoxtraBotUtilException(String message, Throwable t) {
        super(message, t);
    }

}
