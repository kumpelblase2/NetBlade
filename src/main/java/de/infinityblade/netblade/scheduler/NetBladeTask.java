package de.infinityblade.netblade.scheduler;

public class NetBladeTask implements Runnable
{
    private final int m_id;
    private final Runnable m_realTask;
    private long m_nextRun;
    private long m_period = -1;

    public NetBladeTask(int inID, Runnable inTask, long inNextRun)
    {
        this(inID, inTask, inNextRun, 0);
    }

    public NetBladeTask(int inID, Runnable inTask, long inNextRun, long inPeriod)
    {
        this.m_id = inID;
        this.m_realTask = inTask;
        this.m_nextRun = inNextRun;
        this.m_period = inPeriod;
    }

    public int getID()
    {
        return this.m_id;
    }

    public long getNextRun()
    {
        return this.m_nextRun;
    }

    public void setNextRun(long inTicks)
    {
        this.m_nextRun = inTicks;
    }

    public long getPeriod()
    {
        return this.m_period;
    }

    public Runnable getRealTask()
    {
        return this.m_realTask;
    }

    @Override
    public void run()
    {
        this.m_realTask.run();
    }

    public boolean canContinue()
    {
        return this.m_period != 0;
    }
}