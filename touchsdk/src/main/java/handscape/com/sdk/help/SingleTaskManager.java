package handscape.com.sdk.help;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

/**
 * 单任务
 */
public class SingleTaskManager {

    private LinkedHashMap<Integer,WeakReference<SingleTask>> singleTaskMap;
    public static SingleTaskManager getnewInstance(){
        return new SingleTaskManager();
    }

    private SingleTaskManager(){
        singleTaskMap=new LinkedHashMap<>();
    }

    public void addTask(int id,Runnable runnable) throws Exception{
        if(singleTaskMap.get(id)!=null){
            throw new Exception("task is add");
        }
        SingleTask task=new SingleTask(id,runnable);
        WeakReference<SingleTask> taskWeakReference=new WeakReference<>(task);
        singleTaskMap.put(id,taskWeakReference);
    }

    public void removeTask(int id){
        singleTaskMap.remove(id);
    }


    public boolean hasnext(){
        if(singleTaskMap.keySet()!=null&&singleTaskMap.keySet().size()>0){
            return true;
        }
        return  false;
    }

    class SingleTask{
        private int id;
        private Runnable runnable;
        public SingleTask(int id,Runnable runnable){
            this.id=id;
            this.runnable=runnable;
        }

        public void run() throws Exception{
            runnable.run();
        }
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof  SingleTask){
                SingleTask task= (SingleTask) obj;
                return task.id==id;
            }
            return super.equals(obj);
        }
    }

}
