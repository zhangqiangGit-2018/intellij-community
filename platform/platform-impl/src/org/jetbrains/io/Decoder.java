/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Decoder extends ChannelInboundHandlerAdapter {
  protected ByteBuf cumulation;

  @Override
  public final void channelRead(ChannelHandlerContext context, Object message) throws Exception {
    if (message instanceof ByteBuf) {
      messageReceived(context, (ByteBuf)message);
    }
    else {
      context.fireChannelRead(message);
    }
  }

  protected abstract void messageReceived(@NotNull ChannelHandlerContext context, @NotNull ByteBuf message) throws Exception;

  @Nullable
  protected final ByteBuf getBufferIfSufficient(@NotNull ByteBuf input, int requiredLength, @NotNull ChannelHandlerContext context) {
    if (!input.isReadable()) {
      return null;
    }

    if (cumulation == null) {
      if (input.readableBytes() < requiredLength) {
        cumulation = context.channel().config().getAllocator().buffer(requiredLength);
        cumulation.writeBytes(input);
        return null;
      }
      else {
        return input;
      }
    }
    else {
      if ((cumulation.readableBytes() + input.readableBytes()) < requiredLength) {
        cumulation.writeBytes(input);
        return null;
      }
      else {
        ByteBuf buffer = Unpooled.wrappedBuffer(cumulation, input);
        input.skipBytes(input.readableBytes());
        cumulation = null;
        return buffer;
      }
    }
  }
}