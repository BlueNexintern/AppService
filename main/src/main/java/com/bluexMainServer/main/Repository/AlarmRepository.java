package com.bluexMainServer.main.Repository;

import com.bluexMainServer.main.Entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}