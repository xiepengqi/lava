package lava.core;

import lava.constant.Constants;

/**
 * Created by xie,pengqi on 2017/12/21.
 */
public class SysError extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SysError(Form form, String msg){
        super(form.getWhere() + ":" + form.see() + Constants.newLine + "	" + msg);
    }
}
