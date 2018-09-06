package lava.core;

/**
 * Created by xie,pengqi on 2018/9/6.
 */
public class Catchee  extends RuntimeException{
    private Object value;
    public Object getValue(){
        return this.value;
    }
    public Catchee(Object obj) {
        this.value = obj;
    }
}
