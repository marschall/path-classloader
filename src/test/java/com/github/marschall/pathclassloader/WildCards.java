package com.github.marschall.pathclassloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

public class WildCards {

  public static void main(String[] args) throws IOException {
    URL url = new URL("file:////Users/marschall/git/path-classloader/target/test-classes/");
//    try (URLClassLoader classLoder = new URLClassLoader(new URL[]{url})) {
    try (URLClassLoader classLoder = new URLClassLoader(new URL[0])) {
      System.out.println("/subfolder/*:");
      System.out.println(Collections.list(classLoder.getResources("/subfolder/*")));
      System.out.println("subfolder/*:");
      System.out.println(Collections.list(classLoder.getResources("subfolder/*")));
      System.out.println("/subfolder:");
      System.out.println(Collections.list(classLoder.getResources("/subfolder")));
      System.out.println("subfolder:");
      System.out.println(Collections.list(classLoder.getResources("subfolder")));
      System.out.println("subfolder/:");
      System.out.println(Collections.list(classLoder.getResources("/subfolder/")));
      System.out.println("subfolder/:");
      System.out.println(Collections.list(classLoder.getResources("subfolder/")));
      System.out.println(":");
      System.out.println(Collections.list(classLoder.getResources("")));
//      System.out.println(Collections.list(classLoder.getResources(null)));
      System.out.println("file.txt:");
      System.out.println(Collections.list(classLoder.getResources("file.txt")));
      System.out.println("subfolder/file.txt");
      System.out.println(Collections.list(classLoder.getResources("subfolder/file.txt")));
      System.out.println("*/file.txt");
      System.out.println(Collections.list(classLoder.getResources("*/file.txt")));
      System.out.println("subfolder/fil?.txt");
      System.out.println(Collections.list(classLoder.getResources("subfolder/fil?.txt")));
    }

  }

}
