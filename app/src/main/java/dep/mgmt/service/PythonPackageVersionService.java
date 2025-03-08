package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.model.web.PythonPackageSearchResponse;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonPackageVersionService {

  private static final Logger log = LoggerFactory.getLogger(PythonPackageVersionService.class);

  public String getLatestPackageVersion(final String name) {
    PythonPackageSearchResponse pythonPackageSearchResponse = getPythonPackageSearchResponse(name);

    if (pythonPackageSearchResponse == null
        || pythonPackageSearchResponse.getInfo() == null
        || pythonPackageSearchResponse.getInfo().getYanked()) {
      return null;
    }

    return pythonPackageSearchResponse.getInfo().getVersion();
  }

  private PythonPackageSearchResponse getPythonPackageSearchResponse(final String name) {
    try {
      final String url = String.format(ConstantUtils.PYPI_SEARCH_ENDPOINT, name);
      return Connector.sendRequest(url, Enums.HttpMethod.GET, new TypeReference<PythonPackageSearchResponse>() {}, null, null, null).responseBody();
    } catch (Exception ex) {
      log.error("ERROR in Get Python Package Search Response: [ {} ]", name, ex);
    }
    return null;
  }
}
