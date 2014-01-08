package echo; 
/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
28   * Sends one message when a connection is open and echoes back any received
29   * data to the server.  Simply put, the echo client initiates the ping-pong
30   * traffic between the echo client and server by sending the first message to
31   * the server.
32   */
public class EchoClient {

	private final String host;
	private final int port;
	private final int firstMessageSize;

	public EchoClient(String host, int port, int firstMessageSize) {
		this.host = host;
		this.port = port;
		this.firstMessageSize = firstMessageSize;
	}

	public void run() throws Exception {
		// Configure the client.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(
							//new LoggingHandler(LogLevel.INFO),
							new EchoClientHandler(firstMessageSize));
				}
			});

			// Start the client.
			ChannelFuture f = b.connect(host, port).sync();

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		} finally {
			// Shut down the event loop to terminate all threads.
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		// Print usage if no argument is specified.
		if (args.length < 2 || args.length > 3) {
			System.err.println(
					"Usage: " + EchoClient.class.getSimpleName() +
			" <host> <port> [<first message size>]");
			//return;
			args = new String[]{"127.0.0.1","8100"}; 
		} 

		// Parse options.
		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final int firstMessageSize;
		if (args.length == 3) {
			firstMessageSize = Integer.parseInt(args[2]);
		} else {
			firstMessageSize = 256;
		}

		new EchoClient(host, port, firstMessageSize).run();
	}

	public class EchoClientHandler extends ChannelInboundHandlerAdapter{
		private  final Logger logger = Logger.getLogger(
				EchoClientHandler.class.getName());

		private final ByteBuf firstMessage;

		/**
		 * Creates a client-side handler.
		 */
		public EchoClientHandler(int firstMessageSize) {
			if (firstMessageSize <= 0) {
				throw new IllegalArgumentException("firstMessageSize: " + firstMessageSize);
			}
			firstMessage = Unpooled.buffer(firstMessageSize);
			for (int i = 0; i < firstMessage.capacity(); i ++) {
				firstMessage.writeByte((byte) i);
			}
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			ctx.writeAndFlush(firstMessage);
		}

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