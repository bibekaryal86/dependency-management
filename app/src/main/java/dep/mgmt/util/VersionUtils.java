package dep.mgmt.util;

import dep.mgmt.model.enums.RequestParams;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.List;
import java.util.stream.Stream;

public class VersionUtils {

  public static boolean isCheckPreReleaseVersion(final String version) {
    String v = version.toLowerCase().replace("release", "");

    if (v.contains("alpha") || v.contains("beta") || v.contains("rc") || v.contains("snapshot")) {
      return true;
    }

    if (v.matches(".*[._-]?[abm]\\d+$")) {
      return true;
    }

    if (v.matches(".*[._-][ab][._-].*")) {
      return true;
    }

    return false;
  }


  public static String getVersionToCompare(final String version) {
    List<String> strList = Stream.of(version.split("\\.")).limit(3).toList();
    StringBuilder sb = new StringBuilder();
    for (String s : strList) {
      try {
        if (Integer.parseInt(s) < 10) {
          sb.append("0").append(s);
        } else {
          sb.append(s);
        }
      } catch (NumberFormatException ignored) {
        // ignore exception
      }
    }
    return sb.toString();
  }

  public static boolean isRequiresUpdate(final String currentVersion, final String latestVersion) {
    if (CommonUtilities.isEmpty(currentVersion) || CommonUtilities.isEmpty(latestVersion)) {
      return false;
    }
    return getVersionToCompare(latestVersion).compareTo(getVersionToCompare(currentVersion)) > 0;
  }

  public static boolean checkDependenciesUpdate(final RequestParams.UpdateType updateType) {
    return updateType == RequestParams.UpdateType.ALL
        || updateType == RequestParams.UpdateType.GRADLE
        || updateType == RequestParams.UpdateType.NODE
        || updateType == RequestParams.UpdateType.PYTHON;
  }

  /**
   * @param versionFull eg: 3.12 or 3.12.7
   * @return eg: 312 or 3.12
   */
  public static String getVersionMajorMinor(final String versionFull, final boolean includePeriod) {
    String[] parts = versionFull.split("\\.");

    if (parts.length >= 2) {
      if (includePeriod) {
        return parts[0] + "." + parts[1];
      } else {
        return parts[0] + parts[1];
      }
    } else {
      return versionFull;
    }
  }

  public static int compareVersions(String version1, String version2) {
    String[] v1Parts = version1.split("\\.");
    String[] v2Parts = version2.split("\\.");

    int length =
        Math.max(v1Parts.length, v2Parts.length); // Compare based on the longest version string
    for (int i = 0; i < length; i++) {
      int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
      int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

      if (v1Part < v2Part) {
        return -1; // version1 is smaller
      } else if (v1Part > v2Part) {
        return 1; // version1 is greater
      }
    }
    return 0; // Both versions are equal
  }
}
