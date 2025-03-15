package dep.mgmt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class AppDataLatestVersions implements Serializable {
  private final LatestVersionServers latestVersionServers;
  private final LatestVersionTools latestVersionTools;
  private final LatestVersionGithubActions latestVersionGithubActions;
  private final LatestVersionLanguages latestVersionLanguages;

  @JsonCreator
  public AppDataLatestVersions(
      @JsonProperty("latestVersionServers") final LatestVersionServers latestVersionServers,
      @JsonProperty("latestVersionTools") final LatestVersionTools latestVersionTools,
      @JsonProperty("latestVersionGithubActions")
          final LatestVersionGithubActions latestVersionGithubActions,
      @JsonProperty("latestVersionLanguages") final LatestVersionLanguages latestVersionLanguages) {
    this.latestVersionServers = latestVersionServers;
    this.latestVersionTools = latestVersionTools;
    this.latestVersionGithubActions = latestVersionGithubActions;
    this.latestVersionLanguages = latestVersionLanguages;
  }

  public LatestVersionServers getLatestVersionServers() {
    return latestVersionServers;
  }

  public LatestVersionTools getLatestVersionTools() {
    return latestVersionTools;
  }

  public LatestVersionGithubActions getLatestVersionGithubActions() {
    return latestVersionGithubActions;
  }

  public LatestVersionLanguages getLatestVersionLanguages() {
    return latestVersionLanguages;
  }

  @Override
  public String toString() {
    return "AppDataLatestVersions{"
        + "latestVersionServers="
        + latestVersionServers
        + ", latestVersionTools="
        + latestVersionTools
        + ", latestVersionGithubActions="
        + latestVersionGithubActions
        + ", latestVersionLanguages="
        + latestVersionLanguages
        + '}';
  }

  public static class LatestVersionServers implements Serializable {
    private final LatestVersion nginx;

    @JsonCreator
    public LatestVersionServers(@JsonProperty("nginx") final LatestVersion nginx) {
      this.nginx = nginx;
    }

    public LatestVersion getNginx() {
      return nginx;
    }

    @Override
    public String toString() {
      return "LatestVersionServers{" + "nginx=" + nginx + '}';
    }
  }

  public static class LatestVersionTools implements Serializable {
    private final LatestVersion gradle;
    private final LatestVersion flyway;

    @JsonCreator
    public LatestVersionTools(
        @JsonProperty("gradle") final LatestVersion gradle,
        @JsonProperty("flyway") final LatestVersion flyway) {
      this.gradle = gradle;
      this.flyway = flyway;
    }

    public LatestVersion getGradle() {
      return gradle;
    }

    public LatestVersion getFlyway() {
      return flyway;
    }

    @Override
    public String toString() {
      return "LatestVersionTools{" + "gradle=" + gradle + ", flyway=" + flyway + '}';
    }
  }

  public static class LatestVersionGithubActions implements Serializable {
    private final LatestVersion checkout;
    private final LatestVersion setupJava;
    private final LatestVersion setupGradle;
    private final LatestVersion setupNode;
    private final LatestVersion setupPython;
    private final LatestVersion codeql;

    @JsonCreator
    public LatestVersionGithubActions(
        @JsonProperty("checkout") final LatestVersion checkout,
        @JsonProperty("setupJava") final LatestVersion setupJava,
        @JsonProperty("setupGradle") final LatestVersion setupGradle,
        @JsonProperty("setupNode") final LatestVersion setupNode,
        @JsonProperty("setupPython") final LatestVersion setupPython,
        @JsonProperty("codeql") final LatestVersion codeql) {
      this.checkout = checkout;
      this.setupJava = setupJava;
      this.setupGradle = setupGradle;
      this.setupNode = setupNode;
      this.setupPython = setupPython;
      this.codeql = codeql;
    }

    public LatestVersion getCheckout() {
      return checkout;
    }

    public LatestVersion getSetupJava() {
      return setupJava;
    }

    public LatestVersion getSetupGradle() {
      return setupGradle;
    }

    public LatestVersion getSetupNode() {
      return setupNode;
    }

    public LatestVersion getSetupPython() {
      return setupPython;
    }

    public LatestVersion getCodeql() {
      return codeql;
    }

    @Override
    public String toString() {
      return "LatestVersionGithubActions{"
          + "checkout="
          + checkout
          + ", setupJava="
          + setupJava
          + ", setupGradle="
          + setupGradle
          + ", setupNode="
          + setupNode
          + ", setupPython="
          + setupPython
          + ", codeql="
          + codeql
          + '}';
    }
  }

  public static class LatestVersionLanguages implements Serializable {
    private final LatestVersion java;
    private final LatestVersion node;
    private final LatestVersion python;

    @JsonCreator
    public LatestVersionLanguages(
        @JsonProperty("java") final LatestVersion java,
        @JsonProperty("node") final LatestVersion node,
        @JsonProperty("python") final LatestVersion python) {
      this.java = java;
      this.node = node;
      this.python = python;
    }

    public LatestVersion getJava() {
      return java;
    }

    public LatestVersion getNode() {
      return node;
    }

    public LatestVersion getPython() {
      return python;
    }

    @Override
    public String toString() {
      return "LatestVersionLanguages{"
          + "java="
          + java
          + ", node="
          + node
          + ", python="
          + python
          + '}';
    }
  }
}
