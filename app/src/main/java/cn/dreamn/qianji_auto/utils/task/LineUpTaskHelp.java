package cn.dreamn.qianji_auto.utils.task;

import android.text.TextUtils;

import java.util.Iterator;
import java.util.LinkedList;


public class LineUpTaskHelp {

    private static LineUpTaskHelp lineUpTaskHelp;
    /**
     * 排队容器, 需要排队的任务才会加入此列表
     */
    private final LinkedList<ConsumptionTask> lineUpBeans;
    /**
     * 执行下一个任务任务的监听器
     */
    private OnTaskListener onTaskListener;

    public LineUpTaskHelp() {
        // app 只会执行一直
        lineUpBeans = new LinkedList<>();
    }

    public static LineUpTaskHelp getInstance() {
        if (lineUpTaskHelp == null) {
            lineUpTaskHelp = new LineUpTaskHelp();
        }
        return lineUpTaskHelp;
    }

    public LineUpTaskHelp setOnTaskListener(OnTaskListener onTaskListener) {
        this.onTaskListener = onTaskListener;
        return this;
    }

    /**
     * 加入排队
     *
     * @param task 一个任务
     */
    public void addTask(ConsumptionTask task) {
        android.util.Log.i("Post", "任务加入排队中" + task.taskNo);
        if (!checkTask()) {
            if (onTaskListener != null) onTaskListener.exNextTask(task);
        }
        lineUpBeans.addLast(task);
    }

    /**
     * 检查列表中是否有任务正在执行。
     *
     * @return true 表示有任务正在执行。
     */
    public boolean checkTask() {
        boolean isTask = false;
        if (lineUpBeans.size() > 0) isTask = true;
        return isTask;
    }


    /**
     * 得到下一个执行的计划，
     *
     * @return 返回下一个将要执行的任务， 返回null ,代表没有任务可以执行了
     */
    public ConsumptionTask getFirst() {
        return lineUpBeans.getFirst();
    }


    /**
     * 执行成功之后，删除排队中的任务。
     *
     * @param task
     */
    private void deleteTask(ConsumptionTask task) {
        for (int i = 0; i < lineUpBeans.size(); i++) {
            ConsumptionTask consumptionTask = lineUpBeans.get(i);
            if (task.taskNo.equals(consumptionTask.taskNo)) {
                lineUpBeans.remove(consumptionTask); // 删除任务
                break;
            }
        }
    }


    /**
     * 删除对应父id的所有任务
     *
     * @param
     */
    public void deletePlanNoAll(String planNo) {
        Iterator<ConsumptionTask> iterator = lineUpBeans.iterator();
        if (TextUtils.isEmpty(planNo)) return;
        while (iterator.hasNext()) {
            ConsumptionTask task = iterator.next();
            if (task.planNo.equals(planNo)) {
                iterator.remove();
            }
        }
    }

    /**
     * 删除对应子id的项
     *
     * @param
     */
    public void deleteTaskNoAll(String taskNo) {
        Iterator<ConsumptionTask> iterator = lineUpBeans.iterator();
        if (TextUtils.isEmpty(taskNo)) return;
        while (iterator.hasNext()) {
            ConsumptionTask task = iterator.next();
            if (task.taskNo.equals(taskNo)) {
                iterator.remove();
                break;
            }
        }
    }


    /**
     * 外部调用， 当执行完成一个任务调用
     */
    public void exOk(ConsumptionTask task) {
        deleteTask(task); // 删除已经执行完成的任务。
        if (checkTask()) {
            // 发现还有任务
            if (onTaskListener != null) {
                onTaskListener.exNextTask(lineUpBeans.getFirst());
            }
        } else {
            if (onTaskListener != null) {
                onTaskListener.noTask();
            }
        }
    }


    /**
     * 当第一个任务执行完成，发现列队中还存在任务， 将继续执行下一个任务
     */
    public interface OnTaskListener {
        /**
         * 执行下一个任务
         *
         * @param task
         */
        void exNextTask(ConsumptionTask task);

        /**
         * 所有任务执行完成
         */
        void noTask();
    }


}
