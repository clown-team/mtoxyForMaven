package vip.smilex.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.smilex.MTServer;
import vip.smilex.stats.ConnectionType;

@ChannelHandler.Sharable
public class ProtocolDetector extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ProtocolDetector.class);

    private final MTServer server;
    private final StatisticsHandler mtStatistics;

    public ProtocolDetector(MTServer server) {
        this.server = server;

        mtStatistics = new StatisticsHandler(server.getStatisticsTracker(), ConnectionType.MTPROTO);
    }

    private void setupMtConnection(ChannelHandlerContext ctx, Object msg) {
        ctx.pipeline().remove(this);
        ctx.pipeline().addLast(
                mtStatistics,
                new Obfuscated2Handshaker(false, server.getConfiguration().getSecretKey()),
                new DatacenterConnectionHandler(server)
        );
        ctx.pipeline().fireChannelRead(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        setupMtConnection(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Exception caught in protocol detector from {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
