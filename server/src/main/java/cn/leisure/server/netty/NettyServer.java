package cn.leisure.server.netty;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;

import cn.leisure.server.ServerApplication;
import cn.leisure.utils.AskMsg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Created by yaozb on 15-4-11.
 */
public class NettyServer {
    private final static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private int port;
    private SocketChannel socketChannel;

    private DispatcherServlet dispatcherServlet;

    public NettyServer(int port) throws Exception {
        this.port = port;
        bind();//msg
        run();//http
    }

    public void run() throws Exception {
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            server.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .localAddress(8080)
                    .childHandler(new HttpServletChannelInitializer());

            logger.info("Netty server has started on port : " + 8080);

            server.bind().sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private void bind() throws InterruptedException, ServletException {

        MockServletContext servletContext = new MockServletContext();
        MockServletConfig servletConfig = new MockServletConfig(servletContext);

        AnnotationConfigWebApplicationContext wac = new AnnotationConfigWebApplicationContext();
        wac.setServletContext(servletContext);
        wac.setServletConfig(servletConfig);
        wac.register(ServerApplication.class);//--
        wac.refresh();

        this.dispatcherServlet = new DispatcherServlet(wac);
        this.dispatcherServlet.init(servletConfig);

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
//                p.addLast("decoder", new HttpRequestDecoder());
//                p.addLast("aggregator", new HttpObjectAggregator(65536));
//                p.addLast("encoder", new HttpResponseEncoder());
//                p.addLast("chunkedWriter", new ChunkedWriteHandler());
//                p.addLast("handler", new ServletNettyHandler(dispatcherServlet));

                p.addLast(new ObjectEncoder());
                p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                p.addLast(new NettyTcpHandler());


            }
        });
        ChannelFuture f = bootstrap.bind(port).sync();
        if (f.isSuccess()) {
//            System.out.println("server start---------------");
            logger.info("Netty server has started on port : " + port);
        }
    }

    public static void main(String[] args) throws Exception {

        NettyServer bootstrap = new NettyServer(9999);
        while (true) {
            SocketChannel channel = (SocketChannel) TcpClientMap.get("001");
            if (channel != null) {
                AskMsg askMsg = new AskMsg();
                channel.writeAndFlush(askMsg);
            }
            TimeUnit.SECONDS.sleep(1000);
        }
    }
}
