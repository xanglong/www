package com.xanglong.frame.log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.Queue;

import com.xanglong.frame.Sys;
import com.xanglong.frame.config.Config;
import com.xanglong.frame.io.FileUtil;
import com.xanglong.frame.util.DateUtil;

public class Logger {
	
	private static boolean hasConsumer = false;
	private static Queue<LogData> logQueue = new LinkedList<LogData>();
	
	/**
	 * 记录日志
	 * @param logData 日志对象
	 * */
	private static void asyncWriteLog(LogData logData) {
		Throwable throwable = logData.getThrowable();
		String logPath = logData.getLogPath();
		String dateTime = logData.getDateTime();
		try (StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
		) {
			throwable.printStackTrace(printWriter);
			FileUtil.writeAppend(new File(logPath), dateTime + "\t" + stringWriter.toString() + "\n");
		} catch(Throwable e) {
			//唯独这里的异常不处理
			e.printStackTrace();
		}
	}

	/**
	 * 打印业务异常日志
	 * @param throwable 异常 
	 * */
	public static void warn(Throwable throwable) {
		Config config = Sys.getConfig();
		String dateTime = DateUtil.getDateTime();
		String date = dateTime.substring(0, 10);
		String logTypeFolder = Sys.getConfig().getLog().getBizExceptionPath();
		String logPath = config.getDataPath() + config.getLog().getLogBasePath() + "/" + logTypeFolder + "." + date + ".log";
		writeLog(new LogData(throwable, logPath, dateTime));
	}

	/**
	 * 打印系统异常日志
	 * @param throwable 异常 
	 * */
	public static void danger(Throwable throwable) {
		Config config = Sys.getConfig();
		String dateTime = DateUtil.getDateTime();
		String date = dateTime.substring(0, 10);
		String logTypeFolder = Sys.getConfig().getLog().getSystemExceptionPath();
		String logPath = config.getDataPath() + config.getLog().getLogBasePath() + "/" + logTypeFolder + "." + date + ".log";
		writeLog(new LogData(throwable, logPath, dateTime));
	}

	/**
	 * 打印系统错误日志
	 * @param throwable 异常
	 * */
	public static void error(Throwable throwable) {
		Config config = Sys.getConfig();
		String dateTime = DateUtil.getDateTime();
		String date = dateTime.substring(0, 10);
		String logTypeFolder = Sys.getConfig().getLog().getErrorPath();
		String logPath = config.getDataPath() + config.getLog().getLogBasePath() + "/" + logTypeFolder + "." + date + ".log";
		writeLog(new LogData(throwable, logPath, dateTime));
	}

	/**
     * 写日志
     * @param 日志对象
     * */
    public static void writeLog(LogData logData) {
    	if (logQueue.size() == 0) {
    		//如果队列为空,则先添加到队列
    		logQueue.offer(logData);
    		//如果没有消费者，则开启异步线程开始消费
    		if (!hasConsumer) {
    			hasConsumer = true;
    			new Thread() {
                    public void run() {
                    	//循环发送邮件队列，发送时取出对象
                		while (logQueue.size() > 0) {
                			//获取一个对象，但不从队列删除
                			LogData nextlogData = logQueue.peek();
                			//写日志，这个过程阻塞的
                			asyncWriteLog(nextlogData);
                			//发送完移除对象，防止开启多个任务
                			logQueue.poll();
                		}
                		//消费完了
                		hasConsumer = false;
                    }
                }.start();
    		}
    	} else {
    		//如果队列不为空，则只需要添加到队列即可
    		logQueue.add(logData);
    	}
    }

}