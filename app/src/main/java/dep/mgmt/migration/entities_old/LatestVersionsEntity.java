package dep.mgmt.migration.entities_old;

import java.time.LocalDateTime;

import dep.mgmt.model.LatestVersion;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class LatestVersionsEntity {
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

  public LatestVersionsEntity() {}

  public LatestVersionsEntity(
      ObjectId id,
      LocalDateTime updateDateTime,
      LatestVersion nginx,
      LatestVersion gradle,
      LatestVersion flyway,
      LatestVersion checkout,
      LatestVersion setupJava,
      LatestVersion setupGradle,
      LatestVersion setupNode,
      LatestVersion setupPython,
      LatestVersion codeql,
      LatestVersion java,
      LatestVersion node,
      LatestVersion python) {
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

  public void setId(ObjectId id) {
    this.id = id;
  }

  public LocalDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(LocalDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  public LatestVersion getNginx() {
    return nginx;
  }

  public void setNginx(LatestVersion nginx) {
    this.nginx = nginx;
  }

  public LatestVersion getGradle() {
    return gradle;
  }

  public void setGradle(LatestVersion gradle) {
    this.gradle = gradle;
  }

  public LatestVersion getFlyway() {
    return flyway;
  }

  public void setFlyway(LatestVersion flyway) {
    this.flyway = flyway;
  }

  public LatestVersion getCheckout() {
    return checkout;
  }

  public void setCheckout(LatestVersion checkout) {
    this.checkout = checkout;
  }

  public LatestVersion getSetupJava() {
    return setupJava;
  }

  public void setSetupJava(LatestVersion setupJava) {
    this.setupJava = setupJava;
  }

  public LatestVersion getSetupGradle() {
    return setupGradle;
  }

  public void setSetupGradle(LatestVersion setupGradle) {
    this.setupGradle = setupGradle;
  }

  public LatestVersion getSetupNode() {
    return setupNode;
  }

  public void setSetupNode(LatestVersion setupNode) {
    this.setupNode = setupNode;
  }

  public LatestVersion getSetupPython() {
    return setupPython;
  }

  public void setSetupPython(LatestVersion setupPython) {
    this.setupPython = setupPython;
  }

  public LatestVersion getCodeql() {
    return codeql;
  }

  public void setCodeql(LatestVersion codeql) {
    this.codeql = codeql;
  }

  public LatestVersion getJava() {
    return java;
  }

  public void setJava(LatestVersion java) {
    this.java = java;
  }

  public LatestVersion getNode() {
    return node;
  }

  public void setNode(LatestVersion node) {
    this.node = node;
  }

  public LatestVersion getPython() {
    return python;
  }

  public void setPython(LatestVersion python) {
    this.python = python;
  }
}
