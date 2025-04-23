package dep.mgmt.util;

import dep.mgmt.model.ProcessSummaries;
import java.util.Comparator;
import java.util.List;

public class ProcessSummaryEmailUtils {

  public static synchronized String getProcessSummaryContent(
      ProcessSummaries.ProcessSummary processSummary) {
    List<ProcessSummaries.ProcessSummary.ProcessTask> allProcessedTasks =
        processSummary.getProcessTasks();
    List<ProcessSummaries.ProcessSummary.ProcessRepository> allProcessedRepositories =
        processSummary.getProcessRepositories();
    List<ProcessSummaries.ProcessSummary.ProcessRepository> prCreatedAndMerged =
        processSummary.getProcessRepositories().stream()
            .filter(
                processedRepository ->
                    processedRepository.getPrCreated() && processedRepository.getPrMerged())
            .sorted(
                Comparator.comparing(
                    ProcessSummaries.ProcessSummary.ProcessRepository::getRepoName))
            .toList();
    List<ProcessSummaries.ProcessSummary.ProcessRepository> prCreatedNotMerged =
        processSummary.getProcessRepositories().stream()
            .filter(
                processedRepository ->
                    processedRepository.getPrCreated() && !processedRepository.getPrMerged())
            .sorted(
                Comparator.comparing(
                    ProcessSummaries.ProcessSummary.ProcessRepository::getRepoName))
            .toList();

    StringBuilder html = new StringBuilder();
    html.append(
        """
              <html>
                <head>
                  <style>
                    th {
                        border-bottom: 2px solid #9e9e9e;
                        position: sticky;
                        top: 0;
                        background-color: lightgrey;
                      }
                    td {
                      padding: 5px;
                      text-align: left;
                      border-bottom: 1px solid #9e9e9e;
                    }
                    td:first-child {
                       text-align: left;
                     }
                     td:not(:first-child) {
                       text-align: center;
                     }
                  </style>
                </head>
                <body>
              """);

    html.append(
        """
              <p style='font-size: 14px; font-weight: bold;'>App Dependency Update Process Summary: %s</p>
              <table cellpadding='10' cellspacing='0' style='font-size: 12px; border-collapse: collapse;'>
                <tr>
                  <th>Item</th>
                  <th>Value</th>
                </tr>
                <tr>
                  <td>Gradle Plugins To Update</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Gradle Dependencies To Update</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Python Packages To Update</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Node Dependencies To Update</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Total PR Created Count</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Total PR Merged Count</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Total PR Merge Errors Count</td>
                  <td>%d</td>
                </tr>
              </table>
            """
            .formatted(
                processSummary.getUpdateType(),
                processSummary.getGradlePluginsToUpdate(),
                processSummary.getGradleDependenciesToUpdate(),
                processSummary.getPythonPackagesToUpdate(),
                processSummary.getNodeDependenciesToUpdate(),
                processSummary.getTotalPrCreatedCount(),
                processSummary.getTotalPrMergeErrorsCount(),
                processSummary.getTotalPrMergedCount()));

    html.append(
        """
                <br />
                <p style='font-size: 14px; font-weight: bold;%s'>Exception Caught in the Update Process: %s</p>
                <br />
              """
            .formatted(
                processSummary.getErrorsOrExceptions() ? " color: red;" : "",
                processSummary.getErrorsOrExceptions()));

    if (prCreatedAndMerged.isEmpty()) {
      html.append(
          """
                <br />
                <p style='font-size: 14px; font-weight: bold;'>Repositories with PR Created and Merged: N/A</p>
                <br />
              """);
    } else {
      html.append(
          """
                <br />
                <p style='font-size: 14px; font-weight: bold;'>Repositories with PR Created and Merged</p>
                <table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>
                  <tr>
                    <th>Repository</th>
                    <th>Type</th>
                    <th>PR Created</th>
                    <th>PR Merged</th>
                  </tr>
              """);

      processedRepositoryTable(prCreatedAndMerged, html);
    }

    if (prCreatedNotMerged.isEmpty()) {
      html.append(
          """
                <br />
                <p style='font-size: 14px; font-weight: bold;'>Repositories with PR Created but NOT Merged: N/A</p>
                <br />
              """);
    } else {
      html.append(
          """
                <br />
                <p style='font-size: 14px; font-weight: bold;'>Repositories with PR Created but NOT Merged</p>
                <table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>
                  <tr>
                    <th>Repository</th>
                    <th>Type</th>
                    <th>PR Created</th>
                    <th>PR Merged</th>
                  </tr>
                  <br />
                    <p style='font-size: 10px; font-weight: bold;'>The system will attempt to merge these again in about an hour...</p>
                  <br />
              """);

      processedRepositoryTable(prCreatedNotMerged, html);
    }

    html.append(
        """
              <br />
              <p style='font-size: 14px; font-weight: bold;'>All Repositories</p>
              <table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>
                <tr>
                  <th>Repository</th>
                  <th>Type</th>
                  <th>PR Created</th>
                  <th>PR Merged</th>
                </tr>
            """);

    processedRepositoryTable(allProcessedRepositories, html);

    html.append(
        """
                  <br />
                  <p style='font-size: 14px; font-weight: bold;'>All Process Tasks</p>
                  <table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>
                    <tr>
                      <th>Queue</th>
                      <th>Task</th>
                      <th>Added</th>
                      <th>Started</th>
                      <th>Ended</th>
                    </tr>
                """);

    processedTaskTable(allProcessedTasks, html);

    html.append(
        """
          </body>
        </html>
        """);

    return html.toString();
  }

  private static void processedRepositoryTable(
      List<ProcessSummaries.ProcessSummary.ProcessRepository> processedRepositories,
      StringBuilder html) {
    for (ProcessSummaries.ProcessSummary.ProcessRepository processedRepository :
        processedRepositories) {
      html.append("<tr>");
      html.append("<td>").append(processedRepository.getRepoName()).append("</td>");
      html.append("<td>").append(processedRepository.getRepoType()).append("</td>");
      html.append("<td>").append(processedRepository.getPrCreated() ? "Y" : "N").append("</td>");
      html.append("<td>").append(processedRepository.getPrMerged() ? "Y" : "N").append("</td>");
      html.append("</tr>");
    }

    html.append(
        """
        </table>
      """);
  }

  private static void processedTaskTable(
      List<ProcessSummaries.ProcessSummary.ProcessTask> processedTasks, StringBuilder html) {
    for (ProcessSummaries.ProcessSummary.ProcessTask processedTask : processedTasks) {
      html.append("<tr>");
      html.append("<td>").append(processedTask.getQueueName()).append("</td>");
      html.append("<td>").append(processedTask.getTaskName()).append("</td>");
      html.append("<td>").append(processedTask.getAdded()).append("</td>");
      html.append("<td>").append(processedTask.getStarted()).append("</td>");
      html.append("<td>").append(processedTask.getEnded()).append("</td>");
      html.append("</tr>");
    }

    html.append(
        """
            </table>
          """);
  }
}
