package echo;
import java.util.logging.Level;
import java.util.logging.Logger; 
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.bootstrap.*;
import io.netty.channel.socket.nio.*;
import io.netty.channel.socket.*; 
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.channel.ChannelHandler.Sharable;
public class EchoServer {

	private int port;

	public EchoServer(int port){
		this.port = port;
	}
	public static void main(String[] args) throws Exception {

		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 8100;
		}
		new EchoServer(port).run();
	}


	public void run() throws Exception {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(
							//new LoggingHandler(LogLevel.INFO),
							new EchoServerHandler());
				}
			});
			ChannelFuture future =  b.bind(port).sync();
			future.channel().closeFuture().sync();
		}
		catch (InterruptedException e) {
			// TODO: handle exception
		}
		finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	
	@Sharable
	public class EchoServerHandler extends ChannelInboundHandlerAdapter{
		private final Logger logger = Logger.getLogger(
				EchoServerHandler.class.getName());

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ctx.write(msg);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.flush();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			// Close the connection when an exception is raised.
			logger.log(Level.WARNING, "Unexpected exception from downstream.", cause);
			ctx.close();
		}
	}
}
