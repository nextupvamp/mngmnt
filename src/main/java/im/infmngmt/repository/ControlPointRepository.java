package im.infmngmt.repository;

import im.infmngmt.entity.ControlPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ControlPointRepository extends JpaRepository<ControlPoint, Long> {
    List<ControlPoint> findBySchemeId(Long schemeId);
}