package dep.mgmt.service;

import dep.mgmt.config.CacheConfig;
import dep.mgmt.config.MongoDbConfig;
import dep.mgmt.model.entity.ExcludedRepoEntity;
import dep.mgmt.repository.ExcludedRepoRepository;
import dep.mgmt.util.ConstantUtils;
import io.github.bibekaryal86.shdsvc.helpers.CommonUtilities;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcludedRepoService {

  private static final Logger log = LoggerFactory.getLogger(ExcludedRepoService.class);
  private final ExcludedRepoRepository excludedRepoRepository;

  public ExcludedRepoService() {
    this.excludedRepoRepository = new ExcludedRepoRepository(MongoDbConfig.getDatabase());
  }

  public Map<String, ExcludedRepoEntity> getExcludedReposMap() {
    Map<String, ExcludedRepoEntity> excludedReposMap = CacheConfig.getExcludedReposMap();
    if (CommonUtilities.isEmpty(excludedReposMap)) {
      final List<ExcludedRepoEntity> excludedRepos = excludedRepoRepository.findAll();
      log.info("Excluded Repos List: [ {} ]", excludedRepos);
      excludedReposMap =
          excludedRepos.stream()
              .collect(Collectors.toMap(ExcludedRepoEntity::getName, excludedRepo -> excludedRepo));
      CacheConfig.setExcludedReposMap(excludedReposMap);
    }
    return excludedReposMap;
  }

  public void insertExcludedRepo(final String name) {
    log.info("Insert Excluded Repo: [ {} ]", name);
    CacheConfig.resetExcludedReposMap();
    final ExcludedRepoEntity excludedRepoEntity = new ExcludedRepoEntity(name);
    excludedRepoRepository.insert(excludedRepoEntity);
  }

  public void deleteExcludedRepo(final String name) {
    log.info("Delete Excluded Repo: [ {} ]", name);
    CacheConfig.resetExcludedReposMap();
    excludedRepoRepository.delete(ConstantUtils.MONGODB_COLUMN_NAME, name);
  }

  public void deleteAllExcludedRepos() {
    log.info("Delete All Excluded Repos...");
    CacheConfig.resetExcludedReposMap();
    excludedRepoRepository.deleteAll();
  }
}
