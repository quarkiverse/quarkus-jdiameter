/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, TeleStax Inc. and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.jdiameter.impl.ha.common.s13;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.common.api.app.s13.S13SessionState;
import org.jdiameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.jdiameter.impl.ha.data.CachedSessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class S13SessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IS13SessionData
{

    private static final Logger logger = LoggerFactory.getLogger(S13SessionDataReplicatedImpl.class);

    private static final String STATE = "STATE";
    private static final String BUFFER = "BUFFER";
    private static final String TS_TIMERID = "TS_TIMERID";

    private final IMessageParser messageParser;

    public S13SessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
    {
        super(sessionId, datasource);
        this.messageParser = datasource.getContainer().getAssemblerFacility().getComponentInstance(IMessageParser.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.s13.IS13SessionData#setS13SessionState(org.jdiameter.common.api.app.s13.S13SessionState)
     */
    @Override
    public void setS13SessionState(S13SessionState state)
    {
        setFieldValue(STATE, state);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.s13.IS13SessionData#getS13SessionState()
     */
    @Override
    public S13SessionState getS13SessionState()
    {
        return S13SessionState.valueOf(getFieldValue(STATE));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.s13.IS13SessionData#getTsTimerId()
     */
    @Override
    public Serializable getTsTimerId()
    {
        return getFieldValue(TS_TIMERID);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.s13.IS13SessionData#setTsTimerId(java.io.Serializable)
     */
    @Override
    public void setTsTimerId(Serializable tid)
    {
        setFieldValue(TS_TIMERID, tid);
    }

    @Override
    public Request getBuffer()
    {
        byte[] data = getByteFieldValue(BUFFER);
        if (data != null) {
            try {
                return this.messageParser.createMessage(ByteBuffer.wrap(data));
            }
            catch (AvpDataException e) {
                logger.error("Unable to recreate message from buffer.");
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void setBuffer(Request buffer)
    {
        if (buffer != null) {
            try {
                byte[] data = this.messageParser.encodeMessage((IMessage) buffer).array();
                setFieldValue(BUFFER, data);
            }
            catch (ParseException e) {
                logger.error("Unable to encode message to buffer.");
            }
        } else {
            remove(BUFFER);
        }
    }
}
