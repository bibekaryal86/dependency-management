package dep.mgmt.model.entity;

import dep.mgmt.model.LatestVersion;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class LatestVersionEntity implements Serializable {
  @BsonId private ObjectId id;
  private LocalDateTime updateDateTime;

  // servers
  private LatestVersion nginx;
  // tools
  private LatestVersion gradle;
  private LatestVersion flyway;
  // actions
  private LatestVersion checkout;
  private LatestVersion setupJava;
  private LatestVersion setupGradle;
  private LatestVersion setupNode;
  private LatestVersion setupPython;
  private LatestVersion codeql;
  // languages
  private LatestVersion java;
  private LatestVersion node;
  private LatestVersion python;

  public LatestVersionEntity() {}

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

  public void setId(final ObjectId id) {
    this.id = id;
  }

  public LocalDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(final LocalDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  public LatestVersion getNginx() {
    return nginx;
  }

  public void setNginx(final LatestVersion nginx) {
    this.nginx = nginx;
  }

  public LatestVersion getGradle() {
    return gradle;
  }

  public void setGradle(final LatestVersion gradle) {
    this.gradle = gradle;
  }

  public LatestVersion getFlyway() {
    return flyway;
  }

  public void setFlyway(final LatestVersion flyway) {
    this.flyway = flyway;
  }

  public LatestVersion getCheckout() {
    return checkout;
  }

  public void setCheckout(final LatestVersion checkout) {
    this.checkout = checkout;
  }

  public LatestVersion getSetupJava() {
    return setupJava;
  }

  public void setSetupJava(final LatestVersion setupJava) {
    this.setupJava = setupJava;
  }

  public LatestVersion getSetupGradle() {
    return setupGradle;
  }

  public void setSetupGradle(final LatestVersion setupGradle) {
    this.setupGradle = setupGradle;
  }

  public LatestVersion getSetupNode() {
    return setupNode;
  }

  public void setSetupNode(final LatestVersion setupNode) {
    this.setupNode = setupNode;
  }

  public LatestVersion getSetupPython() {
    return setupPython;
  }

  public void setSetupPython(final LatestVersion setupPython) {
    this.setupPython = setupPython;
  }

  public LatestVersion getCodeql() {
    return codeql;
  }

  public void setCodeql(final LatestVersion codeql) {
    this.codeql = codeql;
  }

  public LatestVersion getJava() {
    return java;
  }

  public void setJava(final LatestVersion java) {
    this.java = java;
  }

  public LatestVersion getNode() {
    return node;
  }

  public void setNode(final LatestVersion node) {
    this.node = node;
  }

  public LatestVersion getPython() {
    return python;
  }

  public void setPython(final LatestVersion python) {
    this.python = python;
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
