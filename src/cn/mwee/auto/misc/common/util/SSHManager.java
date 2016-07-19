package cn.mwee.auto.misc.common.util;

import com.jcraft.jsch.*;

import cn.mwee.auto.deploy.service.IFlowTaskLogService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by huming on 16/7/6.
 */
public class SSHManager
{
    private org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SSHManager.class);

    private JSch jschSSHChannel;
    private String strUserName;
    private String strConnectionIP;
    private String strPassword;
    private Session sesConnection;

    private int intTimeOut = 60000;
    private int intConnectionPort = 22;

    private String prvkey;
    
    private ThreadPoolTaskExecutor springTaskExecutor;
	private IFlowTaskLogService flowTaskLogService;
	private Integer logId;
    public SSHManager(String userName, String prvkey,String connectionIP,
    		IFlowTaskLogService flowTaskLogService,ThreadPoolTaskExecutor springTaskExecutor,Integer logId)
    {
        this.strUserName = userName;
        this.prvkey = prvkey;
        this.strConnectionIP = connectionIP;
        
        
        this.flowTaskLogService = flowTaskLogService;
        this.springTaskExecutor = springTaskExecutor;
        this.logId = logId;

        jschSSHChannel = new JSch();
    }

    public SSHManager(String userName,String connectionIP)
    {
        this.strUserName = userName;
        this.strConnectionIP = connectionIP;
        jschSSHChannel = new JSch();
    }


    public String connect()
    {
        String errorMessage = null;

        try
        {
            sesConnection = jschSSHChannel.getSession(strUserName,
                    strConnectionIP, intConnectionPort);

            if(StringUtils.isNotBlank(prvkey))
            {
                jschSSHChannel.addIdentity(prvkey);
            }
            else if(StringUtils.isNotBlank(strPassword))
            {
                sesConnection.setPassword(strPassword);
            }

            // UNCOMMENT THIS FOR TESTING PURPOSES, BUT DO NOT USE IN PRODUCTION
            sesConnection.setConfig("StrictHostKeyChecking", "no");

            sesConnection.connect(intTimeOut);
        }
        catch(JSchException jschX)
        {
            errorMessage = jschX.getMessage();
        }

        return errorMessage;
    }

    private String logError(String errorMessage)
    {
        if(errorMessage != null)
        {
            LOGGER.error("{}:{} - {}",strConnectionIP, intConnectionPort, errorMessage);
        }

        return errorMessage;
    }

    private String logWarning(String warnMessage)
    {
        if(warnMessage != null)
        {
            LOGGER.warn("{}:{} - {}",strConnectionIP, intConnectionPort, warnMessage);
        }

        return warnMessage;
    }

    public String sendCommand(String command,Integer flowTaskId)
    {
        StringBuilder outputBuffer = new StringBuilder();

        try
        {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            String lineStr = null ;
            while ((lineStr = reader.readLine()) != null) {
            	outputBuffer.append(lineStr);
            	final String tmpLineStr = lineStr;
            	springTaskExecutor.execute(new Runnable() {
					@Override
					public void run() {
						flowTaskLogService.addLineLog(logId, tmpLineStr);
					}
				});
            }
            
            /*
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();

            while(readByte != 0xffffffff)
            {
                outputBuffer.append((char)readByte);
                readByte = commandOutput.read();
            }
            */
            channel.disconnect();
            if (outputBuffer.length()<1) {
            	flowTaskLogService.addLineLog(logId, "No such file or directory:"+ command);
            }
        }
        catch(IOException ioX)
        {
            logWarning(ioX.getMessage());
            outputBuffer.append(ioX.getMessage());
            return outputBuffer.toString();
        }
        catch(JSchException jschX)
        {
            logWarning(jschX.getMessage());
            outputBuffer.append(jschX.getMessage());
            return outputBuffer.toString();
        }

        return outputBuffer.toString();
    }

    public void close()
    {
        sesConnection.disconnect();
    }

}