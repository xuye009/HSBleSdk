package com.handscape.sdk.util;

import java.lang.ref.WeakReference;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 单任务执行类
 */
public class HSSingleTaskManager {

    private LinkedBlockingDeque<WeakReference<SingleTask>> singleTaskMap;

    public static HSSingleTaskManager getnewInstance() {
        return new HSSingleTaskManager();
    }

    private HSSingleTaskManager() {
        singleTaskMap = new LinkedBlockingDeque<>();
    }

    public void addTask(int id, Runnable runnable) throws Exception {
        WeakReference<SingleTask> taskWeakReference = new WeakReference<>(new SingleTask(id, runnable));
        if (singleTaskMap.contains(taskWeakReference)) {
            throw new Exception("task is add");
        }
        singleTaskMap.put(taskWeakReference);
    }

    public void removeTask(int id) {
        WeakReference<SingleTask> tesk = new WeakReference<>(new SingleTask(id, null));
        singleTaskMap.remove(tesk);
    }

    public int getTaskSize(){
        if(singleTaskMap!=null){
            return singleTaskMap.size();
        }
        return 0;
    }

    public boolean hasNext() {
        if (singleTaskMap == null || singleTaskMap.size() == 0) {
            return false;
        }
        return true;
    }

    public void runTask() {
        SingleTask singleT = null;
        WeakReference<SingleTask> task= singleTaskMap.getFirst();
        if(task!=null){
            singleT=task.get();
            singleTaskMap.remove(task);
        }
        if (singleT != null) {
            singleT.run();
        }
    }

    class SingleTask {
        private int id;
        private Runnable runnable;

        public SingleTask(int id, Runnable runnable) {
            this.id = id;
            this.runnable = runnable;
        }

        public void run() {
            runnable.run();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SingleTask) {
                SingleTask task = (SingleTask) obj;
                return task.id == id;
            }
            return super.equals(obj);
        }
    }

}
