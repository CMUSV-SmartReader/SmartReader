
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;

import play.GlobalSettings;
import util.MorphiaObject;

public class Global extends GlobalSettings {

    @Override
    public void onStart(play.Application arg0) {
        super.beforeStart(arg0);
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
        MorphiaObject.setUp();
    }
}
