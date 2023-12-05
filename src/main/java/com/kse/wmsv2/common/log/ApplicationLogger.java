package com.kse.wmsv2.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLogger
{

	/**
	 * ロガー
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * コンストラクタ
	 * @param logger
	 */
	public ApplicationLogger(Logger logger){
	    this.logger = logger;
	}


	/**
	 * デバッグログを出力します。
	 * @param msg メッセージ
	 */
	public void debug(String msg) {
		logger.debug(msg);
	}

	/**
	 * デバッグログを出力します。
	 * @param format フォーマット
	 * @param arg フォーマット引数
	 */
	public void debug(String format, Object arg) {
		logger.debug(format, arg);

	}

	/**
	 * デバッグログを出力します。
	 * @param format フォーマット
	 * @param arg1 フォーマット引数1
	 * @param arg2 フォーマット引数2
	 */
    public void debug(String format, Object arg1, Object arg2) {
		logger.debug(format, arg1,arg2);
    }

    /**
	 * デバッグログを出力します。
	 * @param format フォーマット
     * @param arguments フォーマット引数...
     */
	public void debug(String format, Object... arguments)
	{
		logger.debug(format, arguments);
	}



	/**
	 * 情報ログを出力します。
	 * @param msg メッセージ
	 */
	public void info(String msg) {
		logger.info(msg);
	}

	/**
	 * 情報ログを出力します。
	 * @param format フォーマット
	 * @param arg フォーマット引数
	 */
	public void info(String format, Object arg) {
		logger.info(format, arg);

	}

	/**
	 * 情報ログを出力します。
	 * @param format フォーマット
	 * @param arg1 フォーマット引数1
	 * @param arg2 フォーマット引数2
	 */
    public void info(String format, Object arg1, Object arg2) {
		logger.info(format, arg1,arg2);
    }

    /**
	 * 情報ログを出力します。
	 * @param format フォーマット
     * @param arguments フォーマット引数...
     */
	public void info(String format, Object... arguments)
	{
		logger.info(format, arguments);
	}


	/**
	 * 警告ログを出力します。
	 * @param msg メッセージ
	 */
	public void warn(String msg) {
		logger.warn(msg);
	}

	/**
	 * 警告ログを出力します。
	 * @param format フォーマット
	 * @param arg フォーマット引数
	 */
	public void warn(String format, Object arg) {
		logger.warn(format, arg);

	}

	/**
	 * 警告ログを出力します。
	 * @param format フォーマット
	 * @param arg1 フォーマット引数1
	 * @param arg2 フォーマット引数2
	 */
    public void warn(String format, Object arg1, Object arg2) {
		logger.warn(format, arg1,arg2);
    }

    /**
	 * 警告ログを出力します。
	 * @param format フォーマット
     * @param arguments フォーマット引数...
     */
	public void warn(String format, Object... arguments)
	{
		logger.warn(format, arguments);
	}


	/**
	 * エラーログを出力します。
	 * @param msg メッセージ
	 */
	public void error(String msg) {
		logger.error(msg);
	}

	/**
	 * エラーログを出力します。
	 * @param format フォーマット
	 * @param arg フォーマット引数
	 */
	public void error(String format, Object arg) {
		logger.error(format, arg);

	}

	/**
	 * エラーログを出力します。
	 * @param format フォーマット
	 * @param arg1 フォーマット引数1
	 * @param arg2 フォーマット引数2
	 */
    public void error(String format, Object arg1, Object arg2) {
		logger.error(format, arg1,arg2);
    }

    /**
	 * エラーログを出力します。
	 * @param format フォーマット
     * @param arguments フォーマット引数...
     */
	public void error(String format, Object... arguments)
	{
		logger.error(format, arguments);
	}


	/**
	 * トレースログを出力します。
	 * @param msg メッセージ
	 */
	public void trace(String msg) {
		logger.trace(msg);
	}

	/**
	 * トレースログを出力します。
	 * @param format フォーマット
	 * @param arg フォーマット引数
	 */
	public void trace(String format, Object arg) {
		logger.trace(format, arg);

	}

	/**
	 * トレースログを出力します。
	 * @param format フォーマット
	 * @param arg1 フォーマット引数1
	 * @param arg2 フォーマット引数2
	 */
    public void trace(String format, Object arg1, Object arg2) {
		logger.trace(format, arg1,arg2);
    }

    /**
	 * トレースログを出力します。
	 * @param format フォーマット
     * @param arguments フォーマット引数...
     */
	public void trace(String format, Object... arguments)
	{
		logger.trace(format, arguments);
	}


	/**
	 * 例外ログを出力します。
	 * @param e 例外
	 */
	public void excepion(Exception e) {
	    if (e != null) {
	        if (e.getMessage() != null) {
	            logger.warn("[例外]"+e.getMessage(), e);
	        } else {
                logger.warn("[例外]エラーメッセージは無し", e);
	        }
	    }
	}
}

