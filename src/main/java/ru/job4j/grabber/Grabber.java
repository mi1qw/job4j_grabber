package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.SqlRuParse.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
    private static final String JOBOFFER = "https://www.sql.ru/forum/job-offers/";
    private static String app = Objects.requireNonNull(Grabber.class.getClassLoader().
            getResource("app.properties")).getFile();
    private static Properties cfg = new Properties();

    /**
     * store.
     *
     * @return store store
     */
    public Store store() {
        System.out.println("store!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("cfg " + cfg.size() + "  " + cfg.getClass());
        return new PsqlStore(cfg);
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
        System.out.println(" cfg.load(in);");
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
        data.put("scheduler", scheduler);
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
        System.out.println("1 main запуск");
        Grabber grab = new Grabber();
        System.out.println("2 main запуск");
        grab.cfg();
        System.out.println("cfg " + cfg.size() + "  " + cfg.getClass());
        System.out.println("3 main запуск");
        Scheduler scheduler = grab.scheduler();
        System.out.println("4 main запуск");
        Store store = grab.store();
        System.out.println("5 main запуск");

        System.out.println("main!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("store " + store.getClass());
        grab.init(new SqlRuParse(), store, scheduler);
    }

    public static class GrabJob implements Job {
        private static int curPage = 1;
        private static int maxPage = 1;
        private static int currExecJobs = 1;
        private static int needPages = 3;

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

            System.out.println("execute!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("store " + store.getClass());

            Parse parse = (Parse) map.get("parse");
            Scheduler scheduler = (Scheduler) map.get("scheduler");
            try {
                if (scheduler.getCurrentlyExecutingJobs().size() <= currExecJobs) {
                    List<Post> listPage;
                    List<Post> list = new ArrayList<>();
                    maxPage = parse.maxPage();
                    for (int n = 0; curPage <= maxPage && n < needPages; ++curPage, n++) {
                        listPage = parse.list(JOBOFFER.concat(String.valueOf(curPage)));
                        list.addAll(listPage);
                        LOG.info("{} Vacancies {}", curPage, list.size());
                    }
                    store.save(list);
                } else {
                    LOG.warn("More then one Job at once !!!!!!!!!!!!!!!!!!!!");
                }
            } catch (SchedulerException e) {
                LOG.error(e.getMessage(), e);
            }
            if (curPage > maxPage) {
                try {
                    scheduler.shutdown();
                    store.close();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }
}
