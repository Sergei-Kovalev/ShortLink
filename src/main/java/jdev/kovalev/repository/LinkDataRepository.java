package jdev.kovalev.repository;

import jdev.kovalev.entity.LinkData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkDataRepository extends JpaRepository<LinkData, UUID> {
    Optional<LinkData> findByAlias(String alias);
}
