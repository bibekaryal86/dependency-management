package dep.mgmt.service;

import dep.mgmt.util.ConstantUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GcpRuntimesVersionService {

  private static final Logger log = LoggerFactory.getLogger(GcpRuntimesVersionService.class);

  public Map<String, String> getGcpRuntimesVersions() {
    final List<String> validKeys =
        List.of(ConstantUtils.JAVA_NAME, ConstantUtils.NODEJS_NAME, ConstantUtils.PYTHON_NAME);
    Map<String, String> latestRuntimesMap = new HashMap<>();

    final Document document = getGcpRuntimeSupportSchedule();

    if (document == null) {
      log.error("GCP Runtimes lookup Document is null...");
      return latestRuntimesMap;
    }

    final Elements tables = document.select("table");
    for (final Element table : tables) {
      latestRuntimesMap.putAll(parseRuntimeTables(table));
    }

    if (latestRuntimesMap.isEmpty()) {
      log.error("Latest GCP Runtimes Map is Empty...");
    } else {
      return latestRuntimesMap.entrySet().stream()
          .filter(entry -> validKeys.contains(entry.getKey()))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    return latestRuntimesMap;
  }

  private Document getGcpRuntimeSupportSchedule() {
    try {
      return Jsoup.connect(ConstantUtils.GCP_RUNTIME_SUPPORT_ENDPOINT).get();
    } catch (IOException ex) {
      log.error("ERROR Get GCP Runtime Support Schedule...", ex);
    }
    return null;
  }

  private Map<String, String> parseRuntimeTables(final Element table) {
    final Element headerRow = table.selectFirst("thead tr");

    if (headerRow == null) {
      log.error("GCP Runtime Tables, HeaderRow Not Found...");
      return Collections.emptyMap();
    }

    final Elements headers = headerRow.select("th");
    int runtimeIdIndex = getRuntimeIdIndex(headers);

    if (runtimeIdIndex == -1) {
      log.error("GCP Runtime Tables, Runtime ID Header Not Found...");
      return Collections.emptyMap();
    }

    final Elements rows = table.select("tbody tr");

    for (final Element row : rows) {
      final Element runtimeIdCell = row.select("td").get(runtimeIdIndex);
      final Element codeElement = runtimeIdCell.selectFirst("code");

      if (codeElement == null) {
        log.error("GCP Runtime Tables, Code Element Not Found...");
        return Collections.emptyMap();
      }

      final String runtimeId = codeElement.text();
      return parseRuntimeId(runtimeId);
    }

    return Collections.emptyMap();
  }

  private int getRuntimeIdIndex(final Elements headers) {
    int runtimeIdIndex = -1;
    for (int i = 0; i < headers.size(); i++) {
      if (headers.get(i).text().equalsIgnoreCase(ConstantUtils.GCP_RUNTIME_ID)) {
        runtimeIdIndex = i;
        break;
      }
    }
    return runtimeIdIndex;
  }

  private Map<String, String> parseRuntimeId(final String runtimeId) {
    final String alphaPart = runtimeId.replaceAll("[^A-Za-z]", "");
    final String numericPart = runtimeId.replaceAll("[^0-9]", "");
    return Map.of(alphaPart, numericPart);
  }
}
