package dep.mgmt.util;

import dep.mgmt.model.ProcessSummaries;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProcessSummaryEmailUtils {

  public static synchronized String getProcessSummaryContent(
      ProcessSummaries.ProcessSummary processSummary) {
    List<ProcessSummaries.ProcessSummary.ProcessTask> allProcessedTasks =
        processSummary.getProcessTasks();
    List<ProcessSummaries.ProcessSummary.ProcessRepository> allProcessedRepositories =
        processSummary.getProcessRepositories();

    Set<String> pullRequestCreatedRepoNames =
        allProcessedRepositories.stream()
            .filter(ProcessSummaries.ProcessSummary.ProcessRepository::getPrCreated)
            .map(ProcessSummaries.ProcessSummary.ProcessRepository::getRepoName)
            .collect(Collectors.toSet());

    Set<String> pullRequestMergedRepoNames =
        allProcessedRepositories.stream()
            .filter(ProcessSummaries.ProcessSummary.ProcessRepository::getPrMerged)
            .map(ProcessSummaries.ProcessSummary.ProcessRepository::getRepoName)
            .collect(Collectors.toSet());

    List<ProcessSummaries.ProcessSummary.ProcessRepository> branchPullRequestCreatedMerged =
        allProcessedRepositories.stream()
            .filter(ProcessSummaries.ProcessSummary.ProcessRepository::getUpdateBranchCreated)
            .filter(repo -> pullRequestCreatedRepoNames.contains(repo.getRepoName()))
            .filter(repo -> pullRequestMergedRepoNames.contains(repo.getRepoName()))
            .toList();

    List<ProcessSummaries.ProcessSummary.ProcessRepository> branchCreatedPullRequestNotCreated =
        allProcessedRepositories.stream()
            .filter(ProcessSummaries.ProcessSummary.ProcessRepository::getUpdateBranchCreated)
            .filter(repo -> !pullRequestCreatedRepoNames.contains(repo.getRepoName()))
            .toList();

    List<ProcessSummaries.ProcessSummary.ProcessRepository> pullRequestCreatedNotMerged =
        allProcessedRepositories.stream()
            .filter(ProcessSummaries.ProcessSummary.ProcessRepository::getPrCreated)
            .filter(repo -> !pullRequestMergedRepoNames.contains(repo.getRepoName()))
            .toList();

    // Repositories with NO Updates
    //    List<ProcessSummaries.ProcessSummary.ProcessRepository> branchNotCreated =
    //        allProcessedRepositories.stream().filter(repo ->
    // !repo.getUpdateBranchCreated()).toList();

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
                  <td>Gradle Plugins Checked</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Gradle Dependencies Checked</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Python Packages Checked</td>
                  <td>%d</td>
                </tr>
                <tr>
                  <td>Node Dependencies Checked</td>
                  <td>%d</td>
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
                processSummary.getGradlePluginsChecked(),
                processSummary.getGradleDependenciesChecked(),
                processSummary.getPythonPackagesChecked(),
                processSummary.getNodeDependenciesChecked(),
                processSummary.getGradlePluginsToUpdate(),
                processSummary.getGradleDependenciesToUpdate(),
                processSummary.getPythonPackagesToUpdate(),
                processSummary.getNodeDependenciesToUpdate(),
                processSummary.getTotalPrCreatedCount(),
                processSummary.getTotalPrMergedCount(),
                processSummary.getTotalPrMergeErrorsCount()));

    html.append(
        """
                <br />
                <p style='font-size: 14px; font-weight: bold;%s'>Exception Caught in the Update Process: %s</p>
                <br />
              """
            .formatted(
                processSummary.getErrorsOrExceptions() ? " color: red;" : "",
                processSummary.getErrorsOrExceptions()));

    if (branchPullRequestCreatedMerged.isEmpty()) {
      html.append(
          """
                <br />
                <p style='font-size: 14px; font-weight: bold;'>Repositories with Update Branch Pull Request Created and Merged: N/A</p>
                <br />
              """);
    } else {
      html.append(
          """
                <br />
                <p style='font-size: 14px; font-weight: bold;'>Repositories with Update Branch Pull Request Created and Merged</p>
                <table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>
                  <tr>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Updated</th>
                    <th>PR Created</th>
                    <th>PR Number</th>
                    <th>PR Merged</th>
                  </tr>
              """);

      processedRepositoryTable(branchPullRequestCreatedMerged, html);
    }

    if (branchCreatedPullRequestNotCreated.isEmpty()) {
      html.append(
          """
                    <br />
                    <p style='font-size: 14px; font-weight: bold;'>Repositories with Update Branch but Pull Request NOT Created: N/A</p>
                    <br />
                  """);
    } else {
      html.append(
          """
                    <br />
                    <p style='font-size: 14px; font-weight: bold;'>Repositories with Update Branch but Pull Request NOT Created</p>
                    <table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>
                      <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Updated</th>
                        <th>PR Created</th>
                        <th>PR Number</th>
                        <th>PR Merged</th>
                      </tr>
                  """);

      processedRepositoryTable(branchCreatedPullRequestNotCreated, html);
    }

    if (pullRequestCreatedNotMerged.isEmpty()) {
      html.append(
          """
                    <br />
                    <p style='font-size: 14px; font-weight: bold;'>Repositories with Pull Request Created but NOT Merged: N/A</p>
                    <br />
                  """);
    } else {
      html.append(
          """
                    <br />
                    <p style='font-size: 14px; font-weight: bold;'>Repositories with Pull Request Created but NOT Merged</p>
                    <table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>
                      <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Updated</th>
                        <th>PR Created</th>
                        <th>PR Number</th>
                        <th>PR Merged</th>
                      </tr>
                  """);

      processedRepositoryTable(pullRequestCreatedNotMerged, html);
    }

    html.append(
        """
              <br />
              <p style='font-size: 14px; font-weight: bold;'>All Repositories</p>
              <table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>
                <tr>
                  <th>Name</th>
                  <th>Type</th>
                  <th>Updated</th>
                  <th>PR Created</th>
                  <th>PR Number</th>
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
                      <th>TimedOut?</th>
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
      html.append("<td>")
          .append(processedRepository.getUpdateBranchCreated() ? "Y" : "N")
          .append("</td>");
      html.append("<td>").append(processedRepository.getPrCreated() ? "Y" : "N").append("</td>");
      html.append("<td>").append(processedRepository.getPrNumber()).append("</td>");
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
      html.append("<td>").append(processedTask.getTimedOut() ? "Y" : "N").append("</td>");
      html.append("</tr>");
    }

    html.append(
        """
            </table>
          """);
  }
}
