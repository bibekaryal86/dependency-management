package dep.mgmt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import dep.mgmt.model.web.ApiReleaseResponse;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.Connector;
import io.github.bibekaryal86.shdsvc.dtos.Enums;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerVersionService {
  private static final Logger log = LoggerFactory.getLogger(DockerVersionService.class);

  public boolean checkDockerVersionExists(final String library, final String tag) {
    return getDockerImageTagName(library, tag) != null;
  }

  private String getDockerImageTagName(final String library, final String tag) {
    final String url = String.format(ConstantUtils.DOCKER_TAG_LOOKUP_ENDPOINT, library, tag);
    try {
      ApiReleaseResponse apiReleaseResponse =
          Connector.sendRequestNoEx(
                  url,
                  Enums.HttpMethod.GET,
                  new TypeReference<ApiReleaseResponse>() {},
                  null,
                  null,
                  null)
              .responseBody();
      if (apiReleaseResponse != null && !CommonUtilities.isEmpty(apiReleaseResponse.getName())) {
        return apiReleaseResponse.getName().trim();
      }
    } catch (Exception ex) {
      log.error("Get Docker Image Tag: Library={} | Tag={}", library, tag, ex);
    }
    return null;
  }
}
