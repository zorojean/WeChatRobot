package robot;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.buffer.*;

public class RotBotHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(io.netty.channel.ChannelHandlerContext ctx)
	throws java.lang.Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);
		//		ctx.write("we receied cmd of yours!");
		//		ctx.flush();
		ByteBuf hello = ctx.alloc().buffer(5);
		//hello.writeChar((int) (Math.random()*26+'a'));
		hello.writeInt(1);
		ctx.writeAndFlush(hello);
		System.out.println("hello,person,i'm Robot !");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
		ByteBuf in = (ByteBuf) msg;
		try {
			while (in.isReadable()) { // (1)
				System.out.println("receive msg form person£º");
				System.out.println((char) in.readByte());
				System.out.flush();
			}
		} finally {
			ReferenceCountUtil.release(msg); // (2)
		}
	}
	
	 
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
 
	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws java.lang.Exception {
		// TODO Auto-generated method stub
		super.channelWritabilityChanged(ctx);
		System.out.println("ctx write buffer is change!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}


}
