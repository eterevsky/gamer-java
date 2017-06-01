package gamer.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Version {
  public static String getVersion() {
    try {
      Enumeration<URL> resources =
          Version.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
      while (resources.hasMoreElements()) {
        Manifest manifest = new Manifest(resources.nextElement().openStream());
        Attributes attr = manifest.getMainAttributes();

        if ("gamer.App".equals(attr.getValue("Main-Class"))) {
          return attr.getValue("Implementation-Version");
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

}
