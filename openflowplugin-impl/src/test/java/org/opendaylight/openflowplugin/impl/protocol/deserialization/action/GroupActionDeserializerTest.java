/*
 * Copyright (c) 2016 Pantheon Technologies s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.openflowplugin.impl.protocol.deserialization.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.impl.util.ActionConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.GroupActionCase;

public class GroupActionDeserializerTest extends AbstractActionDeserializerTest {

    @Test
    public void testDeserialize() throws Exception {
        final ByteBuf in = UnpooledByteBufAllocator.DEFAULT.buffer();
        final int groupId = 10;
        writeHeader(in);
        in.writeInt(groupId);

        final Action action = deserializeAction(in);
        assertTrue(action instanceof GroupActionCase);
        assertEquals(groupId, ((GroupActionCase) action).getGroupAction().getGroupId().intValue());
        assertEquals(0, in.readableBytes());
    }

    @Override
    protected short getType() {
        return ActionConstants.GROUP_CODE;
    }

    @Override
    protected short getLength() {
        return ActionConstants.GENERAL_ACTION_LENGTH;
    }

}
