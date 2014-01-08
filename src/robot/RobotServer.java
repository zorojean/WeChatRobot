package robot;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.bootstrap.*;
import io.netty.channel.socket.nio.*;
import io.netty.channel.socket.*; 
public class RobotServer {

	private int port;

	public RobotServer(int port){
		this.port = port;
	}
	public static void main(String[] args) throws Exception {

		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 8100;
		}
		new RobotServer(port).run();
	}


	public void run() throws Exception {

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>(){
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new RotBotHandler());
				}
			} 
			).option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE,true);
			ChannelFuture future = serverBootstrap.bind(port).sync();
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

}
