package dep.mgmt.model.entity;

import dep.mgmt.model.LatestVersion;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class LatestVersionEntity implements Serializable {
  @BsonId private final ObjectId id;
  private final LocalDateTime updateDateTime;

  // servers
  private final LatestVersion nginx;
  // tools
  private final LatestVersion gradle;
  private final LatestVersion flyway;
  // actions
  private final LatestVersion checkout;
  private final LatestVersion setupJava;
  private final LatestVersion setupGradle;
  private final LatestVersion setupNode;
  private final LatestVersion setupPython;
  private final LatestVersion codeql;
  // languages
  private final LatestVersion java;
  private final LatestVersion node;
  private final LatestVersion python;

  public LatestVersionEntity(
      final ObjectId id,
      final LocalDateTime updateDateTime,
      final LatestVersion nginx,
      final LatestVersion gradle,
      final LatestVersion flyway,
      final LatestVersion checkout,
      final LatestVersion setupJava,
      final LatestVersion setupGradle,
      final LatestVersion setupNode,
      final LatestVersion setupPython,
      final LatestVersion codeql,
      final LatestVersion java,
      final LatestVersion node,
      final LatestVersion python) {
    this.id = id;
    this.updateDateTime = updateDateTime;
    this.nginx = nginx;
    this.gradle = gradle;
    this.flyway = flyway;
    this.checkout = checkout;
    this.setupJava = setupJava;
    this.setupGradle = setupGradle;
    this.setupNode = setupNode;
    this.setupPython = setupPython;
    this.codeql = codeql;
    this.java = java;
    this.node = node;
    this.python = python;
  }

  public ObjectId getId() {
    return id;
  }

  public LocalDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public LatestVersion getNginx() {
    return nginx;
  }

  public LatestVersion getGradle() {
    return gradle;
  }

  public LatestVersion getFlyway() {
    return flyway;
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
    return "LatestVersions{"
        + "id="
        + id
        + ", updateDateTime="
        + updateDateTime
        + ", nginx="
        + nginx
        + ", gradle="
        + gradle
        + ", flyway="
        + flyway
        + ", checkout="
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
        + ", java="
        + java
        + ", node="
        + node
        + ", python="
        + python
        + '}';
  }
}
