package com.sencorsta.ids.core.net.outerClient;

import com.sencorsta.ids.core.net.coder.IdsRpcDecoder;
import com.sencorsta.ids.core.net.coder.IdsRpcEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * 编解码工厂
 *
 * @author ICe
 */
public final class OutCodecFactory extends ChannelInitializer<SocketChannel> {

    private ChannelHandler handler;

    public OutCodecFactory(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new IdsRpcDecoder());
        pipeline.addLast("encoder", new IdsRpcEncoder());
        pipeline.addLast("handler", handler);
    }

}
