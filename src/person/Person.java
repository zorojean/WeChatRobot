package person;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.bootstrap.*;
import io.netty.channel.socket.nio.*; 
public class Person {

	static java.util.Scanner scanner = new java.util.Scanner(System.in);
	public static void main(String[] args) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new HelloClientHandler());
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.connect(new java.net.InetSocketAddress(8100));
	}

	static class HelloClientHandler extends ChannelInboundHandlerAdapter{

		@Override
		public void channelActive(io.netty.channel.ChannelHandlerContext ctx)
		throws java.lang.Exception {
			// TODO Auto-generated method stub
			super.channelActive(ctx);
			System.out.println("hello,i am a person!");
		}
		
		@Override
		public void channelWritabilityChanged(
				io.netty.channel.ChannelHandlerContext ctx)
				throws java.lang.Exception {
			// TODO Auto-generated method stub
			super.channelWritabilityChanged(ctx);
		
			scanner.next();
		}
	}	
}

