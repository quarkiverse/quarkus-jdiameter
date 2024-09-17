/*
 * Copyright (C) 2018 TradeSwitch (Pty) Ltd
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of MobileData (Pty) Ltd and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to MobileData (Pty) Ltd
 * and its suppliers and may be covered by South African and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 *
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from TradeSwitch (Pty) Ltd.
 *
 *
 */

package org.jdiameter.common.impl;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.validation.AvpRepresentation;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.common.impl.validation.AvpRepresentationImpl;
import org.jdiameter.common.impl.validation.DictionaryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiameterUtilities {
    private static final Logger LOG = LoggerFactory.getLogger(DiameterUtilities.class);
    public static final Dictionary AVP_DICTIONARY = DictionaryImpl.INSTANCE;

    private DiameterUtilities() {
    }

    //-------------------------------------------------------[ printMessage ]---
    public static void printMessage(Message message) {
        if (LOG.isTraceEnabled()) {
            String reqFlag = message.isRequest() ? "R" : "A";
            String flags = reqFlag + (message.isError() ? " | E" : "");

            LOG.trace("Message [{}] Command-Code: {} / E2E({}) / HbH({})", flags, message.getCommandCode(),
                    message.getEndToEndIdentifier(), message.getHopByHopIdentifier());
            LOG.trace("\n- - - - - - - - - - - - - - - - AVPs - - - - - - - - - - - - - - - -\n{}",
                    printAvps(message.getAvps()));
        } //if
    }//printMessage

    //----------------------------------------------------------[ printAvps ]---
    public static String printAvps(AvpSet avps, String keywordSep) {
        return printAvps(avps, keywordSep, "", false);
    }

    public static String printAvps(AvpSet avps) {
        return printAvps(avps, "-");
    }//printAvps

    //----------------------------------------------------------[ printAvps ]---
    public static String printAvps(AvpSet avps, String indentation, boolean log) {
        return printAvps(avps, "-", indentation, log);
    }

    public static String printAvps(AvpSet avps, String pKeywordSep, String indentation, boolean log) {
        if (avps == null) {
            return "AvpSet is null";
        } //if

        StringBuilder stringBuilder = new StringBuilder();
        for (Avp avp : avps) {
            AvpRepresentation avpRep = AVP_DICTIONARY.getAvp(avp.getCode(), avp.getVendorId());
            if (avpRep == null) {
                avpRep =  new AvpRepresentationImpl("Unknown AVP "+avp.getCode(), "Unknown AVP",
                                                    avp.getCode(), false, null, null, null,
                                                    avp.getVendorId(),
                                                    AvpRepresentation.Type.UTF8String.toString(),
                                                    AvpRepresentation.Type.UTF8String.toString());
            }

            Object avpValue;
            boolean isGrouped = false;

            try {
                String avpType = avpRep.getType();

                if (null == avpType) {
                    avpValue = avp.getUTF8String().replace("\r", "").replace("\n", "");
                } //if
                else {
                    switch (avpType) {
                        case "Integer32", "AppId":
                            avpValue = avp.getInteger32();
                            break;
                        case "Unsigned32", "VendorId":
                            avpValue = avp.getUnsigned32();
                            break;
                        case "Float64":
                            avpValue = avp.getFloat64();
                            break;
                        case "Integer64":
                            avpValue = avp.getInteger64();
                            break;
                        case "Time":
                            avpValue = avp.getTime();
                            break;
                        case "Unsigned64":
                            avpValue = avp.getUnsigned64();
                            break;
                        case "Grouped":
                            avpValue = "<Grouped>";
                            isGrouped = true;
                            break;
                        default:
                            avpValue = avp.getUTF8String().replace("\r", "").replace("\n", "");
                            break;
                    }//else Switch
                }
            } //try
            catch (AvpDataException ignore) {
                try {
                    avpValue = avp.getUTF8String().replace("\r", "").replace("\n", "");
                } //try
                catch (AvpDataException e) {
                    avpValue = avp.toString();
                } //catch
            } //catch

            String fieldName = avpRep.getName().replace("-", pKeywordSep);
            StringBuilder avpLine = new StringBuilder(indentation)
                    .append(avp.getCode())
                    .append(": ")
                    .append(fieldName);
            while (avpLine.length() < 40) {
                avpLine.append(avpLine.length() % 2 == 0 ? "." : " ");
            } //while
            avpLine.append(avpValue);

            if (log && LOG.isDebugEnabled()) {
                LOG.debug(avpLine.toString());
            } //if

            stringBuilder.append(avpLine).append('\n');

            if (isGrouped) {
                try {
                    stringBuilder.append(printAvps(avp.getGrouped(), pKeywordSep, indentation + "  ", log));
                } //try
                catch (AvpDataException e) {
                    // Failed to ungroup... ignore then...
                } //catch
            } //if
        } //for

        return stringBuilder.toString();
    }//printAvps
}//DiameterUtilities
