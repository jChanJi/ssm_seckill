package org.seckill.enums;

/**
 * 使用枚举表示我们的常量数据
 * Create By ChanJi on 2018/1/28
 */
public enum SeckillStatEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_REEOR(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATE_REWRITE(-3,"系统数据篡改")
    ;
    private int  state;
    private String stateInfo;

    SeckillStatEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public static SeckillStatEnum stateOf(int index){
        for(SeckillStatEnum state: values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
