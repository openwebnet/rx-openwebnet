package com.github.niqdev.openwebnet;

/*
 * @see
 *  - http://joel-costigliola.github.io/assertj/index.html
 *  - http://www.codeaffine.com/2014/07/28/clean-junit-throwable-tests-with-java-8-lambdas/
 *  - http://blog.codeleak.pl/2014/07/junit-testing-exception-with-java-8-and-lambda-expressions.html
 */
public class ThrowableCaptor {

    public interface Actor {
        void act() throws Throwable;
    }

    public static Throwable captureThrowable(Actor actor) {
        Throwable result = null;
        try {
            actor.act();
        } catch (Throwable throwable) {
            result = throwable;
        }
        return result;
    }

}