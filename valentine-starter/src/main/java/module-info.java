@SuppressWarnings({"requires-automatic"})
module lwohvye.valentine.starter {
    requires lwohvye.unicorn.system;
    requires lwohvye.unicorn.core;
    requires lombok;
//    requires com.mzt.logapi;
//    requires captcha;

    exports com.unicorn.vs.rest to spring.beans, spring.aop, spring.web;

    // maven需要opens resources，而gradle不需要
//    opens config;
    opens com.unicorn;
    opens com.unicorn.vs.rest to spring.core;
}
