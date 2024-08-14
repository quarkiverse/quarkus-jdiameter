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
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 *   JBoss, Home of Professional Open Source
 *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
 *   by the @authors tag. See the copyright.txt in the distribution for a
 *   full listing of individual contributors.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.impl.helpers;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.api.IAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.jdiameter.client.impl.helpers.ExtensionPoint.*;
import static org.jdiameter.client.impl.helpers.Parameters.ExtensionName;
import static org.jdiameter.client.impl.helpers.Parameters.Extensions;

/**
 * IoC for stack
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
@SuppressWarnings("all") //3rd party lib
public class AssemblerImpl implements IAssembler
{
    private static final Logger logger = LoggerFactory.getLogger(AssemblerImpl.class);
    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    AssemblerImpl parent;
    final AssemblerImpl[] childs = new AssemblerImpl[ExtensionPoint.COUNT];

    static private class LazyInstance extends Object
    {

    }

    /**
     * Create instance of class with predefined configuration
     *
     * @param config configuration of stack
     * @throws Exception if generated internal exception
     */
    public AssemblerImpl(Configuration config) throws Exception
    {
        Configuration[] ext = config.getChildren(Extensions.ordinal());
        for (Configuration e : ext) {
            String extName = e.getStringValue(ExtensionName.ordinal(), "");

            // Create structure of containers
            if (extName.equals(ExtensionPoint.Internal.name())) {
                fill(ExtensionPoint.Internal.getExtensionPoints(), e, true);
            } else if (extName.equals(ExtensionPoint.StackLayer.name())) {
                updateContainer(config, StackLayer, InternalMetaData, InternalSessionFactory, InternalMessageParser, InternalElementParser);
            } else if (extName.equals(ExtensionPoint.ControllerLayer.name())) {
                updateContainer(config, ControllerLayer, InternalPeerController, InternalPeerFsmFactory, InternalRouterEngine);
            } else if (extName.equals(ExtensionPoint.TransportLayer.name())) {
                updateContainer(config, TransportLayer, InternalTransportFactory);
            }
        }
    }

    private void updateContainer(Configuration config, ExtensionPoint pointType, ExtensionPoint... updEntries) throws ClassNotFoundException
    {
        for (ExtensionPoint e : updEntries) {
            Configuration[] internalConf = config.getChildren(Extensions.ordinal());
            String oldValue = internalConf[Internal.id()].getStringValue(e.ordinal(), null);
            String newValue = internalConf[pointType.id()].getStringValue(e.ordinal(), null);
            if (oldValue != null && newValue != null) {
                unregister(loadClass(oldValue));
                registerComponentImplementation(loadClass(newValue));
            }
        }
    }

    /**
     * Create child Assembler
     *
     * @param parent parent assembler
     * @param e      child configuration
     * @param p      extension poit
     * @throws Exception
     */
    protected AssemblerImpl(AssemblerImpl parent, Configuration e, ExtensionPoint p) throws Exception
    {
        this.parent = parent;
        fill(p.getExtensionPoints(), e, false);
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException
    {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    private void fill(ExtensionPoint[] codes, Configuration e, boolean check) throws Exception
    {
        //NOTE: this installs components, but no instances created!
        for (ExtensionPoint c : codes) {
            String value = e.getStringValue(c.ordinal(), c.defValue());
            if (!check && (value == null || value.trim().length() == 0)) {
                return;
            }

            try {
                registerComponentImplementation(loadClass(value));
            }
            catch (NoClassDefFoundError exc) {
                throw new Exception(exc);
            }
        }
    }

    private Object[] buildArgs(Parameter[] parameters)
    {
        List<Object> params = new ArrayList<>();
        for (Parameter parameter : parameters) {
            Object arg = getComponentInstance(parameter.getType());
            if (arg == null) {
                return null;
            }
            params.add(arg);
        }
        return params.toArray();
    }

    private <T> T newInstance(Class<T> aClass)
    {
        List<Constructor> constructors = Arrays.asList(aClass.getConstructors());
        constructors.sort((t1, t2) -> Integer.compare(t2.getParameters().length, t1.getParameters().length));
        for (Constructor<?> constructor : constructors) {
            Object[] args = buildArgs(constructor.getParameters());
            if (args != null) {
                try {
                    T instance = (T) constructor.newInstance(args);
                    instances.put(aClass, instance);
                    return instance;
                }
                catch (InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                    logger.error("Error instantiating {}", aClass, ex);
                }
            } //if
        }

        return null;
    }

    private <T> T getInstance(Class<T> aClass)
    {
        T instance = (T) instances.get(aClass);
        if (instance instanceof LazyInstance) {
            instance = newInstance(aClass);
            if (instance == null) {
                throw new IllegalArgumentException("Error creating a new instance of " + aClass);
            }
            instances.put(aClass, instance);
        }
        return instance;
    }

    /**
     * @see org.picocontainer.MutablePicoContainer
     */
    @Override
    public <T> T getComponentInstance(Class<T> aClass)
    {
        if (aClass.isInterface()) {
            for (Class<?> regClass : instances.keySet()) {
                if (aClass.isAssignableFrom(regClass)) {
                    return (T) getInstance(regClass);
                }
            }
        }

        if (!instances.containsKey(aClass)) {
            logger.debug("No component instance found for {}", aClass);
            return null;
        }

        return getInstance(aClass);
    }

    /**
     * @see org.picocontainer.MutablePicoContainer
     */
    @Override
    public void registerComponentInstance(Object object)
    {
        instances.put(object.getClass(), object);
    }

    public void registerComponentImplementation(Class aClass)
    {
        instances.put(aClass, new LazyInstance());
    }

    /**
     * @see org.picocontainer.MutablePicoContainer
     */
    @Override
    public void registerComponentImplementation(Class<?> aClass, Object object)
    {
        instances.putIfAbsent(aClass, object);
    }

    public void unregister(Class aClass)
    {
        instances.remove(aClass);
    }

    @Override
    public void destroy()
    {
        instances.clear();
    }

    /**
     * return parent IOC
     */
    @Override
    public IAssembler getParent()
    {
        return parent;
    }

    /**
     * Get childs IOCs
     *
     * @return childs IOCs
     */
    @Override
    public IAssembler[] getChilds()
    {
        return childs;
    }
}
