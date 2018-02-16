package com.jahnelgroup.jgbay.search.rsql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface RsqlTestEntityRepository extends JpaRepository<RSQLTestEntity, Long>, JpaSpecificationExecutor<RSQLTestEntity> {}