//package ru.job4j.grabber;
//
//public class Aaaa {
//    private int curPage = 1;
//    private int maxPage = 1;
//    private int currExecJobs = 1;
//    private int needPages = 3;
//
//    public final void execute() {
//        for (int n = 0; curPage <= maxPage
//                && n < needPages;
//             ++curPage, n++) {
//            System.out.println(curPage + "  " + maxPage);
//            System.out.println(n + "  " + needPages);
//        }
//        //if (curPage > maxPage) {
//        //try {
//        //    scheduler.shutdown();
//        //    store.close();
//        //} catch (Exception e) {
//        //    LOG.error(e.getMessage(), e);
//        //}
//        //}
//        if (curPage == 1 && curPage == 2 && curPage == 3) {
//            System.out.println(1);
//        } else {
//            System.out.println(1);
//        }
//    }
//}
//
///**
// * 1    curPage<=maxPage&&n<needPages
// * curPage>maxPage&&n>needPages
// * curPage<=maxPage&&n>needPages
// * 1    curPage>maxPage&&n<needPages
// * <p>
// * true true
// * false false
// * true false
// * false true
// */
