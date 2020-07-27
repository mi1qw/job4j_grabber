package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * The type Grabber.
 */
public class Grabber implements Grab {
    private static final Logger LOG = LoggerFactory.getLogger(Grabber.class);
    private static String app = Objects.requireNonNull(Grabber.class.getClassLoader().
            getResource("app.properties")).getFile();
    private final Properties cfg = new Properties();

    /**
     * store.
     *
     * @return store store
     */
    public Store store() {
        PsqlStore psqlStore = new PsqlStore(cfg);
        return null;
    }

    /**
     * Scheduler scheduler.
     *
     * @return the scheduler
     * @throws SchedulerException the scheduler exception
     */
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * Cfg.
     *
     * @throws IOException the io exception
     */
    public void cfg() throws IOException {
        try (InputStream in = new FileInputStream(app)) {
            cfg.load(in);
        }
    }

    /**
     * init.
     *
     * @param parse     parse
     * @param store     store
     * @param scheduler scheduler
     * @throws SchedulerException
     */
    @Override
    public void init(final Parse parse, final Store store, final Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception
     */
    public static void main(final String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new SqlRuParse(), store, scheduler);
    }

    public static class GrabJob implements Job {
        /**
         * execute.
         *
         * @param context context
         * @throws JobExecutionException
         */
        @Override
        public void execute(final JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("store");
            //TODO impl logic
        }
    }
}
