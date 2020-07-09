package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    private static final Logger LOG = LoggerFactory.getLogger(AlertRabbit.class);
    private static String fileRabbit = Objects.requireNonNull(AlertRabbit.class.getClassLoader().
            getResource("rabbit.properties")).getFile();

    protected AlertRabbit() {
        throw new IllegalStateException("Utility class");
    }

    public static void main(String[] args) {
        try {
            SqlRuParse.main(new String[0]);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(getInterval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(5000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private static int getInterval() {
        String time = "0";
        try (FileInputStream in = new FileInputStream(fileRabbit)) {
            Properties cfg = new Properties();
            cfg.load(in);
            time = cfg.getProperty("rabbit.interval");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return Integer.parseInt(time);
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode() + " hashCode ");
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());
        }
    }
}
