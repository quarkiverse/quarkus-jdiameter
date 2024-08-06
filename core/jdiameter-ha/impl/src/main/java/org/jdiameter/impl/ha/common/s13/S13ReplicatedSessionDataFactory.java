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

import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.common.api.app.IAppSessionDataFactory;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.impl.ha.client.s13.ClientS13SessionDataReplicatedImpl;
import org.jdiameter.impl.ha.data.CachedSessionDatasource;
import org.jdiameter.impl.ha.server.s13.ServerS13SessionDataReplicatedImpl;

/**
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class S13ReplicatedSessionDataFactory implements IAppSessionDataFactory<IS13SessionData>
{

    private final CachedSessionDatasource cachedSessionDataSource;
    
    public S13ReplicatedSessionDataFactory(CachedSessionDatasource replicatedSessionDataSource)
    { // Is this ok?
        super();
        this.cachedSessionDataSource = replicatedSessionDataSource;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.IAppSessionDataFactory#getAppSessionData(java.lang.Class, java.lang.String)
     */
    @Override
    public IS13SessionData getAppSessionData(Class<? extends AppSession> clazz, String sessionId)
    {
        if (clazz.equals(ClientS13Session.class)) {
            ClientS13SessionDataReplicatedImpl data =
                    new ClientS13SessionDataReplicatedImpl(sessionId, cachedSessionDataSource);
            return data;
        } else if (clazz.equals(ServerS13Session.class)) {
            ServerS13SessionDataReplicatedImpl data =
                    new ServerS13SessionDataReplicatedImpl(sessionId, cachedSessionDataSource);
            return data;
        }
        throw new IllegalArgumentException();
    }

}
