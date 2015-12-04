package com.github.niqdev.openwebnet.rx;

import com.github.niqdev.openwebnet.domain.OpenSession;
import rx.Observable;

/**
 * TODO javadocs
 *
 * https://en.wikipedia.org/wiki/OpenWebNet
 *
 * @author niqdev
 */
public interface OpenWebNet {

    /**
     * Syntax *WHO*WHAT*WHERE##
     *
     * @param who
     * @param what
     * @param where
     * @return OpenSession
     */
    Observable<OpenSession> writeCommand(Integer who, Integer what, Integer where);

    /**
     * Syntax *#WHO*WHERE##
     *
     * @param who
     * @param where
     * @return OpenSession
     */
    Observable<OpenSession> readStatus(Integer who, Integer where);

    /**
     * Syntax *#WHO*WHERE*DIMENSION##
     *
     * @param who
     * @param where
     * @param dimension
     * @return OpenSession
     */
    Observable<OpenSession> readDimension(Integer who, Integer where, Integer dimension);

    /**
     * Syntax *#WHO*WHERE*#DIMENSION*VAL1*VAL2*...*VALn##
     *
     * @param who
     * @param where
     * @param dimension
     * @param value
     * @return OpenSession
     */
    Observable<OpenSession> writeDimension(Integer who, Integer where, Integer dimension, Integer... value);

}
