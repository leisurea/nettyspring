package cn.leisure.client;

import cn.leisure.utils.BaseMsg;
import cn.leisure.utils.LoginMsg;
import cn.leisure.utils.MsgType;
import cn.leisure.utils.PingMsg;
import cn.leisure.utils.ReplyClientBody;
import cn.leisure.utils.ReplyMsg;
import cn.leisure.utils.ReplyServerBody;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by yaozb on 15-4-11.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<BaseMsg> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case WRITER_IDLE:
                    PingMsg pingMsg = new PingMsg();
                    ctx.writeAndFlush(pingMsg);
                    System.out.println("send ping to server----------");
                    break;
                default:
                    break;
            }
        }
    }

//    @Override
//    protected void messageReceived(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
//        MsgType msgType = baseMsg.getType();
//        System.out.println("ggggggg----------" + baseMsg.getType());
//        switch (msgType) {
//            case LOGIN: {
//                //向服务器发起登录
//                LoginMsg loginMsg = new LoginMsg();
//                loginMsg.setPassword("yao");
//                loginMsg.setUserName("robin");
//                channelHandlerContext.writeAndFlush(loginMsg);
//                System.out.println("ggggggg----------" + "send login");
//            }
//            break;
//            case PING: {
//                System.out.println("receive ping from server----------");
//            }
//            break;
//            case ASK: {
//                ReplyClientBody replyClientBody = new ReplyClientBody("client info **** !!!");
//                ReplyMsg replyMsg = new ReplyMsg();
//                replyMsg.setBody(replyClientBody);
//                channelHandlerContext.writeAndFlush(replyMsg);
//            }
//            break;
//            case REPLY: {
//                ReplyMsg replyMsg = (ReplyMsg) baseMsg;
//                ReplyServerBody replyServerBody = (ReplyServerBody) replyMsg.getBody();
//                System.out.println("receive client msg: " + replyServerBody.getServerInfo());
//            }
//            default:
//                break;
//        }
//        ReferenceCountUtil.release(msgType);
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {
        MsgType msgType = baseMsg.getType();
//        System.out.println("ggggggg----------" + baseMsg.getType());
        switch (msgType) {
            case LOGIN: {
                //向服务器发起登录
                LoginMsg loginMsg = new LoginMsg();
                loginMsg.setPassword("yao");
                loginMsg.setUserName("robin");
                channelHandlerContext.writeAndFlush(loginMsg);
//                System.out.println("ggggggg----------" + "send login");
            }
            break;
            case PING: {
                System.out.println("receive ping from server----------");
            }
            break;
            case ASK: {
                ReplyClientBody replyClientBody = new ReplyClientBody("client info **** !!!");
                ReplyMsg replyMsg = new ReplyMsg();
                replyMsg.setBody(replyClientBody);
                channelHandlerContext.writeAndFlush(replyMsg);
            }
            break;
            case REPLY: {
                ReplyMsg replyMsg = (ReplyMsg) baseMsg;
                ReplyServerBody replyServerBody = (ReplyServerBody) replyMsg.getBody();
                System.out.println("receive client msg: " + replyServerBody.getServerInfo());
            }
            default:
                break;
        }
        ReferenceCountUtil.release(msgType);
    }
}
