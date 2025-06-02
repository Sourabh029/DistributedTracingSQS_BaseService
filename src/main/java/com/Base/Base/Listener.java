package com.Base.Base;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

@Service
@Log4j2
public class Listener {



    @SqsListener("MyQueue")
    public void listen(Message message){
       log.info(message);
    }


}
