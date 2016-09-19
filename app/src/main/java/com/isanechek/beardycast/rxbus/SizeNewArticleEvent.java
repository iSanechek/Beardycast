package com.isanechek.beardycast.rxbus;

/**
 * Created by isanechek on 17.09.16.
 */
public class SizeNewArticleEvent {
    public SizeNewArticleEvent() {}

    public static class Message {
        public final int count;

        public Message(int count) {
            this.count = count;
        }
    }
}
