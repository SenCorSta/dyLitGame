package com.sencorsta.ids;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sencorsta.ids.api.request.LoginRequest;
import com.sencorsta.ids.core.config.GlobalConfig;
import com.sencorsta.ids.core.constant.ProtocolTypeConstant;
import com.sencorsta.ids.core.constant.SerializeTypeConstant;
import com.sencorsta.ids.core.net.innerClient.RpcClientBootstrap;
import com.sencorsta.ids.core.net.outerClient.OutClientChannelHandler;
import com.sencorsta.ids.core.net.outerClient.OutCodecFactory;
import com.sencorsta.ids.core.net.protocol.RpcMessage;
import com.sencorsta.ids.core.processor.MessageProcessor;
import com.sencorsta.utils.object.Jsons;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientForUser {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        GlobalConfig.instance();
        GlobalConfig.IS_DEBUG = false;
        RpcClientBootstrap bootstrap = new RpcClientBootstrap("test", new OutCodecFactory(new OutClientChannelHandler()));
        Channel connect = bootstrap.connect("127.0.0.1", 10001);
        int total = 1;
        int count = 0;
        while (count < total) {
            RpcMessage message = new RpcMessage(ProtocolTypeConstant.TYPE_REQ);
            message.setMethod("/userServer/login");
            message.setSerializeType(SerializeTypeConstant.TYPE_JSON);
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setAccount("icemaker1");
            loginRequest.setPassword("12345678");
            message.setData(Jsons.getMapper().writeValueAsBytes(loginRequest));
            message.setChannel(connect);

            RpcMessage response = MessageProcessor.request(message);
            if (response != null) {
                if (response.getErrCode() > 0) {
                    log.info("response error:{}", response.getErrCode());
                } else {
                    log.info("response:{}", new String(response.getData()));
                }
            } else {
                log.info("response:{}", "null");
            }
            count++;
            //Thread.sleep(1);
        }
    }
}
