package cn.mwee.auto.listener;

import cn.mwee.auto.common.util.MessageUtil;
import cn.mwee.auto.deploy.model.Mail;
import cn.mwee.auto.misc.mq.AppEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by raoshaoquan on 16/8/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/applicationContext.xml",})
public class MailMQMessageListenerTest {

    @Resource(name = "mailMQSender")
    private MessageUtil mailMQSender;

    @Test
    public void sendMail() {
        Mail mail = new Mail();
        mail.setSubject("美味不用等自动化提醒");
        mail.setMailBody("邮件发送测试!");
        AppEvent event = new AppEvent(0, "soa.notify.mail.create", mail);
        mailMQSender.convertAndSend(event);
    }
}
