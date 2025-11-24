package com.bluexMainServer.main.Repository;

import com.bluexMainServer.main.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}