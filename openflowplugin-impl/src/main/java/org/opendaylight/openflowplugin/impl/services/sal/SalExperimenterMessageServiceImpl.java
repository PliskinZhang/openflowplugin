/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowplugin.impl.services.sal;

import static org.opendaylight.openflowplugin.openflow.md.util.InventoryDataServiceUtil.extractDatapathId;

import com.google.common.util.concurrent.ListenableFuture;
import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.openflowplugin.api.openflow.device.DeviceContext;
import org.opendaylight.openflowplugin.api.openflow.device.RequestContextStack;
import org.opendaylight.openflowplugin.api.openflow.device.Xid;
import org.opendaylight.openflowplugin.extension.api.ConverterMessageToOFJava;
import org.opendaylight.openflowplugin.extension.api.ExtensionConvertorData;
import org.opendaylight.openflowplugin.extension.api.TypeVersionKey;
import org.opendaylight.openflowplugin.extension.api.core.extension.ExtensionConverterProvider;
import org.opendaylight.openflowplugin.extension.api.exception.ConversionException;
import org.opendaylight.openflowplugin.extension.api.exception.ConverterNotFoundException;
import org.opendaylight.openflowplugin.impl.services.AbstractSimpleService;
import org.opendaylight.openflowplugin.impl.services.util.ServiceException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.experimenter.message.service.rev151020.SalExperimenterMessageService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.experimenter.message.service.rev151020.SendExperimenterInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.experimenter.message.service.rev151020.SendExperimenterOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.ExperimenterInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.OfHeader;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.experimenter.core.ExperimenterDataOfChoice;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflowplugin.experimenter.types.rev151020.experimenter.core.message.ExperimenterMessageOfChoice;
import org.opendaylight.yangtools.yang.common.RpcResult;

public class SalExperimenterMessageServiceImpl extends AbstractSimpleService<SendExperimenterInput,
        SendExperimenterOutput> implements SalExperimenterMessageService {

    private final ExtensionConverterProvider extensionConverterProvider;

    public SalExperimenterMessageServiceImpl(final RequestContextStack requestContextStack,
                                             final DeviceContext deviceContext,
                                             final ExtensionConverterProvider extensionConverterProvider) {
        super(requestContextStack, deviceContext, SendExperimenterOutput.class);
        this.extensionConverterProvider = extensionConverterProvider;
    }

    @Override
    protected OfHeader buildRequest(Xid xid, SendExperimenterInput input) throws ServiceException {
        final TypeVersionKey key =
                new TypeVersionKey(input.getExperimenterMessageOfChoice().getImplementedInterface(), getVersion());
        final ConverterMessageToOFJava<ExperimenterMessageOfChoice, ExperimenterDataOfChoice,
            ExtensionConvertorData> messageConverter = extensionConverterProvider.getMessageConverter(key);

        if (messageConverter == null) {
            throw new ServiceException(new ConverterNotFoundException(key.toString()));
        }
        final ExperimenterInputBuilder experimenterInputBld;
        try {
            final ExtensionConvertorData data = new ExtensionConvertorData(OFConstants.OFP_VERSION_1_3);
            data.setXid(xid.getValue());
            data.setDatapathId(extractDatapathId(input.getNode()));
            experimenterInputBld = new ExperimenterInputBuilder()
                    .setExperimenter(messageConverter.getExperimenterId())
                    .setExpType(messageConverter.getType())
                    .setExperimenterDataOfChoice(messageConverter.convert(input.getExperimenterMessageOfChoice(), data))
                    .setVersion(getVersion())
                    .setXid(xid.getValue());
        } catch (ConversionException e) {
            throw new ServiceException(e);
        }

        return experimenterInputBld.build();
    }

    @Override
    public ListenableFuture<RpcResult<SendExperimenterOutput>> sendExperimenter(SendExperimenterInput input) {
        return handleServiceCall(input);
    }
}
