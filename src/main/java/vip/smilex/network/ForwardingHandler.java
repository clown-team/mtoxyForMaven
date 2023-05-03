package vip.smilex.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.smilex.util.ExceptionUtils;

public class ForwardingHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ForwardingHandler.class);

    private Channel to;
    private ForwardingHandler peerHandler;
    private boolean isClosed = false;

    private ForwardingHandler(Channel to) {
        this.to = to;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        to.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if (!ExceptionUtils.isConnectionResetException(cause)) {
            LOG.error("Exception caught while forwarding: {} -> {}", ctx.channel().remoteAddress(), to.remoteAddress(),
                    cause);
        }

        ctx.close();
        close();
    }

    private void close() {
        if (isClosed) {
            return;
        }

        isClosed = true;
        to.close();
        peerHandler.close();
    }

    public static void setupForwarding(Channel src, Channel dst) {
        ForwardingHandler first = new ForwardingHandler(dst);
        ForwardingHandler second = new ForwardingHandler(src);

        first.peerHandler = second;
        second.peerHandler = first;

        src.pipeline().addLast(first);
        dst.pipeline().addLast(second);
    }
}
