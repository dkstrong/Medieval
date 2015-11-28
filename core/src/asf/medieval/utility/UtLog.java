package asf.medieval.utility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by daniel on 11/15/15.
 */
public enum UtLog {
	TRACE,
	INFO,
	WARNING,
	ERROR,
	OFF;

	public static UtLog logLevel = UtLog.INFO;

	public static void trace(Object message){
		doLog(UtLog.TRACE, String.valueOf(message), null);
	}

	public static void info(Object message){
		doLog(UtLog.INFO, String.valueOf(message), null);
	}

	public static void warning(Object message){
		doLog(UtLog.WARNING, String.valueOf(message), null);
	}

	public static void error(Object message){
		doLog(UtLog.ERROR, String.valueOf(message), null);
	}


	public static void trace(Object message, Throwable e){
		doLog(UtLog.TRACE, String.valueOf(message), e);
	}

	public static void info(Object message, Throwable e){
		doLog(UtLog.INFO, String.valueOf(message), e);
	}

	public static void warning(Object message, Throwable e){
		doLog(UtLog.WARNING, String.valueOf(message), e);
	}

	public static void error(Object message, Throwable e){
		doLog(UtLog.ERROR, String.valueOf(message), e);
	}

	private static final Date date = new Date();
	private static final SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss:SSS");  // "yyyy-MM-dd HH:mm:ss"

	private static void doLog(UtLog logType, String logText, Throwable logException) {
		if(logType.ordinal() < logLevel.ordinal())
			return;

		String threadName, className, methodName, fileName, lineNumber;
		try {
			Thread thread = Thread.currentThread();
			StackTraceElement element = thread.getStackTrace()[3];
			threadName = thread.getName();
			date.setTime(System.currentTimeMillis());
			className = element.getClassName();
			methodName = element.getMethodName();
			fileName = element.getFileName();
			lineNumber = String.valueOf(element.getLineNumber());
		} catch (Exception e) {
			className = "{UtLog}";
			threadName="";
			methodName = "log()";
			fileName = "{UtLogged File}";
			lineNumber = "NaN";
		}


		String[] splitClassName = className.split("\\.");
		String shortClassName = splitClassName[splitClassName.length - 1];
		String timeFormatted = formatter.format(date);

		System.out.printf("%-5s %-5s: %s [%s] %s\n", timeFormatted,shortClassName+"."+methodName, logText,threadName,logType);


		if (logException != null) {
			StringWriter sw = new StringWriter();
			logException.printStackTrace(new PrintWriter(sw));
			System.err.print(sw.toString());
		}
	}


}
