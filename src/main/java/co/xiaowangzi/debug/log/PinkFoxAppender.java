package co.xiaowangzi.debug.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

import java.io.FileOutputStream;

public class PinkFoxAppender<E> extends UnsynchronizedAppenderBase<E> {

    private Encoder encoder;
    private String fileName;

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void append(E e) {
        ILoggingEvent event = (ILoggingEvent) e;
        byte[] bytes = this.encoder.encode(e);
        try {
            String s = new String(bytes, "utf-8");

            if (Level.INFO == event.getLevel()) {
                Log.info(s);
            } else if (Level.ERROR == event.getLevel()) {
                Log.error(s);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
