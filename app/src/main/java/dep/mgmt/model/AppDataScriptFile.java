package dep.mgmt.model;

import dep.mgmt.model.enums.RequestParams;

public class AppDataScriptFile {
  private final String scriptFileName;
  private final RequestParams.UpdateType type;

  public AppDataScriptFile(final String scriptFileName) {
    this.scriptFileName = scriptFileName;
    String[] sfnArray = scriptFileName.split("\\.");
    this.type = RequestParams.UpdateType.valueOf(sfnArray[0]);
  }

  public String getScriptFileName() {
    return scriptFileName;
  }

  public RequestParams.UpdateType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "ScriptFile{" + "scriptFileName='" + scriptFileName + '\'' + ", type=" + type + '}';
  }
}
