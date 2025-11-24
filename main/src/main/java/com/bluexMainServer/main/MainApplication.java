package com.bluexMainServer.main;

import com.bluexMainServer.main.Entity.Member;
import com.bluexMainServer.main.Repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}


	@Bean
	public CommandLineRunner initMembers(MemberRepository memberRepository) {
		return args -> {
			long count = memberRepository.count();
			if (count > 0) {
				System.out.println("이미 " + count + "명의 사용자가 있어서 생성 스킵함");
				return;
			}

			System.out.println("10만명 사용자 생성 시작...");

			int total = 100_000;
			int batchSize = 1_000;

			List<Member> buffer = new ArrayList<>(batchSize);
			Random random = new Random();

			for (int i = 1; i <= total; i++) {
				Member m = new Member();
				// 랜덤 이름 (User12345 이런 느낌)
				m.setName("User" + random.nextInt(1_000_000));

				buffer.add(m);
				// 배치 단위로 저장
				if (buffer.size() == batchSize) {
					memberRepository.saveAll(buffer);
					buffer.clear();
					System.out.println(i + "명 생성 완료");
				}
			}

			// 마지막에 남아 있는 것 저장
			if (!buffer.isEmpty()) {
				memberRepository.saveAll(buffer);
			}

			System.out.println("10만명 사용자 생성 완료!");
		};
	}
}
