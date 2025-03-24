package dep.mgmt.model;

public class AppDataScriptFile {
  private final String scriptFileName;
  private final String scriptName;

  public AppDataScriptFile(final String scriptFileName) {
    this.scriptFileName = scriptFileName;
    String[] sfnArray = scriptFileName.split("\\.");
    this.scriptName =sfnArray[0];
  }

  public String getScriptFileName() {
    return scriptFileName;
  }

  public String getScriptName() {
    return scriptName;
  }

  @Override
  public String toString() {
    return "ScriptFile{" + "scriptFileName='" + scriptFileName + '\'' + ", scriptName=" + scriptName + '}';
  }
}
