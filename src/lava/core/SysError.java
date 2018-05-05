package lava.core;

/**
 * Created by xie,pengqi on 2017/12/21.
 */
public class SysError extends RuntimeException{
    public SysError(Form form, String msg){
        super(form.getWhere() + ":" + form.see() + ":" + msg);
    }
}
