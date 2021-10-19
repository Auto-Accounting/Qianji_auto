package cn.dreamn.qianji_auto.ui.utils.task;

public class ConsumptionTask {
    /*
       子ID，在列队中保持唯一。对应的是一条数据。
     */
    public String taskNo;
    /**
     * 任务执行方法体
     */
    public RunBody runnable;
    /**
     * 父ID，通常以组的方式出现，关联一组相关的数据
     */
    public String planNo;
}
