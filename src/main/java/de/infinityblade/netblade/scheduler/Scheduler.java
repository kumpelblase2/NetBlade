package de.infinityblade.netblade.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import de.infinityblade.netblade.LogManager;

public class Scheduler
{
    private final AtomicInteger m_ids = new AtomicInteger(1);
    private final PriorityQueue<NetBladeTask> m_waiting;
    private List<NetBladeTask> m_temp;
    private List<NetBladeTask> m_pending;
    private long m_currentTick = 1;

    public Scheduler()
    {
        this.m_waiting = new PriorityQueue<NetBladeTask>(10, new Comparator<NetBladeTask>() {
            @Override
            public int compare(NetBladeTask o1, NetBladeTask o2)
            {
                return (int)(o1.getNextRun() - o2.getNextRun());
            }
        });
        this.m_temp = new ArrayList<NetBladeTask>();
        this.m_pending = new ArrayList<NetBladeTask>();
    }

    public void tick()
    {
        handlePending();
        while(this.hasWork())
        {
            NetBladeTask task = this.m_waiting.poll();
            LogManager.getLogger().finest("Running task with id " + task.getID());
            task.run();
            if(task.canContinue())
                this.m_temp.add(task);
        }

        for(NetBladeTask task : this.m_temp)
        {
            task.setNextRun(this.m_currentTick + task.getPeriod());
            this.m_waiting.offer(task);
        }

        this.m_temp.clear();
        this.m_currentTick++;
    }

    private void handlePending()
    {
        synchronized(this)
        {
            for(NetBladeTask task : this.m_pending)
            {
                this.m_waiting.offer(task);
            }

            this.m_pending.clear();
        }
    }

    private boolean hasWork()
    {
        return !this.m_waiting.isEmpty() && this.m_waiting.peek().getNextRun() <= this.m_currentTick;
    }

    public void scheduleTask(Runnable inTask)
    {
        this.scheduleTask(inTask, 1);
    }

    public void scheduleTask(Runnable inTask, long inDelay)
    {
        this.scheduleTask(inTask, inDelay, 0);
    }

    public void scheduleTask(Runnable inTask, long inDelay, long inPeriod)
    {
        synchronized(this)
        {
            this.m_pending.add(new NetBladeTask(this.m_ids.incrementAndGet(), inTask, this.m_currentTick + inDelay, inPeriod));
        }
    }

    public void cancelTask(int inID)
    {
        synchronized(this)
        {
            //TODO
        }
    }
}