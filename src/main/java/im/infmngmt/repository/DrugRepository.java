package im.infmngmt.repository;

import im.infmngmt.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugRepository extends JpaRepository<Drug, Long> {
}
