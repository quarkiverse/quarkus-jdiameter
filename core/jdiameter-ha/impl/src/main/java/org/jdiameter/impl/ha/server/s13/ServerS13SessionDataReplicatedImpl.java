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

package org.jdiameter.impl.ha.server.s13;

import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.common.api.app.s13.S13SessionState;
import org.jdiameter.impl.ha.common.s13.S13SessionDataReplicatedImpl;
import org.jdiameter.impl.ha.data.CachedSessionDatasource;
import org.jdiameter.server.impl.app.s13.IServerS13SessionData;

/**
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServerS13SessionDataReplicatedImpl extends S13SessionDataReplicatedImpl implements IServerS13SessionData
{
    public ServerS13SessionDataReplicatedImpl(String sessionId, CachedSessionDatasource datasource)
    {
        super(sessionId, datasource);

        setAppSessionIface(ServerS13Session.class);
        setS13SessionState(S13SessionState.IDLE);
    }
}
