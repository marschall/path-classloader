package com.github.marschall.pathclassloader;

import java.util.concurrent.Callable;

public class HelloWorld implements Callable<String> {

  @Override
  public String call() throws Exception {
    return "Hello World";
  }

}